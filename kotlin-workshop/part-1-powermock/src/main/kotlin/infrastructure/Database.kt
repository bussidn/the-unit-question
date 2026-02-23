package infrastructure

import domain.Stock

/**
 * Simulates a database singleton - the kind of thing you'd need PowerMock to mock
 */
object Database {
    
    private val stocks = mutableMapOf(
        "PROD-001" to Stock("PROD-001", 100),
        "PROD-002" to Stock("PROD-002", 50),
        "PROD-003" to Stock("PROD-003", 0)
    )
    
    fun getStock(productId: String): Stock? = stocks[productId]

    /**
     * Batch version - retrieves multiple stocks in a single call
     * More efficient than calling getStock() multiple times
     */
    fun getStockBatch(productIds: List<String>): Map<String, Stock> {
        return productIds.mapNotNull { id -> stocks[id]?.let { id to it } }.toMap()
    }

    fun updateStock(productId: String, newQuantity: Int) {
        stocks[productId]?.let {
            stocks[productId] = it.copy(availableQuantity = newQuantity)
        }
    }
    
    fun saveOrder(orderId: String, data: Map<String, Any>) {
        // Simulates saving to DB
        println("Saving order $orderId to database")
    }
}

