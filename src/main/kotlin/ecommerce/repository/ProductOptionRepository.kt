package ecommerce.repository

import ecommerce.model.ProductOption
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductOptionRepository : JpaRepository<ProductOption, Long> {
    fun existsByName(name: String): Boolean

    fun deleteByProductId(productId: Long)
}
