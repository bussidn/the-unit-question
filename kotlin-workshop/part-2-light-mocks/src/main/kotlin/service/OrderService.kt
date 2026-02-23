package service

import domain.*
import repository.StockRepository

/**
 * Order service that orchestrates the order creation process.
 * All dependencies are injected - clean architecture!
 */
class OrderService(
    private val stockService: StockService,
    private val pricingService: PricingService,
    private val paymentService: PaymentService,
    private val shippingService: ShippingService,
    private val stockRepository: StockRepository
) {

    fun createOrder(order: Order): OrderResult {
        // 1. Check stock availability
        if (!stockService.checkAvailability(order.items)) {
            return OrderResult.Failure("Insufficient stock")
        }

        // 2. Calculate total
        val total = pricingService.calculateTotal(order)

        // 3. Process payment
        val paymentResult = paymentService.processPayment(order.copy(
            items = order.items // Pass order for payment calculation
        ))
        
        if (paymentResult.status != PaymentStatus.SUCCESS) {
            return OrderResult.Failure("Payment failed")
        }

        // 4. Reserve stock
        val reservations = stockService.reserveStock(order.items)
        if (reservations.any { !it.reserved }) {
            // Rollback: refund payment
            paymentService.refundPayment(paymentResult.transactionId)
            stockService.releaseStock(reservations)
            return OrderResult.Failure("Could not reserve stock")
        }

        // 5. Create shipment
        val shipment = shippingService.createShipment(order)

        // 6. Update order status
        val confirmedOrder = order.copy(status = OrderStatus.CONFIRMED)

        return OrderResult.Success(confirmedOrder, shipment)
    }
}

sealed class OrderResult {
    data class Success(val order: Order, val shipment: ShippingConfirmation) : OrderResult()
    data class Failure(val reason: String) : OrderResult()
}

