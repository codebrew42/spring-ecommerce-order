package ecommerce.repository

import ecommerce.model.Cart
import ecommerce.model.CartItem
import ecommerce.model.ProductOption
import org.springframework.data.jpa.repository.JpaRepository

interface CartItemRepository : JpaRepository<CartItem, Long> {
    fun findByCartAndProductOption(
        cart: Cart,
        productOption: ProductOption,
    ): CartItem?

    fun findByCartId(cartId: Long): List<CartItem>

    fun deleteByCartId(cartId: Long)
}
