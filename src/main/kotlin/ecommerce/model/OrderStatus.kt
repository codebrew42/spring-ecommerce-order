package ecommerce.model

enum class OrderStatus {
    PENDING, // Order created, but payment pending
    CONFIRMED, // Payment successful, order confirmed
    PROCESSING, // Order being prepared
    SHIPPED, // Order shipped
    DELIVERED, // Order delivered
    CANCELLED, // Order cancelled (payment failed or user cancelled)
    REFUNDED, // Order refunded
}
