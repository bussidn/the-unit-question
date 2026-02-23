package repository

import domain.Stock

/**
 * Repository interface for stock operations.
 * Injected into services - easy to mock!
 */
interface StockRepository {
    fun findByProductId(productId: String): Stock?
    fun findByProductIds(productIds: List<String>): Map<String, Stock>
    fun updateQuantity(productId: String, newQuantity: Int)
}

