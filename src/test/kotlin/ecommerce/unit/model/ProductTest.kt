package ecommerce.unit.model

import ecommerce.model.Product
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class ProductTest {
    @Test
    fun `create product successfully`() {
        assertDoesNotThrow {
            Product(
                name = "Test Product",
                price = 10.0,
                quantity = 2,
                imageUrl = "http://example.com/image.jpg",
            )
        }
    }
}
