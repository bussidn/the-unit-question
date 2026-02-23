package infrastructure

import domain.ShippingConfirmation
import domain.ShippingRequest
import java.util.UUID

/**
 * External API client - instantiated directly in services (bad practice)
 */
class ShippingApi {
    
    private val baseUrl = "https://shipping.example.com"
    
    fun createShipment(request: ShippingRequest): ShippingConfirmation {
        // In real life, this would make an HTTP call
        return ShippingConfirmation(
            orderId = request.orderId,
            trackingNumber = "TRACK-${UUID.randomUUID().toString().take(8)}",
            estimatedDelivery = "2024-12-25"
        )
    }
    
    fun cancelShipment(trackingNumber: String): Boolean {
        // Simulate cancellation
        return true
    }
}

