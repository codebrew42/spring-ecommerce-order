package ecommerce.model

enum class OrderStatus {
    PENDING, // Order created, but payment pending
    CONFIRMED, // Payment successful, order confirmed
}
