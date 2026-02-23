package service

import domain.OrderItem

/**
 * Pricing service - relatively simple, but still has internal logic
 */
class PricingService {
    
    companion object {
        const val TAX_RATE = 0.20
        const val FREE_SHIPPING_THRESHOLD = 100.0
        const val SHIPPING_COST = 5.99
    }
    
    fun calculateTotal(items: List<OrderItem>): Double {
        val subtotal = calculateSubtotal(items)
        val tax = calculateTax(subtotal)
        val shipping = calculateShipping(subtotal)
        return subtotal + tax + shipping
    }
    
    private fun calculateSubtotal(items: List<OrderItem>): Double {
        return items.sumOf { it.quantity * it.unitPrice }
    }
    
    private fun calculateTax(subtotal: Double): Double {
        return subtotal * TAX_RATE
    }
    
    private fun calculateShipping(subtotal: Double): Double {
        return if (subtotal >= FREE_SHIPPING_THRESHOLD) 0.0 else SHIPPING_COST
    }
}

