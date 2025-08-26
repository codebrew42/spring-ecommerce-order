package ecommerce.controller

import com.fasterxml.jackson.databind.ObjectMapper
import ecommerce.dto.order.CreateOrderRequest
import ecommerce.model.Currency
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CheckoutControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should return 401 when not authenticated`() {
        val request =
            CreateOrderRequest(
                cartItemIds = listOf(1L),
                paymentMethod = "pm_card_visa",
                currency = Currency.EUR,
            )

        mockMvc.post("/api/checkout") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `should return 401 with invalid token`() {
        val request =
            CreateOrderRequest(
                cartItemIds = listOf(1L),
                paymentMethod = "pm_card_visa",
                currency = Currency.EUR,
            )

        mockMvc.post("/api/checkout") {
            header("Authorization", "Bearer invalid_token")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isUnauthorized() }
        }
    }
}
