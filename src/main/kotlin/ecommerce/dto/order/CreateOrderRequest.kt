package ecommerce.dto.order

import ecommerce.model.Currency
import jakarta.validation.constraints.NotEmpty

data class CreateOrderRequest(
    @field:NotEmpty(message = "Cart items are required")
    val cartItemIds: List<Long>,
    val currency: Currency = Currency.EUR,
    val paymentMethod: String = "pm_card_visa",
)

data class CreateOrderItemRequest(
    val productOptionId: Long,
    val quantity: Int,
)
