package service

import domain.*
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import repository.OrderRepository
import kotlin.test.assertEquals

class OrderCancellationServiceTest {

    private val orderRepository = mockk<OrderRepository>()
    private val paymentService = mockk<PaymentService>()
    private val stockService = mockk<StockService>()
    private val shippingService = mockk<ShippingService>()

    private val service = OrderCancellationService(
        orderRepository,
        paymentService,
        stockService,
        shippingService
    )

    @BeforeEach
    fun setup() {
        clearMocks(orderRepository, paymentService, stockService, shippingService)
    }

    @Test
    fun `cancel order before shipping - refunds and releases stock`() {
        // Given
        val order = Order(
            id = "ORDER-001",
            customerId = "CUST-001",
            items = listOf(OrderItem("PROD-001", 2, 25.0)),
            status = OrderStatus.CONFIRMED,
            transactionId = "TXN-123",
            trackingNumber = null
        )

        every { orderRepository.findById("ORDER-001") } returns order
        every { stockService.releaseStock(any()) } just Runs
        every { paymentService.refundPayment("TXN-123") } returns true
        every { orderRepository.updateStatus("ORDER-001", OrderStatus.CANCELLED) } just Runs

        // When
        val result = service.cancelOrder("ORDER-001")

        // Then
        assertEquals(CancellationResult.Success, result)

        verifySequence {
            orderRepository.findById("ORDER-001")
            stockService.releaseStock(any())
            paymentService.refundPayment("TXN-123")
            orderRepository.updateStatus("ORDER-001", OrderStatus.CANCELLED)
        }
        confirmVerified(shippingService)
    }

    @Test
    fun `cancel order with shipment in progress - cancels everything`() {
        // Given
        val order = Order(
            id = "ORDER-002",
            customerId = "CUST-001",
            items = listOf(
                OrderItem("PROD-001", 1, 50.0),
                OrderItem("PROD-002", 3, 15.0)
            ),
            status = OrderStatus.CONFIRMED,
            transactionId = "TXN-456",
            trackingNumber = "TRACK-789"
        )

        every { orderRepository.findById("ORDER-002") } returns order
        every { stockService.releaseStock(any()) } just Runs
        every { paymentService.refundPayment("TXN-456") } returns true
        every { shippingService.cancelShipment("TRACK-789") } returns true
        every { orderRepository.updateStatus("ORDER-002", OrderStatus.CANCELLED) } just Runs

        // When
        val result = service.cancelOrder("ORDER-002")

        // Then
        assertEquals(CancellationResult.Success, result)

        verifySequence {
            orderRepository.findById("ORDER-002")
            stockService.releaseStock(any())
            paymentService.refundPayment("TXN-456")
            shippingService.cancelShipment("TRACK-789")
            orderRepository.updateStatus("ORDER-002", OrderStatus.CANCELLED)
        }
    }

    @Test
    fun `cannot cancel shipped order`() {
        // Given
        val order = Order(
            id = "ORDER-003",
            customerId = "CUST-001",
            items = listOf(OrderItem("PROD-001", 1, 100.0)),
            status = OrderStatus.SHIPPED,
            transactionId = "TXN-789"
        )

        every { orderRepository.findById("ORDER-003") } returns order

        // When
        val result = service.cancelOrder("ORDER-003")

        // Then
        assertEquals(CancellationResult.Failure("Cannot cancel shipped order"), result)
        verify(exactly = 0) { paymentService.refundPayment(any()) }
        verify(exactly = 0) { stockService.releaseStock(any()) }
    }

    @Test
    fun `order not found returns failure`() {
        // Given
        every { orderRepository.findById("UNKNOWN") } returns null

        // When
        val result = service.cancelOrder("UNKNOWN")

        // Then
        assertEquals(CancellationResult.Failure("Order not found"), result)
    }
}

