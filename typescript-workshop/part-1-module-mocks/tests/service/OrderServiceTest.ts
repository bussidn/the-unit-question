// vi.mock() calls are hoisted to the top of the file by Vitest.
// This is how we intercept the `new StockService()`, `new PaymentService()`, etc.
// calls that happen INSIDE OrderService.placeOrder() — the TypeScript equivalent of
// Mockito's mockConstruction().
vi.mock('../../src/service/StockService');
vi.mock('../../src/service/PaymentService');
vi.mock('../../src/service/ShippingService');
vi.mock('../../src/repository/OrderRepository');
vi.mock('../../src/service/PricingService');

import { beforeEach, describe, expect, it, vi } from 'vitest';
import { Order } from '../../src/domain/Order';
import { OrderItem } from '../../src/domain/OrderItem';
import { OrderStatus } from '../../src/domain/OrderStatus';
import { PaymentResult } from '../../src/domain/PaymentResult';
import { PaymentStatus } from '../../src/domain/PaymentStatus';
import { ShippingConfirmation } from '../../src/domain/ShippingConfirmation';
import { StockReservation } from '../../src/domain/StockReservation';
import { OrderRepository } from '../../src/repository/OrderRepository';
import { PaymentService } from '../../src/service/PaymentService';
import { calculateTotal, calculateTotalWithDiscount } from '../../src/service/PricingService';
import { ShippingService } from '../../src/service/ShippingService';
import { StockService } from '../../src/service/StockService';
import { OrderService } from '../../src/service/OrderService';

// Helpers to get the mock instances created inside placeOrder().
// Because OrderService creates dependencies with `new`, Vitest captures each
// instantiation in `.mock.instances`. The first (and only) call per placeOrder()
// invocation is at index [0].
function stockMock() { return vi.mocked(StockService).mock.instances[0]; }
function paymentMock() { return vi.mocked(PaymentService).mock.instances[0]; }
function shippingMock() { return vi.mocked(ShippingService).mock.instances[0]; }
function repoMock() { return vi.mocked(OrderRepository).mock.instances[0]; }

describe('OrderService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  // ══════════════════════════════════════════════════════════════════════════
  // YOUR EXERCISE — See README for full instructions
  // ══════════════════════════════════════════════════════════════════════════

  // --- Step 1: Reject already-used discount codes ---
  // 1.1. Add vi.mock('../../src/service/DiscountCodeService'); at the top of this file.
  // 1.2. Import DiscountCodeService and DiscountCode.
  // 1.3. Add `discountCodeService = new DiscountCodeService();` inside placeOrder()
  //      in OrderService.ts (only when discountCode is provided).
  // 1.4. Fill in the GIVEN/WHEN/THEN below.
  //      Stub: vi.mocked(DiscountCodeService).mock.instances[0].checkDiscountCode
  //             .mockReturnValue(true/false)

  it.skip('p1_orderIsRejected_whenDiscountCodeIsAlreadyUsed', () => {
    // GIVEN an order with discount code SUMMER20
    // AND this code has already been used by this customer

    // WHEN the customer places the order

    // THEN the order is rejected with reason "Discount code already used"
    // AND no payment is triggered
    //     Hint: expect(paymentMock().processPayment).not.toHaveBeenCalled()

    expect.fail('TODO: implement scenario "discount code already used"');
  });
  // ✅ 1.5. Run: pnpm test — then back to README for Step 2
  // ─── END OF STEP 1 ──────────────────────────────────────────────────────



  // --- Step 2: Apply discount to the price ---
  // 2.1. calculateTotalWithDiscount is already mocked via vi.mock('../../src/service/PricingService').
  //      Stub it with: vi.mocked(calculateTotalWithDiscount).mockReturnValue(...)
  // 2.2. Fill in the GIVEN/WHEN/THEN below.

  it.skip('p1_orderIsConfirmed_whenDiscountCodeIsValid', () => {
    // GIVEN an order with 2 items at €55 each (subtotal: €110)
    // AND discount code SUMMER20 (-20%)
    // AND the code has not yet been used by this customer

    // WHEN the customer places the order

    // THEN payment is processed for €105.60
    // AND the order is confirmed

    expect.fail('TODO: implement scenario "order with a valid discount code"');
  });
  // ✅ 2.3. Run: pnpm test — then back to README for Step 3
  // ─── END OF STEP 2 ──────────────────────────────────────────────────────



  // --- Step 3: Mark as used after payment ---
  // 3.1. After a successful payment, call discountCodeService.markAsUsed().
  // 3.2. Fill in the GIVEN/WHEN/THEN below.
  //      Hint: expect(vi.mocked(DiscountCodeService).mock.instances[0].markAsUsed)
  //              .toHaveBeenCalledWith('CUST-001', DiscountCode.SUMMER20)

  it.skip('p1_discountCodeIsMarkedAsUsed_afterSuccessfulPayment', () => {
    // GIVEN an order with a valid discount code
    // AND payment succeeds

    // WHEN the customer places the order

    // THEN the discount code is marked as used

    expect.fail('TODO: implement scenario "discount code marked as used after payment"');
  });
  // ✅ 3.3. Run: pnpm test ✅
  // ─── END OF STEP 3 ──────────────────────────────────────────────────────



  // ══════════════════════════════════════════════════════════════════════════
  // REFERENCE TESTS — existing tests, for inspiration
  // ══════════════════════════════════════════════════════════════════════════

  // ── Validation ────────────────────────────────────────────────────────────

  it('p1_isValid_returnsTrue_whenOrderIsPendingWithItems', () => {
    const order = new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 10.0)], OrderStatus.Pending, 0, null, null);

    expect(new OrderService().isValid(order)).toBe(true);
  });

  it('p1_isValid_returnsFalse_whenOrderIsAlreadyConfirmed', () => {
    const order = new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 10.0)], OrderStatus.Confirmed, 50.0, 'TXN-001', 'TRACK-001');

    expect(new OrderService().isValid(order)).toBe(false);
  });

  it('p1_isValid_returnsFalse_whenOrderHasNoItems', () => {
    const order = new Order('ORDER-001', 'CUST-001', [], OrderStatus.Pending, 0, null, null);

    expect(new OrderService().isValid(order)).toBe(false);
  });

  it('p1_orderIsRejected_whenValidationFails', () => {
    const orderService = new OrderService();
    // Spy on the public isValid method to isolate placeOrder logic
    vi.spyOn(orderService, 'isValid').mockReturnValue(false);
    const order = new Order('ORDER-999', 'CUST-999', [new OrderItem('PROD-001', 1, 10.0)], OrderStatus.Pending, 0, null, null);

    const result = orderService.placeOrder(order);

    expect(result.kind).toBe('failure');
    if (result.kind === 'failure') {
      expect(result.reason).toBe('Invalid order');
    }
    // StockService IS constructed (before the isValid check), but reserveStock is never called
    expect(stockMock().reserveStock).not.toHaveBeenCalled();
    expect(paymentMock().processPayment).not.toHaveBeenCalled();
    expect(repoMock().save).not.toHaveBeenCalled();
  });

  it('p1_orderIsRejected_whenOrderIsAlreadyConfirmed', () => {
    const order = new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 20.0)], OrderStatus.Confirmed, 50.0, 'TXN-001', 'TRACK-001');

    const result = new OrderService().placeOrder(order);

    expect(result.kind).toBe('failure');
    if (result.kind === 'failure') {
      expect(result.reason).toBe('Invalid order');
    }
  });

  it('p1_orderIsRejected_whenOrderHasNoItems', () => {
    const order = new Order('ORDER-001', 'CUST-001', [], OrderStatus.Pending, 0.0, null, null);

    const result = new OrderService().placeOrder(order);

    expect(result.kind).toBe('failure');
    if (result.kind === 'failure') {
      expect(result.reason).toBe('Invalid order');
    }
  });

  it('p1_noStockIsReserved_whenOrderIsInvalid', () => {
    const order = new Order('ORDER-001', 'CUST-001', [], OrderStatus.Pending, 0.0, null, null);

    new OrderService().placeOrder(order);

    expect(stockMock().reserveStock).not.toHaveBeenCalled();
  });

  // ── Happy path ────────────────────────────────────────────────────────────

  it('p1_orderIsConfirmed_whenAllStepsSucceed', () => {
    vi.mocked(calculateTotal).mockReturnValue(29.0);
    vi.mocked(StockService.prototype.reserveStock).mockReturnValue([new StockReservation('PROD-001', 1, true)]);
    vi.mocked(PaymentService.prototype.processPayment).mockReturnValue(new PaymentResult('ORDER-001', PaymentStatus.Success, 'TXN-123'));
    vi.mocked(ShippingService.prototype.createShipment).mockReturnValue(new ShippingConfirmation('ORDER-001', 'TRACK-456', '2024-12-25'));

    const order = new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 20.0)], OrderStatus.Pending, 0.0, null, null);
    const result = new OrderService().placeOrder(order);

    expect(result.kind).toBe('success');
    if (result.kind === 'success') {
      expect(result.order.status).toBe(OrderStatus.Confirmed);
    }
  });

  it('p1_calculatedPriceIsStoredInOrder_whenAllStepsSucceed', () => {
    vi.mocked(calculateTotal).mockReturnValue(29.0);
    vi.mocked(StockService.prototype.reserveStock).mockReturnValue([new StockReservation('PROD-001', 1, true)]);
    vi.mocked(PaymentService.prototype.processPayment).mockReturnValue(new PaymentResult('ORDER-001', PaymentStatus.Success, 'TXN-123'));
    vi.mocked(ShippingService.prototype.createShipment).mockReturnValue(new ShippingConfirmation('ORDER-001', 'TRACK-456', '2024-12-25'));

    const order = new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 20.0)], OrderStatus.Pending, 0.0, null, null);
    const result = new OrderService().placeOrder(order);

    if (result.kind === 'success') {
      expect(result.order.totalPrice).toBe(29.0);
    }
  });

  it('p1_shippingConfirmationIsReturned_whenAllStepsSucceed', () => {
    const shipment = new ShippingConfirmation('ORDER-001', 'TRACK-456', '2024-12-25');
    vi.mocked(calculateTotal).mockReturnValue(29.0);
    vi.mocked(StockService.prototype.reserveStock).mockReturnValue([new StockReservation('PROD-001', 1, true)]);
    vi.mocked(PaymentService.prototype.processPayment).mockReturnValue(new PaymentResult('ORDER-001', PaymentStatus.Success, 'TXN-123'));
    vi.mocked(ShippingService.prototype.createShipment).mockReturnValue(shipment);

    const order = new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 20.0)], OrderStatus.Pending, 0.0, null, null);
    const result = new OrderService().placeOrder(order);

    if (result.kind === 'success') {
      expect(result.shippingConfirmation).toBe(shipment);
    }
  });

  // ── Key difference from Parts 2–4: transactionId is correctly stored ──────
  // In Parts 2-4, Order.confirm() only takes totalPrice and ignores transactionId.
  // Here in Part 1, confirm() takes all three params — so this test PASSES.
  // The same test would FAIL in Parts 2-4, exposing THE TRAP.

  it('p1_transactionIdIsStoredInConfirmedOrder', () => {
    vi.mocked(calculateTotal).mockReturnValue(10.0);
    vi.mocked(StockService.prototype.reserveStock).mockReturnValue([new StockReservation('ITEM-C', 1, true)]);
    vi.mocked(PaymentService.prototype.processPayment).mockReturnValue(new PaymentResult('ORDER-003', PaymentStatus.Success, 'TRANSACTION-XYZ-789'));
    vi.mocked(ShippingService.prototype.createShipment).mockReturnValue(new ShippingConfirmation('ORDER-003', 'TRACK-003', '2024-12-25'));

    const order = new Order('ORDER-003', 'CUST-003', [new OrderItem('ITEM-C', 1, 10.0)], OrderStatus.Pending, 0, null, null);
    const result = new OrderService().placeOrder(order);

    expect(result.kind).toBe('success');
    if (result.kind === 'success') {
      expect(result.order.transactionId).toBe('TRANSACTION-XYZ-789');
    }
  });

  it('p1_trackingNumberIsStoredInConfirmedOrder', () => {
    vi.mocked(calculateTotal).mockReturnValue(10.0);
    vi.mocked(StockService.prototype.reserveStock).mockReturnValue([new StockReservation('ITEM-C', 1, true)]);
    vi.mocked(PaymentService.prototype.processPayment).mockReturnValue(new PaymentResult('ORDER-003', PaymentStatus.Success, 'TRANSACTION-XYZ-789'));
    vi.mocked(ShippingService.prototype.createShipment).mockReturnValue(new ShippingConfirmation('ORDER-003', 'TRACK-003', '2024-12-25'));

    const order = new Order('ORDER-003', 'CUST-003', [new OrderItem('ITEM-C', 1, 10.0)], OrderStatus.Pending, 0, null, null);
    const result = new OrderService().placeOrder(order);

    expect(result.kind).toBe('success');
    if (result.kind === 'success') {
      expect(result.order.trackingNumber).toBe('TRACK-003');
    }
  });

  // ── Stock unavailable ─────────────────────────────────────────────────────

  it('p1_orderFails_whenStockIsUnavailable', () => {
    vi.mocked(StockService.prototype.reserveStock).mockReturnValue([new StockReservation('PROD-001', 1, false)]);

    const result = new OrderService().placeOrder(new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 20.0)], OrderStatus.Pending, 0.0, null, null));

    expect(result.kind).toBe('failure');
    if (result.kind === 'failure') {
      expect(result.reason).toBe('Insufficient stock');
    }
  });

  it('p1_paymentIsNotCharged_whenStockIsUnavailable', () => {
    vi.mocked(StockService.prototype.reserveStock).mockReturnValue([new StockReservation('PROD-001', 1, false)]);

    new OrderService().placeOrder(new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 20.0)], OrderStatus.Pending, 0.0, null, null));

    expect(paymentMock().processPayment).not.toHaveBeenCalled();
  });

  it('p1_shipmentIsNotCreated_whenStockIsUnavailable', () => {
    vi.mocked(StockService.prototype.reserveStock).mockReturnValue([new StockReservation('PROD-001', 1, false)]);

    new OrderService().placeOrder(new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 20.0)], OrderStatus.Pending, 0.0, null, null));

    expect(shippingMock().createShipment).not.toHaveBeenCalled();
  });

  // ── Payment declined ──────────────────────────────────────────────────────

  it('p1_orderFails_whenPaymentIsDeclined', () => {
    vi.mocked(StockService.prototype.reserveStock).mockReturnValue([new StockReservation('PROD-001', 1, true)]);
    vi.mocked(calculateTotal).mockReturnValue(45.0);
    vi.mocked(PaymentService.prototype.processPayment).mockReturnValue(new PaymentResult('ORDER-001', PaymentStatus.Failed, null));

    const result = new OrderService().placeOrder(new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 20.0)], OrderStatus.Pending, 0.0, null, null));

    expect(result.kind).toBe('failure');
    if (result.kind === 'failure') {
      expect(result.reason).toBe('Payment failed');
    }
  });

  it('p1_shipmentIsNotCreated_whenPaymentIsDeclined', () => {
    vi.mocked(StockService.prototype.reserveStock).mockReturnValue([new StockReservation('PROD-001', 1, true)]);
    vi.mocked(calculateTotal).mockReturnValue(45.0);
    vi.mocked(PaymentService.prototype.processPayment).mockReturnValue(new PaymentResult('ORDER-001', PaymentStatus.Failed, null));

    new OrderService().placeOrder(new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 20.0)], OrderStatus.Pending, 0.0, null, null));

    expect(shippingMock().createShipment).not.toHaveBeenCalled();
  });

  it('p1_stockIsReleased_whenPaymentIsDeclined', () => {
    vi.mocked(StockService.prototype.reserveStock).mockReturnValue([new StockReservation('PROD-001', 1, true)]);
    vi.mocked(calculateTotal).mockReturnValue(45.0);
    vi.mocked(PaymentService.prototype.processPayment).mockReturnValue(new PaymentResult('ORDER-001', PaymentStatus.Failed, null));

    new OrderService().placeOrder(new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 20.0)], OrderStatus.Pending, 0.0, null, null));

    expect(stockMock().releaseStock).toHaveBeenCalled();
  });
});
