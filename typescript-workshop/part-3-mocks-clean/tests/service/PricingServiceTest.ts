import { describe, it, expect } from 'vitest';
import { OrderItem } from '../../src/domain/OrderItem';
import { DiscountCode } from '../../src/domain/DiscountCode';
import { PricingService } from '../../src/service/PricingService';

describe('PricingService', () => {
  const pricingService = new PricingService();

  it('p3_calculateTotal_withNoDiscount_appliesShippingWhenSubtotalBelowThreshold', () => {
    const items = [new OrderItem('PROD-001', 1, 20.0)];
    expect(pricingService.calculateTotal(items)).toBeCloseTo(29.0, 2);
  });

  it('p3_calculateTotal_withNoDiscount_noShippingWhenSubtotalAtOrAboveThreshold', () => {
    const items = [new OrderItem('PROD-001', 5, 20.0)];
    expect(pricingService.calculateTotal(items)).toBeCloseTo(120.0, 2);
  });

  it('p3_calculateTotal_withDiscount_thresholdAppliedOnDiscountedSubtotal', () => {
    // 2 × €55 = subtotal 110, SUMMER20 = 20% off → discounted 88, tax 17.60, shipping 5 = 110.60
    const items = [new OrderItem('PROD-001', 2, 55.0)];
    expect(pricingService.calculateTotal(items, DiscountCode.SUMMER20)).toBeCloseTo(110.60, 2);
  });

  it('p3_calculateTotal_withDiscount_noShippingWhenDiscountedSubtotalAboveThreshold', () => {
    const items = [new OrderItem('PROD-001', 3, 70.0)];
    expect(pricingService.calculateTotal(items, DiscountCode.SUMMER20)).toBeCloseTo(201.60, 2);
  });

  it('p3_calculateTotal_withONCE50_halvesPriceAndAppliesShipping', () => {
    const items = [new OrderItem('PROD-001', 2, 55.0)];
    expect(pricingService.calculateTotal(items, DiscountCode.ONCE50)).toBeCloseTo(71.0, 2);
  });
});
