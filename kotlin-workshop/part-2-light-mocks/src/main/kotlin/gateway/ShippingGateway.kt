package gateway

import domain.ShippingConfirmation
import domain.ShippingRequest

/**
 * Gateway interface for shipping operations.
 * Injected into services - easy to mock!
 */
interface ShippingGateway {
    fun createShipment(request: ShippingRequest): ShippingConfirmation
    fun cancelShipment(trackingNumber: String): Boolean
}

