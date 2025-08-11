package ecommerce.repository

import ecommerce.model.Cart
import ecommerce.model.CartItem
import ecommerce.model.ProductOption
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface CartItemRepository : JpaRepository<CartItem, Long> {
    fun findByCartAndProductOption(
        cart: Cart,
        productOption: ProductOption,
    ): CartItem?

    @EntityGraph(attributePaths = ["productOption", "productOption.product"])
    fun findByCartId(cartId: Long): List<CartItem>

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    fun deleteByCartId(cartId: Long)
}
