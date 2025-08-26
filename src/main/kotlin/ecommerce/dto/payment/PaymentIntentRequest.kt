package ecommerce.dto.payment

import ecommerce.model.Currency
import ecommerce.model.PaymentMethod

data class PaymentIntentRequest(
    val amount: Double,
    val currency: Currency,
    val paymentMethod: PaymentMethod?,
)
