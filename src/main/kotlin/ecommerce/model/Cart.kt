package ecommerce.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(
    name = "carts",
    indexes = [
        Index(name = "idx_cart_member_id", columnList = "member_id"),
    ],
)
class Cart(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = true)
    val member: Member? = null,
    @OneToMany(mappedBy = "cart", cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    @BatchSize(size = 20)
    @JsonIgnore
    val cartItem: MutableList<CartItem> = mutableListOf(),
    @Column(name = "quantity", nullable = false)
    var quantity: Int = 0,
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cart) return false

        if (id != null && other.id != null) {
            return id == other.id
        }

        return member == other.member
    }

    override fun hashCode(): Int {
        return member?.hashCode() ?: 0
    }

    override fun toString(): String {
        //TODO: Check id
        return "Cart(id=$id, memberId=${member?.id}, cartItemCount=${cartItem.size}, quantity=$quantity)"
    }
}
