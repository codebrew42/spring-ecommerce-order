package ecommerce.dto.order

import ecommerce.model.Currency

data class CreateOrderRequest(
    val customerName: String,
    val customerEmail: String,
    val currency: Currency,
    val items: List<CreateOrderItemRequest>,
)

data class CreateOrderItemRequest(
    val productOptionId: Long,
    val quantity: Int,
)
