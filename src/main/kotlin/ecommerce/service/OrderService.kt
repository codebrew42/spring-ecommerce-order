package ecommerce.service

import ecommerce.config.StripeClient
import ecommerce.dto.order.OrderRequest
import ecommerce.dto.payment.PaymentIntentRequest
import ecommerce.exception.NotFoundException
import ecommerce.model.*
import ecommerce.repository.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val memberRepository: MemberRepository,
    private val productOptionRepository: ProductOptionRepository,
    private val cartRepository: CartRepository,
    private val cartItemRepository: CartItemRepository,
    private val stripeClient: StripeClient,
) {
    fun findAllOrders(pageable: Pageable): Page<Order> {
        return orderRepository.findAll(pageable)
    }
    fun findOrderById(id: Long): Order {
        return orderRepository.findByIdOrNull(id)
            ?: throw NotFoundException("Order with id $id not found")
    }

    fun getOrdersPageForMember(memberId: Long, page: Int, size: Int): Map<String, Any> {
        val pageable: Pageable = PageRequest.of(page, size)
        val ordersPage: Page<Order> = orderRepository.findByMemberId(memberId, pageable)
        return mapOf(
            "orders" to ordersPage.content,
            "totalPages" to ordersPage.totalPages,
            "totalElements" to ordersPage.totalElements
        )
    }

    fun deleteById(id: Long) {
        orderRepository.deleteById(id)
    }

    @Transactional
    fun createOrder(orderRequest: OrderRequest): Order {
        val member = memberRepository.findByIdOrNull(orderRequest.memberId)
            ?: throw NotFoundException("Member not found")

        val cart = cartRepository.findByMemberId(orderRequest.memberId)
            ?: throw NotFoundException("Cart not found for member")

        val totalAmount = cart.g

        // 4. Call Stripe to process payment. This is a crucial step!
        // This is where you call stripeClient.createPaymentIntent(...)
        val stripeCheckoutSessionId = stripeClient.createPaymentIntent(totalAmount)
            ?: throw IllegalArgumentException("Payment failed")

        // 5. Create the Order entity (all data is ready)
        val order = Order(
            member = member,
            stripeCheckoutSessionId = stripeCheckoutSessionId,
            orderStatus = OrderStatus.COMPLETED, // You'll need this enum
            totalAmount = totalAmount
        )
        val savedOrder = orderRepository.save(order)

        // 6. Create OrderItem entities and update stock
        cart.cartItems.forEach { cartItem ->
            val productOption = cartItem.productOption
            productOption.stockQuantity -= cartItem.quantity // Decrement stock

            // Create and save OrderItem (You'll need an OrderItem entity and repository)
            val orderItem = OrderItem(
                order = savedOrder,
                productOption = productOption,
                quantity = cartItem.quantity
            )
            // orderItemRepository.save(orderItem)
        }

        // 7. Clear the cart
        cartItemRepository.deleteAll(cart.cartItems)

        return savedOrder
    }


//    @Transactional
//    fun placeOrder(
//        memberId: Long,
//        productOptionId: Long,
//        quantity: Int,
//        paymentIntentRequest: PaymentIntentRequest,
//    ): Order {
//        val member =
//            memberRepository.findByIdOrNull(memberId)
//                ?: throw NotFoundException("Member with id $memberId not found")
//
//        val productOption =
//            productOptionRepository.findByIdOrNull(productOptionId)
//                ?: throw NotFoundException("ProductOption with id $productOptionId not found")
//
//        if (productOption.quantity < quantity) {
//            throw IllegalArgumentException("Insufficient stock. Available: ${productOption.quantity}, Requested: $quantity")
//        }
//
//        try {
//        } catch (e: Exception) {
//            throw IllegalArgumentException("Payment failed: ${e.message}")
//        }
//        return order
//    }

    private fun extractSessionId(stripeResponse: String?): String {
        require(stripeResponse.isNotEmpty()) { "Stripe Response can not be empty" }
        stripeResponse.
    }
//
//    private fun removeFromCartIfExists(
//        memberId: Long,
//        productOptionId: Long,
//    ) {
//    }
//


}
