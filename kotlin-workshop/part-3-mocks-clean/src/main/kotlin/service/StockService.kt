package service

import domain.StockReservation

/**
 * Stock service interface for domain operations.
 */
interface StockService {
    fun releaseStock(reservations: List<StockReservation>)
}

