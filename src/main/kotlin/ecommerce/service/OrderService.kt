package ecommerce.service

import ecommerce.dto.order.CreateOrderItemRequest
import ecommerce.dto.order.CreateOrderRequest
import ecommerce.dto.order.OrderResponse
import ecommerce.dto.order.toResponse
import ecommerce.exception.NotFoundException
import ecommerce.model.Order
import ecommerce.model.OrderItem
import ecommerce.model.OrderStatus
import ecommerce.model.PaymentMethod
import ecommerce.model.PaymentStatus
import ecommerce.repository.CartItemRepository
import ecommerce.repository.CartRepository
import ecommerce.repository.MemberRepository
import ecommerce.repository.OrderRepository
import ecommerce.repository.ProductOptionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val memberRepository: MemberRepository,
    private val productOptionRepository: ProductOptionRepository,
    private val cartItemRepository: CartItemRepository,
    private val cartRepository: CartRepository,
    private val paymentService: PaymentService,
) {
    @Transactional
    fun createOrder(
        request: CreateOrderRequest,
        memberId: Long,
    ): Order {
        // Validate member exists
        val member =
            memberRepository.findById(memberId)
                .orElseThrow { NotFoundException("Member not found with id: $memberId") }

        // Validate order items
        validateOrderRequest(request)

        // Create order
        val order =
            Order(
                member = member,
                currency = request.currency,
                orderStatus = OrderStatus.PENDING,
                paymentStatus = PaymentStatus.PENDING,
            )

        // Create order items
        request.items.forEach { itemRequest ->
            val orderItem = createOrderItem(itemRequest, order)
            order.addOrderItem(orderItem)
        }

        // Calculate and set total amount
        order.calculateTotalAmount()

        return orderRepository.save(order)
    }

    @Transactional
    fun confirmOrder(
        orderId: Long,
        stripePaymentIntentId: String,
        stripeChargeId: String,
        paymentMethod: PaymentMethod,
    ): Order {
        val order =
            orderRepository.findById(orderId)
                .orElseThrow { NotFoundException("Order not found with id: $orderId") }

        // Confirm payment
        paymentService.confirmPayment(stripePaymentIntentId, stripeChargeId, paymentMethod)

        // Update order status
        order.orderStatus = OrderStatus.CONFIRMED
        order.paymentStatus = PaymentStatus.COMPLETED

        // Update product stock
        updateProductStock(order)

        // Clear user's cart items for ordered products
        clearCartItems(order)

        return orderRepository.save(order)
    }

    @Transactional
    fun cancelOrder(
        orderId: Long,
        reason: String,
    ): Order {
        val order =
            orderRepository.findById(orderId)
                .orElseThrow { NotFoundException("Order not found with id: $orderId") }

        if (!order.canBeCancelled()) {
            throw IllegalStateException("Order cannot be cancelled in current status: ${order.orderStatus}")
        }

        // Update order status
        order.orderStatus = OrderStatus.CANCELLED
        order.paymentStatus = PaymentStatus.CANCELLED

        // Fail payment if it exists
        order.payment?.let { payment ->
            paymentService.failPayment(payment.stripePaymentIntentId)
        }

        return orderRepository.save(order)
    }

    fun getOrder(orderId: Long): OrderResponse {
        val order =
            orderRepository.findById(orderId)
                .orElseThrow { NotFoundException("Order not found with id: $orderId") }

        return order.toResponse()
    }

    fun getOrdersByMember(
        memberId: Long,
        pageable: Pageable,
    ): Page<OrderResponse> {
        return orderRepository.findByMemberId(memberId, pageable)
            .map { it.toResponse() }
    }

    fun getOrdersByMemberAndStatus(
        memberId: Long,
        status: OrderStatus,
        pageable: Pageable,
    ): Page<OrderResponse> {
        return orderRepository.findByMemberIdAndOrderStatus(memberId, status, pageable)
            .map { it.toResponse() }
    }

    fun findAllOrders(pageable: Pageable): Page<OrderResponse> {
        return orderRepository.findAll(pageable)
            .map { it.toResponse() }
    }

    @Transactional
    fun deleteById(orderId: Long) {
        val order =
            orderRepository.findById(orderId)
                .orElseThrow { NotFoundException("Order not found with id: $orderId") }

        if (order.orderStatus == OrderStatus.CONFIRMED || order.paymentStatus == PaymentStatus.COMPLETED) {
            throw IllegalStateException("Cannot delete confirmed or completed orders")
        }

        orderRepository.deleteById(orderId)
    }

    // Private helper methods
    private fun validateOrderRequest(request: CreateOrderRequest) {
        if (request.items.isEmpty()) {
            throw IllegalArgumentException("Order must contain at least one item")
        }

        if (request.customerName.isBlank()) {
            throw IllegalArgumentException("Customer name cannot be blank")
        }

        if (request.customerEmail.isBlank()) {
            throw IllegalArgumentException("Customer email cannot be blank")
        }

        // Validate each order item
        request.items.forEach { itemRequest ->
            validateOrderItem(itemRequest)
        }
    }

    private fun validateOrderItem(itemRequest: CreateOrderItemRequest) {
        if (itemRequest.quantity <= 0) {
            throw IllegalArgumentException("Quantity must be positive")
        }

        // Check if product option exists and has sufficient stock
        val productOption =
            productOptionRepository.findById(itemRequest.productOptionId)
                .orElseThrow { NotFoundException("Product option not found with id: ${itemRequest.productOptionId}") }

        if (productOption.quantity < itemRequest.quantity) {
            throw IllegalArgumentException(
                "Insufficient stock for ${productOption.name}. Available: ${productOption.quantity}, Requested: ${itemRequest.quantity}",
            )
        }
    }

    private fun createOrderItem(
        itemRequest: CreateOrderItemRequest,
        order: Order,
    ): OrderItem {
        val productOption =
            productOptionRepository.findById(itemRequest.productOptionId)
                .orElseThrow { NotFoundException("Product option not found with id: ${itemRequest.productOptionId}") }

        return OrderItem.fromProductOption(productOption, itemRequest.quantity, order)
    }

    private fun updateProductStock(order: Order) {
        order.orderItems.forEach { orderItem ->
            val productOption = orderItem.productOption
            productOption.subtract(orderItem.quantity)
            productOptionRepository.save(productOption)
        }
    }

    private fun clearCartItems(order: Order) {
        val member = order.member
        val cart = cartRepository.findByMemberId(member.id ?: 0L)

        if (cart != null) {
            // Remove cart items that match ordered products
            order.orderItems.forEach { orderItem ->
                val cartItems =
                    cartItemRepository.findByCartIdAndProductOptionId(
                        cart.id ?: 0L,
                        orderItem.productOption.id ?: 0L,
                    )
                cartItems.forEach { cartItem ->
                    if (cartItem.quantity <= orderItem.quantity) {
                        // Remove entire cart item if ordered quantity >= cart quantity
                        cartItemRepository.delete(cartItem)
                    } else {
                        // Reduce cart item quantity
                        cartItem.quantity -= orderItem.quantity
                        cartItemRepository.save(cartItem)
                    }
                }
            }
        }
    }
}
