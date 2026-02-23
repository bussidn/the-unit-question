package service

import domain.*
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import repository.StockRepository
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrderServiceTest {

    private lateinit var stockService: StockService
    private lateinit var pricingService: PricingService
    private lateinit var paymentService: PaymentService
    private lateinit var shippingService: ShippingService
    private lateinit var stockRepository: StockRepository
    private lateinit var orderService: OrderService

    @BeforeEach
    fun setUp() {
        stockService = mockk()
        pricingService = mockk()
        paymentService = mockk()
        shippingService = mockk()
        stockRepository = mockk()

        orderService = OrderService(
            stockService = stockService,
            pricingService = pricingService,
            paymentService = paymentService,
            shippingService = shippingService,
            stockRepository = stockRepository
        )
    }

    @Test
    fun `createOrder succeeds when all steps pass`() {
        // Given
        val order = Order(
            id = "ORDER-123",
            customerId = "CUST-456",
            items = listOf(OrderItem("PROD-001", 2, 10.0))
        )

        every { stockService.checkAvailability(any()) } returns true
        every { pricingService.calculateTotal(any()) } returns 24.0
        every { paymentService.processPayment(any()) } returns PaymentResult("TXN-789", PaymentStatus.SUCCESS)
        every { stockService.reserveStock(any()) } returns listOf(
            StockReservation("PROD-001", 2, reserved = true)
        )
        every { shippingService.createShipment(any()) } returns
            ShippingConfirmation("ORDER-123", "TRACK-ABC", "2024-12-25")

        // When
        val result = orderService.createOrder(order)

        // Then
        assertTrue(result is OrderResult.Success)
        assertEquals(OrderStatus.CONFIRMED, (result as OrderResult.Success).order.status)

        verifySequence {
            stockService.checkAvailability(order.items)
            pricingService.calculateTotal(order)
            paymentService.processPayment(any())
            stockService.reserveStock(order.items)
            shippingService.createShipment(any())
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

        every { stockService.checkAvailability(any()) } returns false

        // When
        val result = orderService.createOrder(order)

        // Then
        assertTrue(result is OrderResult.Failure)
        assertEquals("Insufficient stock", (result as OrderResult.Failure).reason)

        verify { stockService.checkAvailability(order.items) }
        verify(exactly = 0) { pricingService.calculateTotal(any()) }
    }

    @Test
    fun `createOrder fails and refunds when stock reservation fails`() {
        // Given
        val order = Order(
            id = "ORDER-123",
            customerId = "CUST-456",
            items = listOf(OrderItem("PROD-001", 2, 10.0))
        )

        every { stockService.checkAvailability(any()) } returns true
        every { pricingService.calculateTotal(any()) } returns 24.0
        every { paymentService.processPayment(any()) } returns PaymentResult("TXN-789", PaymentStatus.SUCCESS)
        every { stockService.reserveStock(any()) } returns listOf(
            StockReservation("PROD-001", 2, reserved = false) // Failed!
        )
        every { paymentService.refundPayment("TXN-789") } returns true
        every { stockService.releaseStock(any()) } just Runs

        // When
        val result = orderService.createOrder(order)

        // Then
        assertTrue(result is OrderResult.Failure)
        assertEquals("Could not reserve stock", (result as OrderResult.Failure).reason)

        verify { paymentService.refundPayment("TXN-789") }
        verify { stockService.releaseStock(any()) }
    }
}

