package ecommerce.controller

import ecommerce.model.Order
import ecommerce.service.OrderService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/orders")
@RestController
class OrderController(private val orderService: OrderService) {
    @GetMapping()
    fun getOrders(pageable: Pageable): Page<Order> = orderService.findAllOrders(pageable)

    @GetMapping("{id}")
    fun getOrderById(
        @PathVariable id: Long,
    ): Order = orderService.findOrderById(id)

    @GetMapping("/member/{memberId}")
    fun getOrdersByMemberId(
        @PathVariable memberId: Long,
        pageable: Pageable,
    ): ResponseEntity<Page<Order>> {
        val result = orderService.findOrdersByMemberId(memberId, pageable)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/{id}")
    fun deleteOrderById(
        @PathVariable id: Long,
    ): ResponseEntity<Unit> {
        orderService.deleteById(id)
        return ResponseEntity.noContent().build()
    }

//
//    @PostMapping
//    fun createOrder(
//        @Valid @RequestBody orderRequest: OrderRequest,
//    ): ResponseEntity<Order> {
//        val savedOrder = orderService.createOrder(orderRequest)
//
//        val location: URI =
//            ServletUriComponentsBuilder
//                .fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(savedOrder.id)
//                .toUri()
//
//        return ResponseEntity.created(location).body(savedOrder)
//    }
//
//    @PostMapping("/place-order")
//    fun placeOrder(
//        // @AuthenticationPrincipal member: Member, // Or some other way to get the logged-in user
//        orderRequest: OrderRequest,
//    ): ResponseEntity<Order> {
//        val savedOrder = orderService.createOrder(orderRequest)
//
//        val location: URI =
//            ServletUriComponentsBuilder
//                .fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(savedOrder.id)
//                .toUri()
//
//        return ResponseEntity.created(location).body(savedOrder)
//    }

//        @PostMapping("/place")
//        fun placeOrder(
//            @Valid @RequestBody orderForm: OrderPlaceForm,
//            @LoginMember member: Member,
//        ):
}

/* prev ver
package ecommerce.controller

import ecommerce.dto.order.OrderRequest
import ecommerce.model.Order
import ecommerce.service.OrderService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

@RequestMapping("/api/orders")
@RestController
class OrderController(private val orderService: OrderService) {
    @GetMapping()
    fun getOrders(pageable: Pageable): Page<Order> = orderService.findAllOrders(pageable)

    @GetMapping("{id}")
    fun getOrderById(
        @PathVariable id: Long,
    ): Order = orderService.findOrderById(id)

    @GetMapping("/member/{memberId}")
    fun getOrdersByMemberId(
        @PathVariable memberId: Long,
        pageable: Pageable,
    ): ResponseEntity<Map<String, Any>> {
        val result = orderService.getOrdersByMemberId(memberId, pageable)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/{id}")
    fun deleteOrderById(
        @PathVariable id: Long,
    ): ResponseEntity<Unit> {
        orderService.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping
    fun createOrder(
        @Valid @RequestBody orderRequest: OrderRequest,
    ): ResponseEntity<Order> {
        val savedOrder = orderService.createOrder(orderRequest)

        val location: URI =
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedOrder.id)
                .toUri()

        return ResponseEntity.created(location).body(savedOrder)
    }

    @PostMapping("/place-order")
    fun placeOrder(
        // @AuthenticationPrincipal member: Member, // Or some other way to get the logged-in user
        orderRequest: OrderRequest,
    ): ResponseEntity<Order> {
        val savedOrder = orderService.createOrder(orderRequest)

        val location: URI =
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedOrder.id)
                .toUri()

        return ResponseEntity.created(location).body(savedOrder)
    }

        @PostMapping("/place")
        fun placeOrder(
            @Valid @RequestBody orderForm: OrderPlaceForm,
            @LoginMember member: Member,
        ):
}


 */
