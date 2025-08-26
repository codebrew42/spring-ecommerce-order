package ecommerce.dto.payment

import ecommerce.model.Currency

data class PaymentRequest(
    val amount: Double,
    val currency: Currency,
    val paymentMethod: String,
)

data class CreatePaymentIntentRequest(
    val orderId: Long,
)

data class CreatePaymentIntentResponse(
    val clientSecret: String,
    val paymentIntentId: String,
)

data class PaymentStatusResponse(
    val paymentIntentId: String,
    val status: String,
    val amount: Double,
    val currency: Currency,
    val paymentMethod: String?,
    val failureReason: String?,
)
