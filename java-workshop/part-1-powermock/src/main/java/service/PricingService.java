package service;

import domain.OrderItem;

import java.util.List;

/**
 * Pricing service with constants.
 */
public class PricingService {
    public static final double TAX_RATE = 0.20;
    public static final double FREE_SHIPPING_THRESHOLD = 100.0;
    public static final double SHIPPING_COST = 5.99;

    public double calculateTotal(List<OrderItem> items) {
        double subtotal = calculateSubtotal(items);
        double tax = calculateTax(subtotal);
        double shipping = calculateShipping(subtotal);
        return subtotal + tax + shipping;
    }

    private double calculateSubtotal(List<OrderItem> items) {
        return items.stream()
            .mapToDouble(item -> item.quantity() * item.unitPrice())
            .sum();
    }

    private double calculateTax(double subtotal) {
        return subtotal * TAX_RATE;
    }

    private double calculateShipping(double subtotal) {
        return subtotal >= FREE_SHIPPING_THRESHOLD ? 0.0 : SHIPPING_COST;
    }
}