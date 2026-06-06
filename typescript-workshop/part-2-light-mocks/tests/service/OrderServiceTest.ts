import { beforeEach, describe, expect, it, vi, type Mock } from 'vitest';
import { Order } from '../../src/domain/Order';
import { OrderItem } from '../../src/domain/OrderItem';
import { OrderStatus } from '../../src/domain/OrderStatus';
import { PaymentResult } from '../../src/domain/PaymentResult';
import { PaymentStatus } from '../../src/domain/PaymentStatus';
import { ShippingConfirmation } from '../../src/domain/ShippingConfirmation';
import { StockReservation } from '../../src/domain/StockReservation';
import type { OrderRepository } from '../../src/repository/OrderRepository';
import type { PaymentService } from '../../src/service/PaymentService';
import type { PricingService } from '../../src/service/PricingService';
import type { ShippingService } from '../../src/service/ShippingService';
import type { StockService } from '../../src/service/StockService';
import { OrderService } from '../../src/service/OrderService';

describe('OrderService', () => {
  let stockService: { reserveStock: Mock; releaseStock: Mock };
  let pricingService: { calculateTotal: Mock };
  let paymentService: { processPayment: Mock; refundPayment: Mock };
  let shippingService: { createShipment: Mock; cancelShipment: Mock };
  let orderRepository: { findById: Mock; save: Mock; updateStatus: Mock };
  let orderService: OrderService;

  beforeEach(() => {
    stockService = { reserveStock: vi.fn(), releaseStock: vi.fn() };
    pricingService = { calculateTotal: vi.fn() };
    paymentService = { processPayment: vi.fn(), refundPayment: vi.fn() };
    shippingService = { createShipment: vi.fn(), cancelShipment: vi.fn() };
    orderRepository = { findById: vi.fn(), save: vi.fn(), updateStatus: vi.fn() };
    orderService = new OrderService(
      orderRepository as unknown as OrderRepository,
      stockService as unknown as StockService,
      pricingService as unknown as PricingService,
      paymentService as unknown as PaymentService,
      shippingService as unknown as ShippingService,
    );
  });

  // ══════════════════════════════════════════════════════════════════════════
  // YOUR EXERCISE — See README for full instructions
  // ══════════════════════════════════════════════════════════════════════════

  // --- Step 1: Reject already-used discount codes ---
  // 1.4. Add a discountCodeService mock field and inject it via the OrderService constructor.
  //      Stub it with discountCodeService.checkDiscountCode.mockReturnValue(...)
  // 1.5. Fill in the GIVEN/WHEN/THEN below

  it.skip('p2_orderIsRejected_whenDiscountCodeIsAlreadyUsed', () => {
    // GIVEN an order with discount code SUMMER20
    // AND this code has already been used by this customer

    // WHEN the customer places the order

    // THEN the order is rejected with reason "Discount code already used"
    // AND no payment is triggered
    //     Hint: expect(paymentService.processPayment).not.toHaveBeenCalled()

    expect.fail('TODO: implement scenario "discount code already used"');
  });
  // ✅ 1.6. Run: npx vitest run — then back to README for Step 2
  // ─── END OF STEP 1 ──────────────────────────────────────────────────────



  // --- Step 2: Apply discount to the price ---
  // 2.2. Stub PricingService.calculateTotal to return the discounted total.
  //      Wire all the mocks needed for a successful order.
  // 2.3. Fill in the GIVEN/WHEN/THEN below

  it.skip('p2_orderIsConfirmed_whenDiscountCodeIsValid', () => {
    // GIVEN an order with 2 items at €55 each (subtotal: €110)
    // AND discount code SUMMER20 (-20%)
    // AND the code has not yet been used by this customer

    // WHEN the customer places the order

    // THEN payment is processed for €105.60
    // AND the order is confirmed

    expect.fail('TODO: implement scenario "order with a valid discount code"');
  });

  // ✅ 2.4. Run: npx vitest run — then back to README for Step 3
  // ─── END OF STEP 2 ──────────────────────────────────────────────────────



  // --- Step 3: Mark as used after payment ---
  // 3.2. Update the test above to also verify that markAsUsed is called on DiscountCodeService.
  //      Hint: expect(discountCodeService.markAsUsed).toHaveBeenCalledWith(customerId, discountCode)
  // 3.3. Fill in the GIVEN/WHEN/THEN below

  it.skip('p2_discountCodeIsMarkedAsUsed_afterSuccessfulPayment', () => {
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

  it('p2_orderIsRejected_whenOrderIsAlreadyConfirmed', () => {
    const order = new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 20.0)], OrderStatus.Confirmed, 50.0, 'TXN-001', 'TRACK-001');

    const result = orderService.placeOrder(order);

    expect(result.kind).toBe('failure');
    if (result.kind === 'failure') {
      expect(result.reason).toBe('Invalid order');
    }
  });

  it('p2_orderIsRejected_whenOrderHasNoItems', () => {
    const order = new Order('ORDER-001', 'CUST-001', [], OrderStatus.Pending, 0.0, null, null);

    const result = orderService.placeOrder(order);

    expect(result.kind).toBe('failure');
    if (result.kind === 'failure') {
      expect(result.reason).toBe('Invalid order');
    }
  });

  it('p2_noStockIsReserved_whenOrderIsInvalid', () => {
    const order = new Order('ORDER-001', 'CUST-001', [], OrderStatus.Pending, 0.0, null, null);

    orderService.placeOrder(order);

    expect(stockService.reserveStock).not.toHaveBeenCalled();
  });

  // ── Happy path ────────────────────────────────────────────────────────────

  it('p2_orderIsConfirmed_whenAllStepsSucceed', () => {
    const order = new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 20.0)], OrderStatus.Pending, 0.0, null, null);
    // total price is 29.0
    pricingService.calculateTotal.mockReturnValue(29.0);
    // payment succeeds
    paymentService.processPayment.mockReturnValue(new PaymentResult('ORDER-001', PaymentStatus.Success, 'TXN-123'));
    // stock is available for all items
    stockService.reserveStock.mockReturnValue([new StockReservation('PROD-001', 1, true)]);
    // shipment is created
    shippingService.createShipment.mockReturnValue(new ShippingConfirmation('ORDER-001', 'TRACK-456', '2024-12-25'));

    const result = orderService.placeOrder(order);

    expect(result.kind).toBe('success');
    if (result.kind === 'success') {
      expect(result.order.status).toBe(OrderStatus.Confirmed);
    }
  });

  it('p2_calculatedPriceIsStoredInOrder_whenAllStepsSucceed', () => {
    const order = new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 20.0)], OrderStatus.Pending, 0.0, null, null);
    // total price is 29.0
    pricingService.calculateTotal.mockReturnValue(29.0);
    // payment succeeds
    paymentService.processPayment.mockReturnValue(new PaymentResult('ORDER-001', PaymentStatus.Success, 'TXN-123'));
    // stock is available for all items
    stockService.reserveStock.mockReturnValue([new StockReservation('PROD-001', 1, true)]);
    // shipment is created
    shippingService.createShipment.mockReturnValue(new ShippingConfirmation('ORDER-001', 'TRACK-456', '2024-12-25'));

    const result = orderService.placeOrder(order);

    if (result.kind === 'success') {
      expect(result.order.totalPrice).toBe(29.0);
    }
  });

  it('p2_shippingConfirmationIsReturned_whenAllStepsSucceed', () => {
    const order = new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 20.0)], OrderStatus.Pending, 0.0, null, null);
    const shipment = new ShippingConfirmation('ORDER-001', 'TRACK-456', '2024-12-25');
    // total price is 29.0
    pricingService.calculateTotal.mockReturnValue(29.0);
    // payment succeeds
    paymentService.processPayment.mockReturnValue(new PaymentResult('ORDER-001', PaymentStatus.Success, 'TXN-123'));
    // stock is available for all items
    stockService.reserveStock.mockReturnValue([new StockReservation('PROD-001', 1, true)]);
    // shipment is created with the expected confirmation
    shippingService.createShipment.mockReturnValue(shipment);

    const result = orderService.placeOrder(order);

    if (result.kind === 'success') {
      expect(result.shippingConfirmation).toBe(shipment);
    }
  });

  // ── Stock unavailable ─────────────────────────────────────────────────────

  it('p2_orderFails_whenStockIsUnavailable', () => {
    // stock is NOT available
    stockService.reserveStock.mockReturnValue([new StockReservation('PROD-001', 1, false)]);

    const result = orderService.placeOrder(new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 20.0)], OrderStatus.Pending, 0.0, null, null));

    expect(result.kind).toBe('failure');
    if (result.kind === 'failure') {
      expect(result.reason).toBe('Insufficient stock');
    }
  });

  it('p2_paymentIsNotCharged_whenStockIsUnavailable', () => {
    // stock is NOT available
    stockService.reserveStock.mockReturnValue([new StockReservation('PROD-001', 1, false)]);

    orderService.placeOrder(new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 20.0)], OrderStatus.Pending, 0.0, null, null));

    expect(paymentService.processPayment).not.toHaveBeenCalled();
  });

  it('p2_shipmentIsNotCreated_whenStockIsUnavailable', () => {
    // stock is NOT available
    stockService.reserveStock.mockReturnValue([new StockReservation('PROD-001', 1, false)]);

    orderService.placeOrder(new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 20.0)], OrderStatus.Pending, 0.0, null, null));

    expect(shippingService.createShipment).not.toHaveBeenCalled();
  });

  // ── Payment declined ──────────────────────────────────────────────────────

  it('p2_orderFails_whenPaymentIsDeclined', () => {
    // stock is available for all items
    stockService.reserveStock.mockReturnValue([new StockReservation('PROD-001', 1, true)]);
    // total price is 45.0
    pricingService.calculateTotal.mockReturnValue(45.0);
    // payment is declined
    paymentService.processPayment.mockReturnValue(new PaymentResult('ORDER-001', PaymentStatus.Failed, null));

    const result = orderService.placeOrder(new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 20.0)], OrderStatus.Pending, 0.0, null, null));

    expect(result.kind).toBe('failure');
    if (result.kind === 'failure') {
      expect(result.reason).toBe('Payment failed');
    }
  });

  it('p2_shipmentIsNotCreated_whenPaymentIsDeclined', () => {
    // stock is available for all items
    stockService.reserveStock.mockReturnValue([new StockReservation('PROD-001', 1, true)]);
    // total price is 45.0
    pricingService.calculateTotal.mockReturnValue(45.0);
    // payment is declined
    paymentService.processPayment.mockReturnValue(new PaymentResult('ORDER-001', PaymentStatus.Failed, null));

    orderService.placeOrder(new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 20.0)], OrderStatus.Pending, 0.0, null, null));

    expect(shippingService.createShipment).not.toHaveBeenCalled();
  });

  it('p2_stockIsReleased_whenPaymentIsDeclined', () => {
    // stock is available for all items
    stockService.reserveStock.mockReturnValue([new StockReservation('PROD-001', 1, true)]);
    // total price is 45.0
    pricingService.calculateTotal.mockReturnValue(45.0);
    // payment is declined
    paymentService.processPayment.mockReturnValue(new PaymentResult('ORDER-001', PaymentStatus.Failed, null));

    orderService.placeOrder(new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 20.0)], OrderStatus.Pending, 0.0, null, null));

    expect(stockService.releaseStock).toHaveBeenCalled();
  });
});
