package ecommerce.dto.payment

data class PaymentIntentRequest(
    val amount: Int,
    val currency: String,
    val paymentMethod: String,
)
