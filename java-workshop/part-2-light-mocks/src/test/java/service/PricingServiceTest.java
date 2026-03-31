package service;

import domain.OrderItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingServiceTest {

    @Test
    void calculateTotal_includesTaxAndShipping_whenSubtotalIsBelowThreshold() {
        var pricingService = new PricingService();

        var items = List.of(new OrderItem("PROD-001", 2, 10.0));

        // subtotal = 20.0, tax = 4.0, shipping = 5.99
        double result = pricingService.calculateTotal(items);

        assertEquals(29.99, result);
    }

    @Test
    void calculateTotal_includesTaxAndNoShipping_whenSubtotalIsExactlyAtThreshold() {
        var pricingService = new PricingService();

        var items = List.of(new OrderItem("PROD-001", 2, 50.0));

        // subtotal = 100.0, tax = 20.0, shipping = 0.0
        double result = pricingService.calculateTotal(items);

        assertEquals(120.0, result);
    }

    @Test
    void calculateTotal_includesTaxAndNoShipping_whenSubtotalIsAboveThreshold() {
        var pricingService = new PricingService();

        var items = List.of(new OrderItem("PROD-001", 3, 50.0));

        // subtotal = 150.0, tax = 30.0, shipping = 0.0
        double result = pricingService.calculateTotal(items);

        assertEquals(180.0, result);
    }

    @Test
    void calculateTotal_includesOnlyShipping_whenItemListIsEmpty() {
        var pricingService = new PricingService();

        var items = List.<OrderItem>of();

        // subtotal = 0.0, tax = 0.0, shipping = 5.99
        double result = pricingService.calculateTotal(items);

        assertEquals(5.99, result);
    }

    @Test
    void calculateTotal_sumsAllItems_whenMultipleItemsAreProvided() {
        var pricingService = new PricingService();

        var items = List.of(
            new OrderItem("PROD-001", 2, 10.0),
            new OrderItem("PROD-002", 1, 20.0)
        );

        // subtotal = 40.0, tax = 8.0, shipping = 5.99
        double result = pricingService.calculateTotal(items);

        assertEquals(53.99, result);
    }
}

