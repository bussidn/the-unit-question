package service

import domain.*
import gateway.ShippingGateway
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ShippingServiceTest {

    private lateinit var shippingGateway: ShippingGateway
    private lateinit var shippingService: ShippingService

    @BeforeEach
    fun setUp() {
        shippingGateway = mockk()
        shippingService = ShippingService(shippingGateway)
    }

    @Test
    fun `createShipment calls gateway and returns confirmation`() {
        // Given
        val order = Order(
            id = "ORDER-123",
            customerId = "CUST-456",
            items = listOf(
                OrderItem("PROD-001", 2, 10.0),
                OrderItem("PROD-002", 1, 20.0)
            )
        )

        val expectedConfirmation = ShippingConfirmation(
            orderId = "ORDER-123",
            trackingNumber = "TRACK-ABC123",
            estimatedDelivery = "2024-12-25"
        )

        every { shippingGateway.createShipment(any()) } returns expectedConfirmation

        // When
        val result = shippingService.createShipment(order)

        // Then
        assertEquals(expectedConfirmation, result)
        verify {
            shippingGateway.createShipment(match { request ->
                request.orderId == "ORDER-123" &&
                request.customerId == "CUST-456" &&
                request.items.size == 2
            })
        }
    }

    @Test
    fun `cancelShipment returns true when gateway succeeds`() {
        // Given
        every { shippingGateway.cancelShipment("TRACK-ABC123") } returns true

        // When
        val result = shippingService.cancelShipment("TRACK-ABC123")

        // Then
        assertTrue(result)
        verify(exactly = 1) { shippingGateway.cancelShipment("TRACK-ABC123") }
    }

    @Test
    fun `cancelShipment returns false when gateway fails`() {
        // Given
        every { shippingGateway.cancelShipment("TRACK-UNKNOWN") } returns false

        // When
        val result = shippingService.cancelShipment("TRACK-UNKNOWN")

        // Then
        assertFalse(result)
        verify(exactly = 1) { shippingGateway.cancelShipment("TRACK-UNKNOWN") }
    }
}

