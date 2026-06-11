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
import type { ShippingService } from '../../src/service/ShippingService';
import type { StockService } from '../../src/service/StockService';
import { OrderService } from '../../src/service/OrderService';
import { PricingService } from '../../src/service/PricingService';
import { OrderBuilder } from '../helper/OrderBuilder';

describe('OrderService', () => {
  let stockService: { reserveStock: Mock; releaseStock: Mock };
  let paymentService: { processPayment: Mock; refundPayment: Mock };
  let shippingService: { createShipment: Mock; cancelShipment: Mock };
  let orderRepository: { findById: Mock; save: Mock; updateStatus: Mock };
  let orderService: OrderService;

  beforeEach(() => {
    stockService = { reserveStock: vi.fn(), releaseStock: vi.fn() };
    paymentService = { processPayment: vi.fn(), refundPayment: vi.fn() };
    shippingService = { createShipment: vi.fn(), cancelShipment: vi.fn() };
    orderRepository = { findById: vi.fn(), save: vi.fn(), updateStatus: vi.fn() };
    orderService = new OrderService(
      orderRepository as unknown as OrderRepository,
      stockService as unknown as StockService,
      new PricingService(), // real PricingService — no mock!
      paymentService as unknown as PaymentService,
      shippingService as unknown as ShippingService,
    );
  });

  // --- Given helpers ---

  function anOrder(): OrderBuilder {
    return OrderBuilder.anOrder()
      .withId('ORDER-001')
      .withCustomerId('CUST-001')
      .withItems([new OrderItem('PROD-001', 1, 20.0)]);
  }

  function givenPaymentSucceedsFor(order: Order): string {
    const txnId = `TXN-${order.id}`;
    paymentService.processPayment.mockReturnValue(
      new PaymentResult(order.id, PaymentStatus.Success, txnId),
    );
    return txnId;
  }

  function givenPaymentIsDeclinedFor(order: Order): void {
    paymentService.processPayment.mockReturnValue(
      new PaymentResult(order.id, PaymentStatus.Failed, null),
    );
  }

  function givenReservationSucceedsFor(order: Order): StockReservation[] {
    const reservations = order.items.map(
      (item) => new StockReservation(item.productId, item.quantity, true),
    );
    stockService.reserveStock.mockReturnValue(reservations);
    return reservations;
  }

  function givenReservationFailsFor(order: Order): StockReservation[] {
    const reservations = order.items.map(
      (item) => new StockReservation(item.productId, item.quantity, false),
    );
    stockService.reserveStock.mockReturnValue(reservations);
    return reservations;
  }

  function givenShipmentCreatedFor(order: Order): ShippingConfirmation {
    const shipment = new ShippingConfirmation(order.id, 'TRACK-456', '2024-12-25');
    shippingService.createShipment.mockReturnValue(shipment);
    return shipment;
  }

  // --- Exercise stubs (it.skip) ---

  it.skip('p3_orderIsRejected_whenDiscountCodeIsAlreadyUsed', () => {
    // Step 1 — Add discount code support to OrderService
    //
    // 1.1. Read PricingService.calculateTotal — notice it accepts an optional discountCode.
    // 1.2. Read OrderService.placeOrder — it doesn't use discountCode yet.
    // 1.3. Update OrderService to accept an optional discountCode parameter in placeOrder
    //      and pass it to PricingService.calculateTotal.
    // 1.4. Add a discountCodeService mock field and inject it via the OrderService constructor.
    //      Create a given helper (e.g. givenDiscountCodeIsAlreadyUsed).
    // 1.5. Write ARRANGE / ACT / ASSERT for the "discount code already used" scenario.
    // ✅ 1.6. Run: npx vitest run — then back to README for Step 2

    expect.fail("TODO: implement scenario 'discount code already used'");
  });

  it.skip('p3_orderIsConfirmed_whenDiscountCodeIsValid', () => {
    // Step 2 — Confirm order with a valid discount code
    //
    // THEN: expect result.order.totalPrice to be €105.60
    //
    // ✅ Run: npx vitest run — then back to README for Step 3

    expect.fail("TODO: implement scenario 'order with a valid discount code'");
  });

  it.skip('p3_discountCodeIsMarkedAsUsed_afterSuccessfulPayment', () => {
    // Step 3 — Mark the discount code as used after successful payment
    //
    // ✅ Run: npx vitest run — then back to README

    expect.fail("TODO: implement scenario 'discount code marked as used after payment'");
  });

  // --- Reference tests (all passing) ---

  it('p3_orderIsRejected_whenOrderIsAlreadyConfirmed', () => {
    const order = anOrder().withStatus(OrderStatus.Confirmed).build();

    const result = orderService.placeOrder(order);

    expect(result.kind).toBe('failure');
    if (result.kind === 'failure') {
      expect(result.reason).toBe('Invalid order');
    }
  });

  it('p3_orderIsRejected_whenOrderHasNoItems', () => {
    const order = anOrder().withItems([]).build();

    const result = orderService.placeOrder(order);

    expect(result.kind).toBe('failure');
    if (result.kind === 'failure') {
      expect(result.reason).toBe('Invalid order');
    }
  });

  it('p3_noStockIsReserved_whenOrderIsInvalid', () => {
    const order = anOrder().withItems([]).build();

    orderService.placeOrder(order);

    expect(stockService.reserveStock).not.toHaveBeenCalled();
  });

  it('p3_orderIsConfirmed_whenAllStepsSucceed', () => {
    const order = anOrder().build();
    givenPaymentSucceedsFor(order);
    givenReservationSucceedsFor(order);
    givenShipmentCreatedFor(order);

    const result = orderService.placeOrder(order);

    expect(result.kind).toBe('success');
    if (result.kind === 'success') {
      expect(result.order.status).toBe(OrderStatus.Confirmed);
    }
  });

  it('p3_calculatedPriceIsStoredInOrder_whenAllStepsSucceed', () => {
    // Real PricingService: 1 × €20 → subtotal 20 + 20% tax 4 + shipping 5 = 29
    const order = anOrder().build();
    givenPaymentSucceedsFor(order);
    givenReservationSucceedsFor(order);
    givenShipmentCreatedFor(order);

    const result = orderService.placeOrder(order);

    if (result.kind === 'success') {
      expect(result.order.totalPrice).toBeCloseTo(29.0, 2);
    }
  });

  it('p3_shippingConfirmationIsReturned_whenAllStepsSucceed', () => {
    const order = anOrder().build();
    givenPaymentSucceedsFor(order);
    givenReservationSucceedsFor(order);
    const shipment = givenShipmentCreatedFor(order);

    const result = orderService.placeOrder(order);

    if (result.kind === 'success') {
      expect(result.shippingConfirmation).toBe(shipment);
    }
  });

  it('p3_orderFails_whenStockIsUnavailable', () => {
    const order = anOrder().build();
    givenReservationFailsFor(order);

    const result = orderService.placeOrder(order);

    expect(result.kind).toBe('failure');
    if (result.kind === 'failure') {
      expect(result.reason).toBe('Insufficient stock');
    }
  });

  it('p3_paymentIsNotCharged_whenStockIsUnavailable', () => {
    const order = anOrder().build();
    givenReservationFailsFor(order);

    orderService.placeOrder(order);

    expect(paymentService.processPayment).not.toHaveBeenCalled();
  });

  it('p3_shipmentIsNotCreated_whenStockIsUnavailable', () => {
    const order = anOrder().build();
    givenReservationFailsFor(order);

    orderService.placeOrder(order);

    expect(shippingService.createShipment).not.toHaveBeenCalled();
  });

  it('p3_orderFails_whenPaymentIsDeclined', () => {
    const order = anOrder().build();
    givenReservationSucceedsFor(order);
    givenPaymentIsDeclinedFor(order);

    const result = orderService.placeOrder(order);

    expect(result.kind).toBe('failure');
    if (result.kind === 'failure') {
      expect(result.reason).toBe('Payment failed');
    }
  });

  it('p3_shipmentIsNotCreated_whenPaymentIsDeclined', () => {
    const order = anOrder().build();
    givenReservationSucceedsFor(order);
    givenPaymentIsDeclinedFor(order);

    orderService.placeOrder(order);

    expect(shippingService.createShipment).not.toHaveBeenCalled();
  });

  it('p3_stockIsReleased_whenPaymentIsDeclined', () => {
    const order = anOrder().build();
    const reservations = givenReservationSucceedsFor(order);
    givenPaymentIsDeclinedFor(order);

    orderService.placeOrder(order);

    expect(stockService.releaseStock).toHaveBeenCalledWith(reservations);
  });
});
