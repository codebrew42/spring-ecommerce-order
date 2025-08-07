package ecommerce.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "product_options")
class ProductOption(
    @Column(name = "name", nullable = false)
    var name: String,
    @Column(name = "quantity", nullable = false)
    var quantity: Int,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    fun subtract(quantity: Int) {
        if (quantity < 1) throw IllegalArgumentException("Quantity must be greater than 0")
        if (quantity > this.quantity) throw IllegalArgumentException("Quantity can not be greater than stock of product quantity")
        this.quantity -= quantity
    }

    override fun toString(): String {
        return "Product Option(id=$id, name=$name, quantity=$quantity, product=$product)"
    }
}
