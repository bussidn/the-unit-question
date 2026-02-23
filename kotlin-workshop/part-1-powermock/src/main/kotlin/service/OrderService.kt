package service

import domain.Order
import domain.OrderStatus
import domain.PaymentStatus
import domain.ShippingConfirmation
import infrastructure.Database

/**
 * Main orchestrator service - creates all its dependencies internally
 * This is the "PowerMock nightmare" - to test this in isolation,
 * you need to mock constructors, objects, and singletons
 */
class OrderService {
    
    fun createOrder(order: Order): OrderResult {
        // Create dependencies internally (bad practice!)
        val stockService = StockService()
        val pricingService = PricingService()
        val paymentService = PaymentService()
        val shippingService = ShippingService()
        
        // 1. Check stock availability
        if (!stockService.checkAvailability(order.items)) {
            return OrderResult.Failure(order.reject(), "Insufficient stock")
        }
        
        // 2. Calculate total price
        val totalPrice = pricingService.calculateTotal(order.items)
        
        // 3. Process payment
        val paymentResult = paymentService.processPayment(
            order.id, 
            order.customerId, 
            totalPrice
        )
        
        if (paymentResult.status != PaymentStatus.SUCCESS) {
            return OrderResult.Failure(order.reject(), "Payment failed")
        }
        
        // 4. Reserve stock
        val reservations = stockService.reserveStock(order.items)
        if (reservations.any { !it.reserved }) {
            // Rollback payment
            paymentResult.transactionId?.let { paymentService.refundPayment(it) }
            stockService.releaseStock(reservations)
            return OrderResult.Failure(order.reject(), "Could not reserve stock")
        }
        
        // 5. Create shipment
        val shippingConfirmation = shippingService.createShipment(order)
        
        // 6. Save to database and return confirmed order
        val confirmedOrder = order.confirm(totalPrice)
        Database.saveOrder(confirmedOrder.id, mapOf(
            "status" to confirmedOrder.status,
            "totalPrice" to totalPrice,
            "trackingNumber" to shippingConfirmation.trackingNumber
        ))
        
        return OrderResult.Success(confirmedOrder, shippingConfirmation)
    }
}

sealed class OrderResult {
    data class Success(
        val order: Order,
        val shippingConfirmation: ShippingConfirmation
    ) : OrderResult()
    
    data class Failure(
        val order: Order,
        val reason: String
    ) : OrderResult()
}

