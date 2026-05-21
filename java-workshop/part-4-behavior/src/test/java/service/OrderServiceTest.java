package service;

import domain.Order;
import domain.OrderItem;
import domain.OrderStatus;
import helper.InMemoryPaymentGateway;
import helper.InMemoryShippingGateway;
import helper.InMemoryStockRepository;
import helper.OrderBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.fail;

class OrderServiceTest {

    private InMemoryPaymentGateway paymentGateway;
    private InMemoryShippingGateway shippingGateway;
    private InMemoryStockRepository stockRepository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        stockRepository = new InMemoryStockRepository();
        paymentGateway = new InMemoryPaymentGateway();
        shippingGateway = new InMemoryShippingGateway();

        orderService = new OrderService(
            new RepositoryStockService(stockRepository),
            new PricingService(),
            new GatewayPaymentService(paymentGateway),
            new GatewayShippingService(shippingGateway)
        );
    }

    // ── Given helpers ─────────────────────────────────────────────────────────

    private OrderBuilder anOrder() {
        return OrderBuilder.anOrder()
            .withId("ORDER-001")
            .withCustomerId("CUST-001")
            .withItems(List.of(new OrderItem("PROD-001", 1, 20.0)));
    }

    private void givenStockIsAvailableFor(Order order) {
        order.items().forEach(item -> stockRepository.withAvailableStock(item.productId(), item.quantity()));
    }

    private void givenPaymentIsDeclined() {
        paymentGateway.willDecline();
    }

    // ── Discount code ─────────────────────────────────────────────────────────
    // TODO: Implement the scenarios below, following the steps in the README.
    //       Follow the same pattern as the tests above:
    //       1. Create an InMemoryDiscountCodeRepository (already provided in helpers)
    //       2. Wire a DiscountCodeService into OrderService via the constructor
    //       3. Use given helpers to set up the discount code state
    //       4. Call placeOrder
    //       5. Assert the result using real values (no mocks!)

    // --- Step 1: Guard clause — reject if discount code is already used ---

    @Disabled
    @Test
    void orderIsRejected_whenDiscountCodeIsAlreadyUsed() {
        // GIVEN an order with discount code SUMMER20
        // AND this code has already been used by this customer

        // WHEN the customer places the order

        // THEN the order is rejected with reason "Discount code already used"
        // AND no payment is triggered

        fail("TODO: implement scenario 'discount code already used'");
    }

    // --- Step 2: Apply discount — calculate total with discount code ---

    @Disabled
    @Test
    void orderIsConfirmed_whenDiscountCodeIsValid() {
        // GIVEN an order with 2 items at €55 each (subtotal: €110)
        // AND discount code SUMMER20 (-20%)
        // AND the code has not yet been used by this customer
        // AND sufficient stock

        // WHEN the customer places the order

        // THEN payment is processed for €105.60
        // AND the order is confirmed

        fail("TODO: implement scenario 'order with a valid discount code'");
    }

    // --- Step 3: Mark as used — after payment, mark the discount code as used ---
    // Update the test above to also verify that the discount code is marked as used.
    // Hint: assertTrue(discountCodeRepository.isUsed("CUST-001", DiscountCode.SUMMER20));

    // ── Existing tests (for reference) ─────────────────────────────────────────

    @Test
    void orderIsConfirmed_whenAllStepsSucceed() {
        var order = anOrder().build();
        givenStockIsAvailableFor(order);

        OrderResult result = orderService.placeOrder(order);

        assertInstanceOf(OrderResult.Success.class, result);
        assertEquals(OrderStatus.CONFIRMED, ((OrderResult.Success) result).order().status());
    }

    @Test
    void calculatedPriceIsStoredInOrder_whenAllStepsSucceed() {
        var order = anOrder().build();
        givenStockIsAvailableFor(order);

        OrderResult result = orderService.placeOrder(order);

        double realTotal = 29.0; // subtotal=20, tax=4 (20%), shipping=5 (below 100€ threshold)
        assertEquals(realTotal, ((OrderResult.Success) result).order().totalPrice());
    }

    @Test
    void shippingConfirmationIsReturned_whenAllStepsSucceed() {
        var order = anOrder().build();
        givenStockIsAvailableFor(order);

        OrderResult result = orderService.placeOrder(order);

        assertEquals(shippingGateway.lastConfirmation(), ((OrderResult.Success) result).shippingConfirmation());
    }

    // ── Stock unavailable ─────────────────────────────────────────────────────

    @Test
    void orderFails_whenStockIsUnavailable() {
        var order = anOrder().build();
        // stockRepository is empty: no stock added

        OrderResult result = orderService.placeOrder(order);

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Insufficient stock", ((OrderResult.Failure) result).reason());
    }

    @Test
    void paymentIsNotCharged_whenStockIsUnavailable() {
        var order = anOrder().build();

        orderService.placeOrder(order);

        assertFalse(paymentGateway.wasCharged());
    }

    @Test
    void shipmentIsNotCreated_whenStockIsUnavailable() {
        var order = anOrder().build();

        orderService.placeOrder(order);

        assertFalse(shippingGateway.hasCreatedShipment());
    }

    // ── Payment declined ──────────────────────────────────────────────────────

    @Test
    void orderFails_whenPaymentIsDeclined() {
        var order = anOrder().build();
        givenStockIsAvailableFor(order);
        givenPaymentIsDeclined();

        OrderResult result = orderService.placeOrder(order);

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Payment failed", ((OrderResult.Failure) result).reason());
    }

    @Test
    void shipmentIsNotCreated_whenPaymentIsDeclined() {
        var order = anOrder().build();
        givenStockIsAvailableFor(order);
        givenPaymentIsDeclined();

        orderService.placeOrder(order);

        assertFalse(shippingGateway.hasCreatedShipment());
    }

    @Test
    void stockIsReleased_whenPaymentIsDeclined() {
        var order = anOrder().build();
        givenStockIsAvailableFor(order);
        givenPaymentIsDeclined();

        orderService.placeOrder(order);

        order.items().forEach(item ->
            assertEquals(item.quantity(), stockRepository.availableStock(item.productId())));
    }
}
