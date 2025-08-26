package ecommerce.controller

import ecommerce.auth.AuthenticatedUser
import ecommerce.config.StripeClient
import ecommerce.dto.checkout.CheckoutResponse
import ecommerce.dto.order.CreateOrderRequest
import ecommerce.dto.order.toResponse
import ecommerce.dto.payment.PaymentIntentRequest
import ecommerce.model.Member
import ecommerce.model.PaymentMethod
import ecommerce.service.OrderService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/checkout")
@RestController
class CheckoutController(
    private val orderService: OrderService,
    private val stripeClient: StripeClient,
) {
    @PostMapping
    fun createOrder(
        @Valid @RequestBody request: CreateOrderRequest,
        @AuthenticatedUser member: Member,
    ): ResponseEntity<CheckoutResponse> {
        val order = orderService.createOrder(request, member.id)

        val paymentIntentRequest =
            PaymentIntentRequest(
                amount = order.totalAmount,
                currency = order.currency,
                paymentMethod = PaymentMethod.valueOf(request.paymentMethod.uppercase()),
            )

        val stripeResponse =
            stripeClient.createCheckoutSession(paymentIntentRequest)
                ?: throw IllegalStateException("Failed to create Stripe PaymentIntent")

        val checkoutResponse =
            stripeResponse.copy(
                orderId = order.id ?: 0L,
                orderStatus = order.orderStatus.name,
                items = order.toResponse().orderItems,
            )

        return ResponseEntity.ok(checkoutResponse)
    }

    @PostMapping("/confirm/{orderId}")
    fun confirmPayment(
        @PathVariable orderId: Long,
        @AuthenticatedUser member: Member,
    ): ResponseEntity<CheckoutResponse> {
        val order = orderService.getById(orderId)

        if (order.member.id != member.id) {
            throw IllegalArgumentException("Order does not belong to member")
        }

        orderService.confirmOrderPayment(orderId)

        val updatedOrder = orderService.getById(orderId)
        val response =
            CheckoutResponse(
                id = "pi_confirmed_${updatedOrder.id}",
                client_secret = null,
                amount = (updatedOrder.totalAmount * 100).toInt(),
                currency = updatedOrder.currency.name.lowercase(),
                status = "succeeded",
                payment_method = null,
                orderId = updatedOrder.id ?: 0L,
                orderStatus = updatedOrder.orderStatus.name,
                items = updatedOrder.toResponse().orderItems,
            )

        return ResponseEntity.ok(response)
    }
}
