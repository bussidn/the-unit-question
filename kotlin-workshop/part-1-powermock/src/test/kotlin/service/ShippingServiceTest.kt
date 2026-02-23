package service

import domain.Order
import domain.OrderItem
import domain.ShippingConfirmation
import domain.ShippingRequest
import infrastructure.ShippingApi
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * PowerMock style: We need to mock the constructor of ShippingApi
 * because ShippingService creates it internally with `new`
 */
class ShippingServiceTest {
    
    private lateinit var shippingService: ShippingService
    private lateinit var mockShippingApi: ShippingApi
    
    @BeforeEach
    fun setUp() {
        mockShippingApi = mockk<ShippingApi>()
        mockkConstructor(ShippingApi::class) // Mock the constructor!
        shippingService = ShippingService()
    }
    
    @AfterEach
    fun tearDown() {
        unmockkConstructor(ShippingApi::class)
    }
    
    @Test
    fun `createShipment calls API and returns confirmation`() {
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
        
        every { 
            anyConstructed<ShippingApi>().createShipment(any()) 
        } returns expectedConfirmation
        
        // When
        val result = shippingService.createShipment(order)
        
        // Then
        assertEquals(expectedConfirmation, result)
        
        verify { 
            anyConstructed<ShippingApi>().createShipment(match { request ->
                request.orderId == "ORDER-123" &&
                request.customerId == "CUST-456" &&
                request.items.size == 2
            })
        }
    }
    
    @Test
    fun `cancelShipment calls API and returns result`() {
        // Given
        val trackingNumber = "TRACK-ABC123"
        
        every { 
            anyConstructed<ShippingApi>().cancelShipment(trackingNumber) 
        } returns true
        
        // When
        val result = shippingService.cancelShipment(trackingNumber)
        
        // Then
        assertEquals(true, result)
        verify { anyConstructed<ShippingApi>().cancelShipment(trackingNumber) }
    }
}

