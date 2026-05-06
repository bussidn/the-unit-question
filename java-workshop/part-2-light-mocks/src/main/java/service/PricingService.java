package service;

import domain.DiscountCode;
import domain.OrderItem;

import java.util.List;

public class PricingService {
    public static final double TAX_RATE = 0.20;
    public static final double FREE_SHIPPING_THRESHOLD = 100.0;
    public static final double SHIPPING_COST = 5.00;

    public double calculateTotal(List<OrderItem> items) {
        double subtotal = calculateSubtotal(items);
        double tax = calculateTax(subtotal);
        double shipping = calculateShipping(subtotal);
        return subtotal + tax + shipping;
    }

    public double calculateTotal(List<OrderItem> items, DiscountCode discountCode) {
        double subtotal = calculateSubtotal(items);
        double discountedSubtotal = applyDiscount(subtotal, discountCode);
        double tax = calculateTax(discountedSubtotal);
        double shipping = calculateShipping(discountedSubtotal);
        return discountedSubtotal + tax + shipping;
    }

    private double calculateSubtotal(List<OrderItem> items) {
        return items.stream()
                .mapToDouble(item -> item.quantity() * item.unitPrice())
                .sum();
    }

    private double applyDiscount(double subtotal, DiscountCode discountCode) {
        double discountFactor = 1.0 - (discountCode.getPercentage() / 100.0);
        return subtotal * discountFactor;
    }

    private double calculateTax(double subtotal) {
        return subtotal * TAX_RATE;
    }

    private double calculateShipping(double subtotal) {
        return subtotal >= FREE_SHIPPING_THRESHOLD ? 0.0 : SHIPPING_COST;
    }
}

