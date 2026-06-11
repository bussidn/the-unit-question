// In Part 1, ShippingService is a concrete class with no dependencies injected.
// It is always used via vi.mock() in OrderServiceTest, never tested in isolation
// (it would just throw "Not implemented — requires shipping API connection").
//
// This file documents the vi.mock() pattern for ShippingService.

vi.mock('../../src/service/ShippingService');

import { describe, it, expect, vi } from 'vitest';
import { Order } from '../../src/domain/Order';
import { OrderItem } from '../../src/domain/OrderItem';
import { OrderStatus } from '../../src/domain/OrderStatus';
import { ShippingConfirmation } from '../../src/domain/ShippingConfirmation';
import { ShippingService } from '../../src/service/ShippingService';

describe('ShippingService (module mock pattern)', () => {
  it('p1_shippingService_canBeFullyControlledViaModuleMock', () => {
    // When vi.mock() is used, the constructor is intercepted and all methods
    // become vi.fn() automatically — no real implementation is called.
    const confirmation = new ShippingConfirmation('ORDER-001', 'TRACK-ABC', '2024-12-25');
    vi.mocked(ShippingService.prototype.createShipment).mockReturnValue(confirmation);

    const service = new ShippingService();
    const order = new Order('ORDER-001', 'CUST-001', [new OrderItem('PROD-001', 1, 10.0)], OrderStatus.Pending, 0, null, null);
    const result = service.createShipment(order);

    expect(result).toBe(confirmation);
    // The real implementation (which throws) was never called
    expect(vi.mocked(ShippingService.prototype.createShipment)).toHaveBeenCalledOnce();
  });
});
