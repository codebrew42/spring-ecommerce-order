package ecommerce.dto.payment

import ecommerce.model.Currency

data class PaymentStatusResponse(
    val paymentIntentId: String,
    val status: String,
    val amount: Double,
    val currency: Currency,
    val paymentMethod: String,
)
