package ecommerce.dto.order

import ecommerce.model.Currency
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class CreateOrderRequest(
    @field:NotEmpty(message = "Cart items are required")
    val cartItemIds: List<Long>,
    @field:NotBlank(message = "Payment method is required")
    val paymentMethod: String = "pm_card_visa",
    val currency: Currency = Currency.EUR,
)

data class CreateOrderItemRequest(
    val productOptionId: Long,
    val quantity: Int,
)
