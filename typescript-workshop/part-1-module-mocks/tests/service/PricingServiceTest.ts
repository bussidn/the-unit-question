import { describe, it, expect } from 'vitest';
import { OrderItem } from '../../src/domain/OrderItem';
import { DiscountCode } from '../../src/domain/DiscountCode';
import { calculateTotal, calculateTotalWithDiscount } from '../../src/service/PricingService';

// PricingService is a module with exported functions (not a class).
// These tests verify the real pricing logic without any mocking.

describe('PricingService', () => {
  // ─── calculateTotal (no discount) ────────────────────────────────────────

  it('p1_calculateTotal_withNoDiscount_appliesShippingWhenSubtotalBelowThreshold', () => {
    const items = [new OrderItem('PROD-001', 1, 20.0)];
    // subtotal = 20, tax = 4, shipping = 5 → 29.0
    expect(calculateTotal(items)).toBeCloseTo(29.0, 2);
  });

  it('p1_calculateTotal_withNoDiscount_noShippingWhenSubtotalAtOrAboveThreshold', () => {
    const items = [new OrderItem('PROD-001', 5, 20.0)];
    // subtotal = 100, tax = 20, shipping = 0 → 120.0
    expect(calculateTotal(items)).toBeCloseTo(120.0, 2);
  });

  // ─── calculateTotalWithDiscount ───────────────────────────────────────────

  it('p1_calculateTotalWithDiscount_thresholdAppliedOnDiscountedSubtotal', () => {
    const items = [new OrderItem('PROD-001', 2, 55.0)];
    // gross subtotal = 110, after SUMMER20 (-20%) = 88
    // 88 < 100 → shipping = 5, tax = 17.60 → total = 110.60
    expect(calculateTotalWithDiscount(items, DiscountCode.SUMMER20)).toBeCloseTo(110.60, 2);
  });

  it('p1_calculateTotalWithDiscount_noShippingWhenDiscountedSubtotalAboveThreshold', () => {
    const items = [new OrderItem('PROD-001', 3, 70.0)];
    // gross subtotal = 210, after SUMMER20 (-20%) = 168
    // 168 >= 100 → shipping = 0, tax = 33.60 → total = 201.60
    expect(calculateTotalWithDiscount(items, DiscountCode.SUMMER20)).toBeCloseTo(201.60, 2);
  });

  it('p1_calculateTotalWithDiscount_withONCE50_halvesPriceAndAppliesShipping', () => {
    const items = [new OrderItem('PROD-001', 2, 55.0)];
    // gross subtotal = 110, after ONCE50 (-50%) = 55
    // 55 < 100 → shipping = 5, tax = 11 → total = 71.0
    expect(calculateTotalWithDiscount(items, DiscountCode.ONCE50)).toBeCloseTo(71.0, 2);
  });
});
