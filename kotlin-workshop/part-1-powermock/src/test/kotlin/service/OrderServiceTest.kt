package service

import domain.*
import infrastructure.Database
import infrastructure.PaymentGateway
import infrastructure.ShippingApi
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * THE POWERMOCK NIGHTMARE
 * 
 * To test OrderService in isolation, we need to mock:
 * - StockService constructor
 * - PricingService constructor  
 * - PaymentService constructor
 * - ShippingService constructor
 * - Database singleton
 * - PaymentGateway singleton
 * - ShippingApi constructor (used inside ShippingService)
 * 
 * This is madness. But this is what "unit testing" means
 * when you take "isolation" to its extreme.
 */
class OrderServiceTest {
    
    private lateinit var orderService: OrderService
    
    @BeforeEach
    fun setUp() {
        // Mock ALL the constructors!
        mockkConstructor(StockService::class)
        mockkConstructor(PricingService::class)
        mockkConstructor(PaymentService::class)
        mockkConstructor(ShippingService::class)
        
        // Mock ALL the singletons!
        mockkObject(Database)
        mockkObject(PaymentGateway)
        
        orderService = OrderService()
    }
    
    @AfterEach
    fun tearDown() {
        unmockkConstructor(StockService::class)
        unmockkConstructor(PricingService::class)
        unmockkConstructor(PaymentService::class)
        unmockkConstructor(ShippingService::class)
        unmockkObject(Database)
        unmockkObject(PaymentGateway)
    }
    
    @Test
    fun `createOrder succeeds when all steps pass`() {
        // Given
        val order = Order(
            id = "ORDER-123",
            customerId = "CUST-456",
            items = listOf(OrderItem("PROD-001", 2, 10.0))
        )
        
        // Mock StockService
        every { anyConstructed<StockService>().checkAvailability(any()) } returns true
        every { anyConstructed<StockService>().reserveStock(any()) } returns listOf(
            StockReservation("PROD-001", 2, reserved = true)
        )
        
        // Mock PricingService
        every { anyConstructed<PricingService>().calculateTotal(any()) } returns 24.0
        
        // Mock PaymentService
        every { anyConstructed<PaymentService>().processPayment(any(), any(), any()) } returns 
            PaymentResult("ORDER-123", PaymentStatus.SUCCESS, "TXN-789")
        
        // Mock ShippingService
        every { anyConstructed<ShippingService>().createShipment(any()) } returns
            ShippingConfirmation("ORDER-123", "TRACK-ABC", "2024-12-25")
        
        // Mock Database
        every { Database.saveOrder(any(), any()) } just Runs
        
        // When
        val result = orderService.createOrder(order)
        
        // Then
        assertTrue(result is OrderResult.Success)
        assertEquals(OrderStatus.CONFIRMED, (result as OrderResult.Success).order.status)
        
        // Verify ALL the interactions (because that's what we do...)
        verifySequence {
            anyConstructed<StockService>().checkAvailability(order.items)
            anyConstructed<PricingService>().calculateTotal(order.items)
            anyConstructed<PaymentService>().processPayment("ORDER-123", "CUST-456", 24.0)
            anyConstructed<StockService>().reserveStock(order.items)
            anyConstructed<ShippingService>().createShipment(any())
            Database.saveOrder("ORDER-123", any())
        }
    }

    @Test
    fun `createOrder fails when stock is unavailable`() {
        // Given
        val order = Order(
            id = "ORDER-123",
            customerId = "CUST-456",
            items = listOf(OrderItem("PROD-001", 2, 10.0))
        )

        every { anyConstructed<StockService>().checkAvailability(any()) } returns false

        // When
        val result = orderService.createOrder(order)

        // Then
        assertTrue(result is OrderResult.Failure)
        assertEquals("Insufficient stock", (result as OrderResult.Failure).reason)

        // Verify that we stopped at stock check
        verify { anyConstructed<StockService>().checkAvailability(order.items) }
        verify(exactly = 0) { anyConstructed<PricingService>().calculateTotal(any()) }
    }

    @Test
    fun `createOrder fails and refunds when stock reservation fails`() {
        // Given
        val order = Order(
            id = "ORDER-123",
            customerId = "CUST-456",
            items = listOf(OrderItem("PROD-001", 2, 10.0))
        )

        every { anyConstructed<StockService>().checkAvailability(any()) } returns true
        every { anyConstructed<PricingService>().calculateTotal(any()) } returns 24.0
        every { anyConstructed<PaymentService>().processPayment(any(), any(), any()) } returns
            PaymentResult("ORDER-123", PaymentStatus.SUCCESS, "TXN-789")
        every { anyConstructed<StockService>().reserveStock(any()) } returns listOf(
            StockReservation("PROD-001", 2, reserved = false) // Failed!
        )
        every { anyConstructed<PaymentService>().refundPayment("TXN-789") } returns true
        every { anyConstructed<StockService>().releaseStock(any()) } just Runs

        // When
        val result = orderService.createOrder(order)

        // Then
        assertTrue(result is OrderResult.Failure)
        assertEquals("Could not reserve stock", (result as OrderResult.Failure).reason)

        // Verify refund was called
        verify { anyConstructed<PaymentService>().refundPayment("TXN-789") }
        verify { anyConstructed<StockService>().releaseStock(any()) }
    }
}

