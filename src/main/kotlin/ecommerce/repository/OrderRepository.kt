package ecommerce.repository

import ecommerce.model.Order
import java.awt.print.Pageable
import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying

interface OrderRepository : JpaRepository<Order, Long> {
    @Modifying
    fun findByMemberId(memberId: Long, pageable: Pageable): Page<Order>

    @Modifying
    fun findByMemberId()
}
