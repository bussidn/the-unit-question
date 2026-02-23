package service

import domain.PaymentResult
import domain.PaymentStatus
import infrastructure.PaymentGateway
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * PowerMock style: We need to mock the PaymentGateway object to test PaymentService
 */
class PaymentServiceTest {
    
    private lateinit var paymentService: PaymentService
    
    @BeforeEach
    fun setUp() {
        mockkObject(PaymentGateway) // Mock the static-like object!
        paymentService = PaymentService()
    }
    
    @AfterEach
    fun tearDown() {
        unmockkObject(PaymentGateway)
    }
    
    @Test
    fun `processPayment returns success when gateway succeeds`() {
        // Given
        val orderId = "ORDER-123"
        val customerId = "CUST-456"
        val amount = 100.0
        
        every { 
            PaymentGateway.processPayment(customerId, amount) 
        } returns PaymentResult("", PaymentStatus.SUCCESS, "TXN-789")
        
        // When
        val result = paymentService.processPayment(orderId, customerId, amount)
        
        // Then
        assertEquals(PaymentStatus.SUCCESS, result.status)
        assertEquals(orderId, result.orderId)
        assertEquals("TXN-789", result.transactionId)
        
        verify(exactly = 1) { PaymentGateway.processPayment(customerId, amount) }
    }
    
    @Test
    fun `processPayment returns failure for zero amount without calling gateway`() {
        // Given
        val orderId = "ORDER-123"
        val customerId = "CUST-456"
        val amount = 0.0
        
        // When
        val result = paymentService.processPayment(orderId, customerId, amount)
        
        // Then
        assertEquals(PaymentStatus.FAILED, result.status)
        verify(exactly = 0) { PaymentGateway.processPayment(any(), any()) }
    }
    
    @Test
    fun `processPayment returns failure when gateway fails`() {
        // Given
        val orderId = "ORDER-123"
        val customerId = "CUST-456"
        val amount = 100.0
        
        every { 
            PaymentGateway.processPayment(customerId, amount) 
        } returns PaymentResult("", PaymentStatus.FAILED)
        
        // When
        val result = paymentService.processPayment(orderId, customerId, amount)
        
        // Then
        assertEquals(PaymentStatus.FAILED, result.status)
    }
}

