package ecommerce.dto.payment

import ecommerce.model.Currency
import ecommerce.model.PaymentMethod

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
    val paymentMethod: PaymentMethod?,
)
