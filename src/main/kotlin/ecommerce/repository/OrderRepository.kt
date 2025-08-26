package ecommerce.repository

import ecommerce.model.Order
import ecommerce.model.OrderStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : JpaRepository<Order, Long> {
    fun findByMemberId(
        memberId: Long,
        pageable: Pageable,
    ): Page<Order>

    fun findByMemberIdAndOrderStatus(
        memberId: Long,
        orderStatus: OrderStatus,
        pageable: Pageable,
    ): Page<Order>

    fun findByStripeCheckoutSessionId(stripeCheckoutSessionId: String): Order?

    fun findByStripePaymentIntentId(stripePaymentIntentId: String): Order?

    @Query(
        """
        SELECT o FROM Order o 
        WHERE o.member.id = :memberId 
        ORDER BY o.createdAt DESC
    """,
    )
    fun findByMemberIdOrderByCreatedAtDesc(memberId: Long): List<Order>
}
