package service

import domain.*
import gateway.PaymentGateway
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PaymentServiceTest {

    private lateinit var paymentGateway: PaymentGateway
    private lateinit var paymentService: PaymentService

    @BeforeEach
    fun setUp() {
        paymentGateway = mockk()
        paymentService = PaymentService(paymentGateway)
    }

    @Test
    fun `processPayment calculates total and calls gateway`() {
        // Given
        val order = Order(
            id = "ORDER-001",
            customerId = "CUST-001",
            items = listOf(
                OrderItem("PROD-001", 2, 10.0),  // 20.0
                OrderItem("PROD-002", 1, 30.0)   // 30.0
            )
        )

        every { paymentGateway.process(any()) } returns PaymentResult("TXN-001", PaymentStatus.SUCCESS)

        // When
        val result = paymentService.processPayment(order)

        // Then
        assertEquals(PaymentStatus.SUCCESS, result.status)
        verify {
            paymentGateway.process(match { request ->
                request.orderId == "ORDER-001" &&
                request.amount == 50.0 &&
                request.customerId == "CUST-001"
            })
        }
    }

    @Test
    fun `processPayment returns failure when gateway fails`() {
        // Given
        val order = Order(
            id = "ORDER-001",
            customerId = "CUST-001",
            items = listOf(OrderItem("PROD-001", 1, 100.0))
        )

        every { paymentGateway.process(any()) } returns PaymentResult("", PaymentStatus.FAILED)

        // When
        val result = paymentService.processPayment(order)

        // Then
        assertEquals(PaymentStatus.FAILED, result.status)
        verify(exactly = 1) { paymentGateway.process(any()) }
    }

    @Test
    fun `refundPayment delegates to gateway and returns true on success`() {
        // Given
        every { paymentGateway.refund("TXN-001") } returns true

        // When
        val result = paymentService.refundPayment("TXN-001")

        // Then
        assertTrue(result)
        verify(exactly = 1) { paymentGateway.refund("TXN-001") }
    }

    @Test
    fun `refundPayment returns false when gateway fails`() {
        // Given
        every { paymentGateway.refund("TXN-999") } returns false

        // When
        val result = paymentService.refundPayment("TXN-999")

        // Then
        assertFalse(result)
        verify(exactly = 1) { paymentGateway.refund("TXN-999") }
    }
}

