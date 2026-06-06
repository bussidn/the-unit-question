import { describe, it, expect, vi } from 'vitest';
import { OrderItem } from '../../src/domain/OrderItem';
import { Stock } from '../../src/domain/Stock';
import { StockReservation } from '../../src/domain/StockReservation';
import type { StockRepository } from '../../src/repository/StockRepository';
import { RepositoryStockService } from '../../src/service/RepositoryStockService';

describe('StockService', () => {
  it('p3_reserveStock_updatesRepositoryAndReturnsReservations', () => {
    const stockRepository = { findByProductId: vi.fn(), updateQuantity: vi.fn() } as unknown as StockRepository;
    const stockService = new RepositoryStockService(stockRepository);

    const items = [new OrderItem('PROD-001', 5, 10.0)];

    (stockRepository as any).findByProductId.mockReturnValue(new Stock('PROD-001', 100));

    const result = stockService.reserveStock(items);

    expect(result).toHaveLength(1);
    expect(result[0].reserved).toBe(true);
    expect((stockRepository as any).updateQuantity).toHaveBeenCalledWith('PROD-001', 95);
  });

  it('p3_releaseStock_restoresQuantity_forSuccessfulReservations', () => {
    const stockRepository = { findByProductId: vi.fn(), updateQuantity: vi.fn() } as unknown as StockRepository;
    const stockService = new RepositoryStockService(stockRepository);

    const reservations = [
      new StockReservation('PROD-001', 5, true),
      new StockReservation('PROD-002', 2, false),
    ];

    (stockRepository as any).findByProductId.mockReturnValue(new Stock('PROD-001', 90));

    stockService.releaseStock(reservations);

    expect((stockRepository as any).updateQuantity).toHaveBeenCalledWith('PROD-001', 95);
    expect((stockRepository as any).findByProductId).not.toHaveBeenCalledWith('PROD-002');
  });
});
