package service

import domain.Order
import domain.ShippingConfirmation
import domain.ShippingItem
import domain.ShippingRequest
import infrastructure.ShippingApi

/**
 * Shipping service that creates its own ShippingApi instance (new)
 * This forces us to use mockkConstructor to test it in isolation
 */
class ShippingService {
    
    fun createShipment(order: Order): ShippingConfirmation {
        val api = ShippingApi() // Hardcoded instantiation - bad practice!
        
        val request = ShippingRequest(
            orderId = order.id,
            customerId = order.customerId,
            items = order.items.map { ShippingItem(it.productId, it.quantity) }
        )
        
        return api.createShipment(request)
    }
    
    fun cancelShipment(trackingNumber: String): Boolean {
        val api = ShippingApi() // Again, creating new instance
        return api.cancelShipment(trackingNumber)
    }
}

