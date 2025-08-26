package ecommerce.dto.cart

import jakarta.validation.constraints.Min

data class UpdateQuantityRequest(
    @field:Min(value = 0, message = "Quantity must be at least 0")
    val quantity: Int,
)
