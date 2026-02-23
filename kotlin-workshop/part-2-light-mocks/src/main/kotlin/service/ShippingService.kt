package service

import domain.Order
import domain.ShippingConfirmation
import domain.ShippingItem
import domain.ShippingRequest
import gateway.ShippingGateway

/**
 * Shipping service with injected gateway.
 * Clean architecture - easy to test with mocks!
 */
class ShippingService(
    private val shippingGateway: ShippingGateway
) {

    fun createShipment(order: Order): ShippingConfirmation {
        val request = ShippingRequest(
            orderId = order.id,
            customerId = order.customerId,
            items = order.items.map { ShippingItem(it.productId, it.quantity) }
        )
        return shippingGateway.createShipment(request)
    }

    fun cancelShipment(trackingNumber: String): Boolean {
        return shippingGateway.cancelShipment(trackingNumber)
    }
}

