package ecommerce.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "carts")
class Cart(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = true)
    val member: Member? = null,
    @OneToMany(mappedBy = "cart", cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    val cartItem: MutableList<CartItem> = mutableListOf(),
    @Column(name = "quantity", nullable = false)
    var quantity: Int = 0,
    @CreationTimestamp
    var createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    var updatedAt: LocalDateTime? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    fun addQuantity(additionalQuantity: Int) {
        this.quantity += additionalQuantity
        updateTimestamp()
    }

    fun updateTimestamp() {
        this.updatedAt = LocalDateTime.now()
    }

    fun updateQuantity(newQuantity: Int) {
        this.quantity = newQuantity
        updateTimestamp()
    }

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
        return "Cart(id=$id, memberId=${member?.id}, cartItemCount=${cartItem.size}, quantity=$quantity)"
    }
}
