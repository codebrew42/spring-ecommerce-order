package ecommerce.service

import ecommerce.config.StripeClient
import ecommerce.dto.checkout.CheckoutResponse
import ecommerce.dto.payment.PaymentIntentRequest
import ecommerce.dto.payment.PaymentStatusResponse
import ecommerce.exception.FailedPaymentException
import ecommerce.exception.NotFoundException
import ecommerce.model.Payment
import ecommerce.repository.PaymentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val stripeClient: StripeClient,
    private val paymentRepository: PaymentRepository,
) {
    @Transactional
    fun createPaymentIntent(request: PaymentIntentRequest): CheckoutResponse {
        return stripeClient.createCheckoutSession(request)
            ?: throw FailedPaymentException("Failed to create payment intent with Stripe")
    }

    fun getPaymentStatus(stripePaymentIntentId: String): PaymentStatusResponse {
        val payment =
            paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
                ?: throw NotFoundException("Payment not found for payment intent: $stripePaymentIntentId")

        return PaymentStatusResponse(
            paymentIntentId = payment.stripePaymentIntentId,
            status = payment.status.name,
            amount = payment.amount,
            currency = payment.currency,
            paymentMethod = payment.paymentMethod.toString(),
        )
    }

    fun findByOrderId(orderId: Long): Payment? {
        return paymentRepository.findByOrderId(orderId)
    }
}
