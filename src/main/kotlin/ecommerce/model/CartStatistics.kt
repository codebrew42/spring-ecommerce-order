package ecommerce.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.Objects

@Entity
@Table(
    name = "cart_statistics",
    indexes = [
        Index(name = "idx_cart_statistics_cart_id", columnList = "cart_id"),
        Index(name = "idx_cart_statistics_product_option_id", columnList = "product_option_id"),
        Index(name = "idx_cart_statistics_added_at", columnList = "added_at"),
    ],
)
class CartStatistics(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "cart_item_id",
        nullable = false,
        foreignKey = ForeignKey(foreignKeyDefinition = "FOREIGN KEY (cart_item_id) REFERENCES cart_items(id) ON DELETE CASCADE"),
    )
    val cartItem: CartItem,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    val cart: Cart,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_id", nullable = false)
    val productOption: ProductOption,
    @Column(name = "quantity", nullable = false)
    val quantity: Int,
    @Column(name = "added_at", nullable = false)
    val addedAt: LocalDateTime = LocalDateTime.now(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CartStatistics) return false

        if (id != null && other.id != null) {
            return id == other.id
        }

        return cartItem == other.cartItem && addedAt == other.addedAt
    }

    override fun hashCode(): Int {
        return Objects.hash(cartItem.id, addedAt)
    }
}
