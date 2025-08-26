package ecommerce.service

import ecommerce.dto.cart.AddToCartRequest
import ecommerce.model.Cart
import ecommerce.model.Member
import ecommerce.model.Product
import ecommerce.model.ProductOption
import ecommerce.repository.CartItemRepository
import ecommerce.repository.CartRepository
import ecommerce.repository.MemberRepository
import ecommerce.repository.ProductOptionRepository
import ecommerce.repository.ProductRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CartItemServiceIntegrationTest {
    @Autowired
    private lateinit var cartItemService: CartItemService

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var cartRepository: CartRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var productOptionRepository: ProductOptionRepository

    @Autowired
    private lateinit var cartItemRepository: CartItemRepository

    private lateinit var testMember: Member
    private lateinit var testCart: Cart
    private lateinit var testProduct: Product
    private lateinit var testProductOption: ProductOption

    @BeforeEach
    fun setUp() {
        testMember = memberRepository.findById(1L).orElseThrow { RuntimeException("Test member not found") }
        testCart = cartRepository.findById(1L).orElseThrow { RuntimeException("Test cart not found") }
        testProduct = productRepository.findById(1L).orElseThrow { RuntimeException("Test product not found") }
        testProductOption = productOptionRepository.findById(1L).orElseThrow { RuntimeException("Test product option not found") }
    }

    @Test
    fun `addCartItem should create new cart item successfully`() {
        val request =
            AddToCartRequest(
                productOptionId = testProductOption.id ?: 1L,
                newProductOptionQuantity = 3,
                cartItemId = 0L,
                cartId = testCart.id ?: 1L,
            )

        val result = cartItemService.addCartItem(request, testCart.id ?: 1L)

        assertThat(result).isNotNull()
        assertThat(result.id).isNotNull()
        assertThat(result.cart.id).isEqualTo(testCart.id)
        assertThat(result.productOption.id).isEqualTo(testProductOption.id)
        assertThat(result.quantity).isEqualTo(3)

        val savedItem = cartItemRepository.findById(result.id ?: 0L).orElse(null)
        assertThat(savedItem).isNotNull()
        assertThat(savedItem.quantity).isEqualTo(3)
    }

    @Test
    fun `addCartItem should create cart item successfully`() {
        val request =
            AddToCartRequest(
                productOptionId = testProductOption.id ?: 1L,
                newProductOptionQuantity = 2,
                cartItemId = 0L,
                cartId = testCart.id ?: 1L,
            )
        val result = cartItemService.addCartItem(request, testCart.id ?: 1L)

        assertThat(result.quantity).isEqualTo(2)
        assertThat(result.cart.id).isEqualTo(testCart.id)
        assertThat(result.productOption.id).isEqualTo(testProductOption.id)
    }

    @Test
    fun `deleteCartItemById should remove cart item successfully`() {
        val request =
            AddToCartRequest(
                productOptionId = testProductOption.id ?: 1L,
                newProductOptionQuantity = 2,
                cartItemId = 0L,
                cartId = testCart.id ?: 1L,
            )
        val createdItem = cartItemService.addCartItem(request, testCart.id ?: 1L)

        cartItemService.deleteCartItemById(createdItem.id ?: 0L, testCart.id ?: 1L)

        val deletedItem = cartItemRepository.findById(createdItem.id ?: 0L).orElse(null)
        assertThat(deletedItem).isNull()
    }

    @Test
    fun `deleteAllCartItemsByCartId should remove all cart items`() {
        val testCart2 = cartRepository.findById(2L).orElseThrow { RuntimeException("Test cart 2 not found") }
        val cart2Id = testCart2.id ?: 2L
        val productOptionId = testProductOption.id ?: 1L

        // Clean up existing data
        cartItemService.deleteAllCartItemsByCartId(cart2Id)

        // Add some cart items to the clean cart
        val request1 = AddToCartRequest(productOptionId, 1, 0L, cart2Id)
        val request2 = AddToCartRequest(productOptionId, 2, 0L, cart2Id)
        cartItemService.addCartItem(request1, cart2Id)
        cartItemService.addCartItem(request2, cart2Id)

        // Verify items were added
        assertThat(cartItemRepository.findByCartId(cart2Id)).hasSize(2)

        // Test the deletion
        cartItemService.deleteAllCartItemsByCartId(cart2Id)

        assertThat(cartItemRepository.findByCartId(cart2Id)).isEmpty()
    }
}
