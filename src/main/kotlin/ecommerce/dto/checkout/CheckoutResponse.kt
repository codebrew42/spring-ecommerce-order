package ecommerce.dto.checkout

import ecommerce.dto.order.OrderItemResponse

data class CheckoutResponse(
    val id: String,
    val client_secret: String?,
    val amount: Int,
    val currency: String,
    val status: String,
    val payment_method: String?,
    val orderId: Long,
    val orderStatus: String,
    val items: List<OrderItemResponse>,
)
