package ecommerce.dto.order

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class OrderRequest(
    @field:NotNull(message = "Member ID is required")
    @field:Positive(message = "Member ID must be positive")
    val memberId: Long,
    @field:NotEmpty(message = "Order items cannot be empty")
    val orderItems: List<OrderItemRequest>,
)

data class OrderItemRequest(
    @field:NotNull(message = "Product option ID is required")
    @field:Positive(message = "Product option ID must be positive")
    val productOptionId: Long,
    @field:NotNull(message = "Quantity is required")
    @field:Min(value = 1, message = "Quantity must be at least 1")
    val quantity: Int,
)
