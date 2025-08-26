package ecommerce.service

import ecommerce.exception.NotFoundException
import ecommerce.model.Order
import ecommerce.repository.OrderRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: OrderRepository,
) {
    fun findAllOrders(pageable: Pageable): Page<Order> {
        return orderRepository.findAll(pageable)
    }

    fun findOrderById(id: Long): Order {
        return orderRepository.findById(id).orElseThrow {
            NotFoundException("Order with id $id not found")
        }
    }

    fun findOrdersByMemberId(
        memberId: Long,
        pageable: Pageable,
    ): Page<Order> {
        return orderRepository.findByMemberId(memberId, pageable)
    }

    fun deleteById(id: Long) {
        orderRepository.findById(id).orElseThrow {
            NotFoundException("Order with id $id not found")
        }
        orderRepository.deleteById(id)
    }
}

/*
@Service
class OrderService(
//    private val orderRepository: OrderRepository,
//    private val memberRepository: MemberRepository,
//    private val productOptionRepository: ProductOptionRepository,
//    private val cartRepository: CartRepository,
//    private val cartItemRepository: CartItemRepository,
//    private val stripeClient: StripeClient,
) {
//    fun findAllOrders(pageable: Pageable): Page<Order> {
//        return orderRepository.findAll(pageable)
//    }
//    fun findOrderById(id: Long): Order {
//        return orderRepository.findByIdOrNull(id)
//            ?: throw NotFoundException("Order with id $id not found")
//    }
//
//    fun getOrdersByMemberId(memberId: Long, page: Int, size: Int): Map<String, Any> {
//        val pageable: Pageable = PageRequest.of(page, size)
//        val ordersPage: Page<Order> = orderRepository.findByMemberId(memberId, pageable)
//        return mapOf(
//            "orders" to ordersPage.content,
//            "totalPages" to ordersPage.totalPages,
//            "totalElements" to ordersPage.totalElements
//        )
//    }
//
//    fun deleteById(id: Long) {
//        orderRepository.deleteById(id)
//    }
//
// //    @Transactional
// //    fun createOrder(orderRequest: OrderRequest): Order {
// //
// //    }
//
//
// //    @Transactional
// //    fun placeOrder(
// //        memberId: Long,
// //        productOptionId: Long,
// //        quantity: Int,
// //        paymentIntentRequest: PaymentIntentRequest,
// //    ): Order {
// //        val member =
// //            memberRepository.findByIdOrNull(memberId)
// //                ?: throw NotFoundException("Member with id $memberId not found")
// //
// //        val productOption =
// //            productOptionRepository.findByIdOrNull(productOptionId)
// //                ?: throw NotFoundException("ProductOption with id $productOptionId not found")
// //
// //        if (productOption.quantity < quantity) {
// //            throw IllegalArgumentException("Insufficient stock. Available: ${productOption.quantity}, Requested: $quantity")
// //        }
// //
// //        try {
// //        } catch (e: Exception) {
// //            throw IllegalArgumentException("Payment failed: ${e.message}")
// //        }
// //        return order
// //    }
//
//    private fun extractSessionId(stripeResponse: String?): String {
//        require(stripeResponse.isNotEmpty()) { "Stripe Response can not be empty" }
//        stripeResponse.
//    }
// //
// //    private fun removeFromCartIfExists(
// //        memberId: Long,
// //        productOptionId: Long,
// //    ) {
// //    }
// //
//
//
}


 */
