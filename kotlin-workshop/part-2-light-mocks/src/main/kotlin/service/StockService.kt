package service

import domain.OrderItem
import domain.StockReservation
import repository.StockRepository

/**
 * Stock service with injected repository.
 * Clean architecture - easy to test with mocks!
 */
class StockService(
    private val stockRepository: StockRepository
) {

    fun checkAvailability(items: List<OrderItem>): Boolean {
        return items.all { item ->
            val stock = stockRepository.findByProductId(item.productId)
            stock != null && stock.availableQuantity >= item.quantity
        }
    }

    fun reserveStock(items: List<OrderItem>): List<StockReservation> {
        return items.map { item ->
            val stock = stockRepository.findByProductId(item.productId)
            if (stock != null && stock.availableQuantity >= item.quantity) {
                stockRepository.updateQuantity(item.productId, stock.availableQuantity - item.quantity)
                StockReservation(item.productId, item.quantity, reserved = true)
            } else {
                StockReservation(item.productId, item.quantity, reserved = false)
            }
        }
    }

    fun releaseStock(reservations: List<StockReservation>) {
        reservations.filter { it.reserved }.forEach { reservation ->
            val stock = stockRepository.findByProductId(reservation.productId)
            if (stock != null) {
                stockRepository.updateQuantity(
                    reservation.productId,
                    stock.availableQuantity + reservation.quantity
                )
            }
        }
    }
}

