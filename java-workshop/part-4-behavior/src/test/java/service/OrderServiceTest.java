package service;

import domain.Order;
import domain.OrderItem;
import domain.OrderStatus;
import helper.InMemoryOrderRepository;
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

    private InMemoryOrderRepository orderRepository;
    private InMemoryPaymentGateway paymentGateway;
    private InMemoryShippingGateway shippingGateway;
    private InMemoryStockRepository stockRepository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        stockRepository = new InMemoryStockRepository();
        paymentGateway = new InMemoryPaymentGateway();
        shippingGateway = new InMemoryShippingGateway();

        orderService = new OrderService(
            orderRepository,
            new RepositoryStockService(stockRepository),
            new PricingService(),
            new GatewayPaymentService(paymentGateway),
            new GatewayShippingService(shippingGateway)
        );
    }

    // ══════════════════════════════════════════════════════════════════════════
    // YOUR EXERCISE — See README for full instructions
    // ══════════════════════════════════════════════════════════════════════════

    // --- Step 1: Reject already-used discount codes ---
    // 1.4. Create an InMemoryDiscountCodeRepository (already provided in helpers).
    //      Wire a DiscountCodeService into OrderService via the constructor.
    //      Create a given helper to set up the discount code state.
    // 1.5. Fill in the GIVEN/WHEN/THEN below

    @Disabled
    @Test
    void p4_orderIsRejected_whenDiscountCodeIsAlreadyUsed() {
        // GIVEN an order with discount code SUMMER20
        // AND this code has already been used by this customer

        // WHEN the customer places the order

        // THEN the order is rejected with reason "Discount code already used"
        // AND no payment is triggered

        fail("TODO: implement scenario 'discount code already used'");
    }
    // ✅ 1.6. Run: ./gradlew test — then back to README for Step 2

    // --- Step 2: Apply discount to the price ---
    // 2.3. Create a given helper for a valid discount code.
    //      Don't forget to set up sufficient stock.
    // 2.4. Fill in the GIVEN/WHEN/THEN below

    @Disabled
    @Test
    void p4_orderIsConfirmed_whenDiscountCodeIsValid() {
        // GIVEN an order with 2 items at €55 each (subtotal: €110)
        // AND discount code SUMMER20 (-20%)
        // AND the code has not yet been used by this customer
        // AND sufficient stock

        // WHEN the customer places the order

        // THEN payment is processed for €105.60
        // AND the order is confirmed

        fail("TODO: implement scenario 'order with a valid discount code'");
    }

    // ✅ 2.5. Run: ./gradlew test — then back to README for Step 3

    // --- Step 3: Mark as used after payment ---
    // 3.4. Update the test above to also verify that the discount code is marked as used.
    //      Hint: assertTrue(discountCodeRepository.isUsed("CUST-001", DiscountCode.SUMMER20));
    // 3.5. Fill in the GIVEN/WHEN/THEN below

    @Disabled
    @Test
    void p4_discountCodeIsMarkedAsUsed_afterSuccessfulPayment() {
        // GIVEN an order with a valid discount code
        // AND payment succeeds

        // WHEN the customer places the order

        // THEN the discount code is marked as used

        fail("TODO: implement scenario 'discount code marked as used after payment'");
    }
    // ✅ 3.6. Run: ./gradlew test ✅

    // ══════════════════════════════════════════════════════════════════════════
    // REFERENCE TESTS — existing tests and helpers, for inspiration
    // ══════════════════════════════════════════════════════════════════════════

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

    // ── Validation ────────────────────────────────────────────────────────────

    @Test
    void p4_orderIsRejected_whenOrderIsAlreadyConfirmed() {
        var order = anOrder().withStatus(OrderStatus.CONFIRMED).build();

        OrderResult result = orderService.placeOrder(order);

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Invalid order", ((OrderResult.Failure) result).reason());
    }

    @Test
    void p4_orderIsRejected_whenOrderHasNoItems() {
        var order = anOrder().withItems(List.of()).build();

        OrderResult result = orderService.placeOrder(order);

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Invalid order", ((OrderResult.Failure) result).reason());
    }

    @Test
    void p4_noPaymentIsCharged_whenOrderIsInvalid() {
        var order = anOrder().withItems(List.of()).build();

        orderService.placeOrder(order);

        assertFalse(paymentGateway.wasCharged());
    }

    // ── Happy path ────────────────────────────────────────────────────────────

    @Test
    void p4_orderIsConfirmed_whenAllStepsSucceed() {
        var order = anOrder().build();
        givenStockIsAvailableFor(order);

        OrderResult result = orderService.placeOrder(order);

        assertInstanceOf(OrderResult.Success.class, result);
        assertEquals(OrderStatus.CONFIRMED, ((OrderResult.Success) result).order().status());
    }

    @Test
    void p4_calculatedPriceIsStoredInOrder_whenAllStepsSucceed() {
        var order = anOrder().build();
        givenStockIsAvailableFor(order);

        OrderResult result = orderService.placeOrder(order);

        double realTotal = 29.0; // subtotal=20, tax=4 (20%), shipping=5 (below 100€ threshold)
        assertEquals(realTotal, ((OrderResult.Success) result).order().totalPrice());
    }

    @Test
    void p4_shippingConfirmationIsReturned_whenAllStepsSucceed() {
        var order = anOrder().build();
        givenStockIsAvailableFor(order);

        OrderResult result = orderService.placeOrder(order);

        assertEquals(shippingGateway.lastConfirmation(), ((OrderResult.Success) result).shippingConfirmation());
    }

    // ── Stock unavailable ─────────────────────────────────────────────────────

    @Test
    void p4_orderFails_whenStockIsUnavailable() {
        var order = anOrder().build();
        // stockRepository is empty: no stock added

        OrderResult result = orderService.placeOrder(order);

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Insufficient stock", ((OrderResult.Failure) result).reason());
    }

    @Test
    void p4_paymentIsNotCharged_whenStockIsUnavailable() {
        var order = anOrder().build();

        orderService.placeOrder(order);

        assertFalse(paymentGateway.wasCharged());
    }

    @Test
    void p4_shipmentIsNotCreated_whenStockIsUnavailable() {
        var order = anOrder().build();

        orderService.placeOrder(order);

        assertFalse(shippingGateway.hasCreatedShipment());
    }

    // ── Payment declined ──────────────────────────────────────────────────────

    @Test
    void p4_orderFails_whenPaymentIsDeclined() {
        var order = anOrder().build();
        givenStockIsAvailableFor(order);
        givenPaymentIsDeclined();

        OrderResult result = orderService.placeOrder(order);

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Payment failed", ((OrderResult.Failure) result).reason());
    }

    @Test
    void p4_shipmentIsNotCreated_whenPaymentIsDeclined() {
        var order = anOrder().build();
        givenStockIsAvailableFor(order);
        givenPaymentIsDeclined();

        orderService.placeOrder(order);

        assertFalse(shippingGateway.hasCreatedShipment());
    }

    @Test
    void p4_stockIsReleased_whenPaymentIsDeclined() {
        var order = anOrder().build();
        givenStockIsAvailableFor(order);
        givenPaymentIsDeclined();

        orderService.placeOrder(order);

        order.items().forEach(item ->
            assertEquals(item.quantity(), stockRepository.availableStock(item.productId())));
    }
}
