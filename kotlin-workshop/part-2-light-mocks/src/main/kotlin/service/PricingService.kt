package service

import domain.Order

/**
 * Pricing service - pure logic, no dependencies.
 * Easy to test without mocks!
 */
class PricingService {

    fun calculateTotal(order: Order): Double {
        return order.items.sumOf { it.quantity * it.unitPrice }
    }

    fun calculateTotalWithTax(order: Order, taxRate: Double): Double {
        val subtotal = calculateTotal(order)
        return subtotal * (1 + taxRate)
    }
}

