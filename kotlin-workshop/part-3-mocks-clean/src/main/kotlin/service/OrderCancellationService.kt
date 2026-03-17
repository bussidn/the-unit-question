package service

import domain.CancellationResult
import domain.OrderStatus
import domain.StockReservation
import repository.OrderRepository

/**
 * Service for cancelling orders.
 * Handles refund, stock release, and shipment cancellation.
 */
class OrderCancellationService(
    private val orderRepository: OrderRepository,
    private val paymentService: PaymentService,
    private val stockService: StockService,
    private val shippingService: ShippingService
) {

    fun cancelOrder(orderId: String): CancellationResult {
        // 1. Find the order
        val order = orderRepository.findById(orderId)
            ?: return CancellationResult.Failure("Order not found")

        // 2. Check if cancellation is allowed
        if (order.status == OrderStatus.SHIPPED) {
            return CancellationResult.Failure("Cannot cancel shipped order")
        }
        if (order.status == OrderStatus.CANCELLED) {
            return CancellationResult.Failure("Order already cancelled")
        }

        // 3. Release reserved stock
        val reservations = order.items.map { item ->
            StockReservation(item.productId, item.quantity, reserved = true)
        }
        stockService.releaseStock(reservations)

        // 4. Refund payment if exists
        if (order.transactionId != null) {
            val refundSuccess = paymentService.refundPayment(order.transactionId)
            if (!refundSuccess) {
                return CancellationResult.Failure("Refund failed")
            }
        }

        // 5. Cancel shipment if tracking exists
        if (order.trackingNumber != null) {
            shippingService.cancelShipment(order.trackingNumber)
        }

        // 6. Update order status
        orderRepository.updateStatus(orderId, OrderStatus.CANCELLED)

        return CancellationResult.Success
    }
}

