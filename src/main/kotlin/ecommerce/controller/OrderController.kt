package ecommerce.controller

import ecommerce.dto.order.OrderResponse
import ecommerce.service.OrderService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/orders")
@RestController
class OrderController(private val orderService: OrderService) {
    @GetMapping("/{id}")
    fun getOrderById(
        @PathVariable id: Long,
    ): OrderResponse = orderService.getOrder(id)

    @GetMapping()
    fun getOrders(pageable: Pageable): Page<OrderResponse> = orderService.findAllOrders(pageable)

    @GetMapping("/member/{memberId}")
    fun getOrdersByMemberId(
        @PathVariable memberId: Long,
        pageable: Pageable,
    ): ResponseEntity<Page<OrderResponse>> {
        val result = orderService.getOrdersByMember(memberId, pageable)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/{id}")
    fun deleteOrderById(
        @PathVariable id: Long,
    ): ResponseEntity<Unit> {
        orderService.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}
