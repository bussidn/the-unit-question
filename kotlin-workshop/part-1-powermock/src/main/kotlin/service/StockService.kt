package service

import domain.OrderItem
import domain.StockReservation
import infrastructure.Database

/**
 * Stock service with hardcoded Database dependency (singleton)
 * This forces us to use mockkObject to test it in isolation
 */
class StockService {
    
    fun checkAvailability(items: List<OrderItem>): Boolean {
        return items.all { item ->
            val stock = Database.getStock(item.productId)
            stock != null && stock.availableQuantity >= item.quantity
        }
    }
    
    fun reserveStock(items: List<OrderItem>): List<StockReservation> {
        return items.map { item ->
            val stock = Database.getStock(item.productId)
            if (stock != null && stock.availableQuantity >= item.quantity) {
                Database.updateStock(item.productId, stock.availableQuantity - item.quantity)
                StockReservation(item.productId, item.quantity, reserved = true)
            } else {
                StockReservation(item.productId, item.quantity, reserved = false)
            }
        }
    }
    
    fun releaseStock(reservations: List<StockReservation>) {
        reservations.filter { it.reserved }.forEach { reservation ->
            val stock = Database.getStock(reservation.productId)
            if (stock != null) {
                Database.updateStock(
                    reservation.productId, 
                    stock.availableQuantity + reservation.quantity
                )
            }
        }
    }
}

