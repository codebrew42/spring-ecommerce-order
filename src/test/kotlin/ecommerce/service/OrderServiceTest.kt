package ecommerce.service

import ecommerce.dto.order.CreateOrderRequest
import ecommerce.exception.NotFoundException
import ecommerce.model.Cart
import ecommerce.model.CartItem
import ecommerce.model.Currency
import ecommerce.model.Member
import ecommerce.model.Order
import ecommerce.model.OrderStatus
import ecommerce.model.PaymentStatus
import ecommerce.model.Product
import ecommerce.model.ProductOption
import ecommerce.model.Role
import ecommerce.repository.CartItemRepository
import ecommerce.repository.CartRepository
import ecommerce.repository.MemberRepository
import ecommerce.repository.OrderRepository
import ecommerce.repository.ProductOptionRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class OrderServiceTest {

    @Mock
    private lateinit var orderRepository: OrderRepository

    @Mock
    private lateinit var memberRepository: MemberRepository

    @Mock
    private lateinit var productOptionRepository: ProductOptionRepository

    @Mock
    private lateinit var cartItemRepository: CartItemRepository

    @Mock
    private lateinit var cartRepository: CartRepository

    private lateinit var orderService: OrderService

    private lateinit var member: Member
    private lateinit var product: Product
    private lateinit var productOption: ProductOption
    private lateinit var cart: Cart
    private lateinit var cartItem: CartItem
    private lateinit var order: Order

    @BeforeEach
    fun setUp() {
        orderService = OrderService(
            orderRepository,
            memberRepository,
            productOptionRepository,
            cartItemRepository,
            cartRepository
        )

        member = Member("test@test.com", "password", "Test User", Role.USER, id = 1L)
        product = Product("Test Product", 29.99, 10, "test.jpg", id = 1L)
        productOption = ProductOption("Size M", 10, product, 29.99, id = 1L)
        cart = Cart(member, id = 1L)
        cartItem = CartItem(cart, productOption, 2, id = 1L)
        order = Order(member, currency = Currency.EUR, id = 1L)
    }

    @Test
    fun `should create order successfully with valid cart items`() {
        // Given
        val request = CreateOrderRequest(
            cartItemIds = listOf(1L),
            paymentMethod = "pm_card_visa",
            currency = Currency.EUR
        )

        `when`(memberRepository.findById(1L)).thenReturn(Optional.of(member))
        `when`(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem))
        `when`(orderRepository.save(any())).thenReturn(order)

        // When
        val result = orderService.createOrder(request, 1L)

        // Then
        assertEquals(member, result.member)
        assertEquals(Currency.EUR, result.currency)
        assertEquals(OrderStatus.PENDING, result.orderStatus)
        assertEquals(PaymentStatus.PENDING, result.paymentStatus)
        verify(orderRepository).save(any())
    }

    @Test
    fun `should throw NotFoundException when member not found`() {
        // Given
        val request = CreateOrderRequest(
            cartItemIds = listOf(1L),
            paymentMethod = "pm_card_visa",
            currency = Currency.EUR
        )

        `when`(memberRepository.findById(1L)).thenReturn(Optional.empty())

        // When & Then
        assertThrows(NotFoundException::class.java) {
            orderService.createOrder(request, 1L)
        }
    }

    @Test
    fun `should throw IllegalArgumentException when cart items empty`() {
        // Given
        val request = CreateOrderRequest(
            cartItemIds = emptyList(),
            paymentMethod = "pm_card_visa",
            currency = Currency.EUR
        )

        `when`(memberRepository.findById(1L)).thenReturn(Optional.of(member))

        // When & Then
        assertThrows(IllegalArgumentException::class.java) {
            orderService.createOrder(request, 1L)
        }
    }

    @Test
    fun `should throw NotFoundException when cart item not found`() {
        // Given
        val request = CreateOrderRequest(
            cartItemIds = listOf(999L),
            paymentMethod = "pm_card_visa",
            currency = Currency.EUR
        )

        `when`(memberRepository.findById(1L)).thenReturn(Optional.of(member))
        `when`(cartItemRepository.findById(999L)).thenReturn(Optional.empty())

        // When & Then
        assertThrows(NotFoundException::class.java) {
            orderService.createOrder(request, 1L)
        }
    }

    @Test
    fun `should throw IllegalArgumentException when insufficient stock`() {
        // Given
        val lowStockProductOption = ProductOption("Size M", 1, product, 29.99, id = 1L)
        val cartItemWithHighQuantity = CartItem(cart, lowStockProductOption, 5, id = 1L)

        val request = CreateOrderRequest(
            cartItemIds = listOf(1L),
            paymentMethod = "pm_card_visa",
            currency = Currency.EUR
        )

        `when`(memberRepository.findById(1L)).thenReturn(Optional.of(member))
        `when`(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItemWithHighQuantity))

        // When & Then
        assertThrows(IllegalArgumentException::class.java) {
            orderService.createOrder(request, 1L)
        }
    }

    @Test
    fun `should confirm order payment successfully`() {
        // Given
        order.orderStatus = OrderStatus.PENDING
        order.paymentStatus = PaymentStatus.PENDING

        `when`(orderRepository.findById(1L)).thenReturn(Optional.of(order))
        `when`(orderRepository.save(any())).thenReturn(order)
        `when`(cartRepository.findByMemberId(1L)).thenReturn(cart)

        // When
        val result = orderService.confirmOrderPayment(1L)

        // Then
        assertEquals(OrderStatus.CONFIRMED, result.orderStatus)
        assertEquals(PaymentStatus.COMPLETED, result.paymentStatus)
        verify(orderRepository).save(order)
    }

    @Test
    fun `should throw IllegalStateException when payment already confirmed`() {
        // Given
        order.paymentStatus = PaymentStatus.COMPLETED

        `when`(orderRepository.findById(1L)).thenReturn(Optional.of(order))

        // When & Then
        assertThrows(IllegalStateException::class.java) {
            orderService.confirmOrderPayment(1L)
        }
    }
}