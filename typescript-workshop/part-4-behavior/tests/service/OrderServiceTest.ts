import { beforeEach, describe, expect, it } from 'vitest';
import { Order } from '../../src/domain/Order';
import { OrderItem } from '../../src/domain/OrderItem';
import { OrderStatus } from '../../src/domain/OrderStatus';
import { GatewayPaymentService } from '../../src/service/GatewayPaymentService';
import { GatewayShippingService } from '../../src/service/GatewayShippingService';
import { OrderService } from '../../src/service/OrderService';
import { PricingService } from '../../src/service/PricingService';
import { RepositoryStockService } from '../../src/service/RepositoryStockService';
import { InMemoryOrderRepository } from '../helper/InMemoryOrderRepository';
import { InMemoryPaymentGateway } from '../helper/InMemoryPaymentGateway';
import { InMemoryShippingGateway } from '../helper/InMemoryShippingGateway';
import { InMemoryStockRepository } from '../helper/InMemoryStockRepository';
import { OrderBuilder } from '../helper/OrderBuilder';

describe('OrderService', () => {
  let orderRepository: InMemoryOrderRepository;
  let stockRepository: InMemoryStockRepository;
  let paymentGateway: InMemoryPaymentGateway;
  let shippingGateway: InMemoryShippingGateway;
  let orderService: OrderService;

  beforeEach(() => {
    orderRepository = new InMemoryOrderRepository();
    stockRepository = new InMemoryStockRepository();
    paymentGateway = new InMemoryPaymentGateway();
    shippingGateway = new InMemoryShippingGateway();

    orderService = new OrderService(
      orderRepository,
      new RepositoryStockService(stockRepository),
      new PricingService(),
      new GatewayPaymentService(paymentGateway),
      new GatewayShippingService(shippingGateway),
    );
  });

  function anOrder() {
    return OrderBuilder.anOrder()
      .withId('ORDER-001').withCustomerId('CUST-001')
      .withItems([new OrderItem('PROD-001', 1, 20.0)]);
  }

  function givenStockIsAvailableFor(order: Order): void {
    order.items.forEach(item => stockRepository.withAvailableStock(item.productId, item.quantity));
  }

  function givenPaymentIsDeclined(): void {
    paymentGateway.willDecline();
  }

  // ══════════════════════════════════════════════════════════════════════════
  // YOUR EXERCISE — See README for full instructions
  // ══════════════════════════════════════════════════════════════════════════

  // --- Step 1: Reject already-used discount codes ---
  // 1.3. Create an InMemoryDiscountCodeRepository (already provided in tests/helper/).
  //      Wire a DiscountCodeService into OrderService via the constructor.
  //      Create a given helper to set up the discount code state.
  // 1.4. Fill in the GIVEN/WHEN/THEN below

  it.skip('p4_orderIsRejected_whenDiscountCodeIsAlreadyUsed', () => {
    // GIVEN an order with discount code SUMMER20
    // AND this code has already been used by this customer

    // WHEN the customer places the order

    // THEN the order is rejected with reason "Discount code already used"
    // AND no payment is triggered
    //     Hint: expect(paymentGateway.wasCharged()).toBe(false)

    expect.fail('TODO: implement scenario "discount code already used"');
  });
  // ✅ 1.5. Run: npx vitest run — then back to README for Step 2
  // ─── END OF STEP 1 ──────────────────────────────────────────────────────



  // --- Step 2: Apply discount to the price ---
  // 2.2. Create a given helper for a valid discount code.
  //      Don't forget to set up sufficient stock.
  // 2.3. Fill in the GIVEN/WHEN/THEN below

  it.skip('p4_orderIsConfirmed_whenDiscountCodeIsValid', () => {
    // GIVEN an order with 2 items at €55 each (subtotal: €110)
    // AND discount code SUMMER20 (-20%)
    // AND the code has not yet been used by this customer

    // WHEN the customer places the order

    // THEN payment is processed for €105.60   <- intentional wrong value (real is €110.60)
    // AND the order is confirmed

    expect.fail('TODO: implement scenario "order with a valid discount code"');
  });
  // ✅ 2.4. Run: npx vitest run — then back to README for Step 3
  // ─── END OF STEP 2 ──────────────────────────────────────────────────────



  // --- Step 3: Mark as used after payment ---
  // 3.2. Update the test above to also verify that the discount code is marked as used.
  //      Hint: assertTrue(discountCodeRepository.isMarkedAsUsed("CUST-001", DiscountCode.SUMMER20));
  //      (Note: the method is isMarkedAsUsed, not isUsed)
  // 3.3. Fill in the GIVEN/WHEN/THEN below

  it.skip('p4_discountCodeIsMarkedAsUsed_afterSuccessfulPayment', () => {
    // GIVEN an order with a valid discount code
    // AND payment succeeds

    // WHEN the customer places the order

    // THEN the discount code is marked as used

    expect.fail('TODO: implement scenario "discount code marked as used after payment"');
  });
  // ✅ 3.4. Run: npx vitest run ✅
  // ─── END OF STEP 3 ──────────────────────────────────────────────────────



  // ══════════════════════════════════════════════════════════════════════════
  // REFERENCE TESTS — existing tests, for inspiration
  // ══════════════════════════════════════════════════════════════════════════

  // ── Validation ────────────────────────────────────────────────────────────

  it('p4_orderIsRejected_whenOrderIsAlreadyConfirmed', () => {
    const order = OrderBuilder.confirmed()
      .withId('ORDER-001').withCustomerId('CUST-001')
      .withItems([new OrderItem('PROD-001', 1, 20.0)])
      .build();

    const result = orderService.placeOrder(order);

    expect(result.kind).toBe('failure');
    if (result.kind === 'failure') {
      expect(result.reason).toBe('Invalid order');
    }
  });

  it('p4_orderIsRejected_whenOrderHasNoItems', () => {
    const order = anOrder().withItems([]).build();

    const result = orderService.placeOrder(order);

    expect(result.kind).toBe('failure');
    if (result.kind === 'failure') {
      expect(result.reason).toBe('Invalid order');
    }
  });

  it('p4_noPaymentIsCharged_whenOrderIsInvalid', () => {
    const order = anOrder().withItems([]).build();

    orderService.placeOrder(order);

    expect(paymentGateway.wasCharged()).toBe(false);
  });

  // ── Happy path ────────────────────────────────────────────────────────────

  it('p4_orderIsConfirmed_whenAllStepsSucceed', () => {
    const order = anOrder().build();
    givenStockIsAvailableFor(order);

    const result = orderService.placeOrder(order);

    expect(result.kind).toBe('success');
    if (result.kind === 'success') {
      expect(result.order.status).toBe(OrderStatus.Confirmed);
    }
  });

  it('p4_calculatedPriceIsStoredInOrder_whenAllStepsSucceed', () => {
    // 1 item × €20, subtotal = 20, tax = 4 (20%), shipping = 5 → total = 29.0
    const order = anOrder().build();
    givenStockIsAvailableFor(order);

    const result = orderService.placeOrder(order);

    if (result.kind === 'success') {
      expect(result.order.totalPrice).toBeCloseTo(29.0, 2);
    }
  });

  it('p4_shippingConfirmationIsReturned_whenAllStepsSucceed', () => {
    const order = anOrder().build();
    givenStockIsAvailableFor(order);

    const result = orderService.placeOrder(order);

    expect(result.kind).toBe('success');
    if (result.kind === 'success') {
      expect(result.shippingConfirmation).not.toBeNull();
      expect(result.shippingConfirmation.orderId).toBe('ORDER-001');
      expect(result.shippingConfirmation.trackingNumber).toBe('TRACK-ORDER-001');
    }
  });

  // ── Stock unavailable ─────────────────────────────────────────────────────

  it('p4_orderFails_whenStockIsUnavailable', () => {
    const order = anOrder().build();
    // no stock seeded — stock unavailable

    const result = orderService.placeOrder(order);

    expect(result.kind).toBe('failure');
    if (result.kind === 'failure') {
      expect(result.reason).toBe('Insufficient stock');
    }
  });

  it('p4_paymentIsNotCharged_whenStockIsUnavailable', () => {
    const order = anOrder().build();
    // no stock seeded — stock unavailable

    orderService.placeOrder(order);

    expect(paymentGateway.wasCharged()).toBe(false);
  });

  it('p4_shipmentIsNotCreated_whenStockIsUnavailable', () => {
    const order = anOrder().build();
    // no stock seeded — stock unavailable

    orderService.placeOrder(order);

    expect(shippingGateway.hasCreatedShipment()).toBe(false);
  });

  // ── Payment declined ──────────────────────────────────────────────────────

  it('p4_orderFails_whenPaymentIsDeclined', () => {
    const order = anOrder().build();
    givenStockIsAvailableFor(order);
    givenPaymentIsDeclined();

    const result = orderService.placeOrder(order);

    expect(result.kind).toBe('failure');
    if (result.kind === 'failure') {
      expect(result.reason).toBe('Payment failed');
    }
  });

  it('p4_shipmentIsNotCreated_whenPaymentIsDeclined', () => {
    const order = anOrder().build();
    givenStockIsAvailableFor(order);
    givenPaymentIsDeclined();

    orderService.placeOrder(order);

    expect(shippingGateway.hasCreatedShipment()).toBe(false);
  });

  it('p4_stockIsReleased_whenPaymentIsDeclined', () => {
    const order = anOrder().build();
    givenStockIsAvailableFor(order);
    givenPaymentIsDeclined();

    orderService.placeOrder(order);

    order.items.forEach(item =>
      expect(stockRepository.availableStock(item.productId)).toBe(item.quantity)
    );
  });
});
