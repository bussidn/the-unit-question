import { describe, it, expect } from 'vitest';
import { OrderItem } from '../../src/domain/OrderItem';
import { DiscountCode } from '../../src/domain/DiscountCode';
import { PricingService } from '../../src/service/PricingService';

describe('PricingService', () => {
  const pricingService = new PricingService();

  // ─── Without discount code ────────────────────────────────────────────────

  it('p2_calculateTotal_withNoDiscount_appliesShippingWhenSubtotalBelowThreshold', () => {
    const items = [new OrderItem('PROD-001', 1, 20.0)];
    // subtotal = 20, tax = 4, shipping = 5 → 29.0
    expect(pricingService.calculateTotal(items)).toBeCloseTo(29.0, 2);
  });

  it('p2_calculateTotal_withNoDiscount_noShippingWhenSubtotalAtOrAboveThreshold', () => {
    const items = [new OrderItem('PROD-001', 5, 20.0)];
    // subtotal = 100, tax = 20, shipping = 0 → 120.0
    expect(pricingService.calculateTotal(items)).toBeCloseTo(120.0, 2);
  });

  // ─── With discount code ───────────────────────────────────────────────────

  it('p2_calculateTotal_withDiscount_thresholdAppliedOnDiscountedSubtotal', () => {
    const items = [new OrderItem('PROD-001', 2, 55.0)];
    // gross subtotal = 110, after SUMMER20 (-20%) = 88
    // 88 < 100 → shipping = 5, tax = 17.60 → total = 110.60
    expect(pricingService.calculateTotal(items, DiscountCode.SUMMER20)).toBeCloseTo(110.60, 2);
  });

  it('p2_calculateTotal_withDiscount_noShippingWhenDiscountedSubtotalAboveThreshold', () => {
    const items = [new OrderItem('PROD-001', 3, 70.0)];
    // gross subtotal = 210, after SUMMER20 (-20%) = 168
    // 168 >= 100 → shipping = 0, tax = 33.60 → total = 201.60
    expect(pricingService.calculateTotal(items, DiscountCode.SUMMER20)).toBeCloseTo(201.60, 2);
  });

  it('p2_calculateTotal_withONCE50_halvesPriceAndAppliesShipping', () => {
    const items = [new OrderItem('PROD-001', 2, 55.0)];
    // gross subtotal = 110, after ONCE50 (-50%) = 55
    // 55 < 100 → shipping = 5, tax = 11 → total = 71.0
    expect(pricingService.calculateTotal(items, DiscountCode.ONCE50)).toBeCloseTo(71.0, 2);
  });
});
