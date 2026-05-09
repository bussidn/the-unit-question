package service;

import domain.DiscountCode;
import domain.OrderItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingServiceTest {

    private final PricingService pricingService = new PricingService();

    // ─── Sans code promo ─────────────────────────────────────────────────────

    @Test
    void calculateTotal_withNoDiscount_appliesShippingWhenSubtotalBelowThreshold() {
        var items = List.of(new OrderItem("PROD-001", 1, 20.0));
        // sous-total = 20, TVA = 4, livraison = 5 → 29.0
        assertEquals(29.0, pricingService.calculateTotal(items), 0.01);
    }

    @Test
    void calculateTotal_withNoDiscount_noShippingWhenSubtotalAtOrAboveThreshold() {
        var items = List.of(new OrderItem("PROD-001", 5, 20.0));
        // sous-total = 100, TVA = 20, livraison = 0 → 120.0
        assertEquals(120.0, pricingService.calculateTotal(items), 0.01);
    }

    // ─── Avec code promo ─────────────────────────────────────────────────────

    @Test
    void calculateTotal_withDiscount_thresholdAppliedOnDiscountedSubtotal() {
        var items = List.of(new OrderItem("PROD-001", 2, 55.0));
        // sous-total brut = 110, après SUMMER20 (-20%) = 88
        // 88 < 100 → livraison = 5, TVA = 17.60 → total = 110.60
        assertEquals(110.60, pricingService.calculateTotal(items, DiscountCode.SUMMER20), 0.01);
    }

    @Test
    void calculateTotal_withDiscount_noShippingWhenDiscountedSubtotalAboveThreshold() {
        var items = List.of(new OrderItem("PROD-001", 3, 70.0));
        // sous-total brut = 210, après SUMMER20 (-20%) = 168
        // 168 >= 100 → livraison = 0, TVA = 33.60 → total = 201.60
        assertEquals(201.60, pricingService.calculateTotal(items, DiscountCode.SUMMER20), 0.01);
    }

    @Test
    void calculateTotal_withONCE50_halvesPriceAndAppliesShipping() {
        var items = List.of(new OrderItem("PROD-001", 2, 55.0));
        // sous-total brut = 110, après ONCE50 (-50%) = 55
        // 55 < 100 → livraison = 5, TVA = 11 → total = 71.0
        assertEquals(71.0, pricingService.calculateTotal(items, DiscountCode.ONCE50), 0.01);
    }
}

