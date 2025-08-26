package ecommerce.repository

import ecommerce.model.Payment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository : JpaRepository<Payment, Long> {
    fun findByStripePaymentIntentId(stripePaymentIntentId: String): Payment?

    fun findByOrderId(orderId: Long): Payment?
}
