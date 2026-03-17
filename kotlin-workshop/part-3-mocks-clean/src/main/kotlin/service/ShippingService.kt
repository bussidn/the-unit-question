package service

/**
 * Shipping service interface for domain operations.
 */
interface ShippingService {
    fun cancelShipment(trackingNumber: String): Boolean
}

