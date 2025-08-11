package ecommerce.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.Objects

@Entity
@Table(
    name = "cart_items",
    indexes = [
        Index(name = "idx_cart_item_cart_id", columnList = "cart_id"),
        Index(name = "idx_cart_item_product_option_id", columnList = "product_option_id"),
    ],
)
class CartItem(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = true)
    var cart: Cart,
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_option_id", nullable = true)
    var productOption: ProductOption,
    @Column(name = "quantity", nullable = true)
    var quantity: Int,
    @Column(name = "updatedAt", nullable = false)
    var itemAddedAt: LocalDateTime? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    fun modify(
        cart: Cart?,
        productOption: ProductOption?,
        quantity: Int,
        itemAddedAt: LocalDateTime?,
    ) {
        if (cart != null) {
            this.cart = cart
        }
        if (productOption != null) {
            this.productOption = productOption
        }
        if (quantity != null) {
            this.quantity = quantity
        }
        if (itemAddedAt != null) {
            this.itemAddedAt = itemAddedAt
        }
    }

    companion object {
        fun validateQuantity(requestedQuantity: Int) {
            if (requestedQuantity <= 0) {
                throw IllegalArgumentException("Cart item quantity must be greater than 0")
            }

            if (requestedQuantity > 999) {
                throw IllegalArgumentException("Maximum quantity per item is 999")
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CartItem) return false

        if (id == null || other.id == null) {
            return cart == other.cart && productOption == other.productOption
        }

        return id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(cart.id, productOption.id)
    }

    override fun toString(): String {
        return "CartItem(id=$id, cartId=${cart.id}, productOptionId=${productOption.id}, quantity=$quantity)"
    }
}
