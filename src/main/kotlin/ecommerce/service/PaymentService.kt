package ecommerce.service

import ecommerce.config.StripeClient
import ecommerce.dto.payment.CreatePaymentIntentRequest
import ecommerce.dto.payment.CreatePaymentIntentResponse
import ecommerce.dto.payment.PaymentIntentRequest
import ecommerce.dto.payment.PaymentStatusResponse
import ecommerce.exception.NotFoundException
import ecommerce.model.Order
import ecommerce.model.Payment
import ecommerce.model.PaymentMethod
import ecommerce.model.PaymentStatus
import ecommerce.repository.PaymentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val stripeClient: StripeClient,
    private val paymentRepository: PaymentRepository,
) {
    @Transactional
    fun createPaymentIntent(
        request: CreatePaymentIntentRequest,
        order: Order,
    ): CreatePaymentIntentResponse {
        val paymentRequest =
            PaymentIntentRequest(
                amount = order.totalAmount,
                currency = order.currency,
                paymentMethod = PaymentMethod.CARD,
            )

        // Create payment intent with Stripe
        val stripeResponse =
            stripeClient.createCheckoutSession(paymentRequest)
                ?: throw RuntimeException("Failed to create payment intent with Stripe")

        // Parse Stripe response to extract payment intent ID and client secret
        // This would need proper JSON parsing based on actual Stripe response
        val paymentIntentId = "pi_${System.currentTimeMillis()}" // Placeholder
        val clientSecret = "${paymentIntentId}_secret_${System.currentTimeMillis()}" // Placeholder

        // Create Payment entity
        val payment =
            Payment(
                order = order,
                stripePaymentIntentId = paymentIntentId,
                amount = order.totalAmount,
                currency = order.currency,
                status = PaymentStatus.PENDING,
            )

        paymentRepository.save(payment)

        return CreatePaymentIntentResponse(
            clientSecret = clientSecret,
            paymentIntentId = paymentIntentId,
        )
    }

    @Transactional
    fun confirmPayment(
        stripePaymentIntentId: String,
        stripeChargeId: String,
        paymentMethod: PaymentMethod,
    ): Payment {
        val payment =
            paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
                ?: throw NotFoundException("Payment not found for payment intent: $stripePaymentIntentId")

        // Update payment status
        payment.status = PaymentStatus.COMPLETED
        payment.stripeChargeId = stripeChargeId
        payment.paymentMethod = paymentMethod
        payment.failureReason = null

        return paymentRepository.save(payment)
    }

    @Transactional
    fun failPayment(
        stripePaymentIntentId: String,
        failureReason: String,
    ): Payment {
        val payment =
            paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
                ?: throw NotFoundException("Payment not found for payment intent: $stripePaymentIntentId")

        // Update payment status
        payment.status = PaymentStatus.FAILED
        payment.failureReason = failureReason

        return paymentRepository.save(payment)
    }

    @Transactional
    fun refundPayment(stripePaymentIntentId: String): Payment {
        val payment =
            paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
                ?: throw NotFoundException("Payment not found for payment intent: $stripePaymentIntentId")

        if (!payment.isCompleted()) {
            throw IllegalStateException("Cannot refund payment that is not completed")
        }

        // Here you would call Stripe refund API
        // stripeClient.refundPayment(payment.stripeChargeId)

        payment.status = PaymentStatus.REFUNDED

        return paymentRepository.save(payment)
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
            paymentMethod = payment.paymentMethod,
            failureReason = payment.failureReason,
        )
    }

    fun findByOrderId(orderId: Long): Payment? {
        return paymentRepository.findByOrderId(orderId)
    }
}
