// In Part 1, StockService is a concrete class with no dependencies injected.
// It is always used via vi.mock() in OrderServiceTest, never tested in isolation
// (it would just throw "Not implemented — requires database connection").
//
// This file documents the vi.mock() pattern for StockService.

vi.mock('../../src/service/StockService');

import { describe, it, expect, vi } from 'vitest';
import { OrderItem } from '../../src/domain/OrderItem';
import { StockReservation } from '../../src/domain/StockReservation';
import { StockService } from '../../src/service/StockService';

describe('StockService (module mock pattern)', () => {
  it('p1_stockService_canBeFullyControlledViaModuleMock', () => {
    // When vi.mock() is used, the constructor is intercepted and all methods
    // become vi.fn() automatically — no real implementation is called.
    vi.mocked(StockService.prototype.reserveStock).mockReturnValue([
      new StockReservation('PROD-001', 2, true),
    ]);

    const service = new StockService();
    const result = service.reserveStock([new OrderItem('PROD-001', 2, 10.0)]);

    expect(result).toHaveLength(1);
    expect(result[0].reserved).toBe(true);
    // The real implementation (which throws) was never called
    expect(vi.mocked(StockService.prototype.reserveStock)).toHaveBeenCalledOnce();
  });
});
