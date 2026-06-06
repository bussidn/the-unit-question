import { describe, it, expect } from 'vitest';
import { OrderItem } from '../../src/domain/OrderItem';
import { StockReservation } from '../../src/domain/StockReservation';
import { RepositoryStockService } from '../../src/service/RepositoryStockService';
import { InMemoryStockRepository } from '../helper/InMemoryStockRepository';

describe('StockService', () => {
  it('p4_reserveStock_updatesRepositoryAndReturnsReservations', () => {
    const stockRepository = new InMemoryStockRepository()
      .withAvailableStock('PROD-001', 100);
    const stockService = new RepositoryStockService(stockRepository);

    const items = [new OrderItem('PROD-001', 5, 10.0)];

    const result = stockService.reserveStock(items);

    expect(result).toHaveLength(1);
    expect(result[0].reserved).toBe(true);
    expect(stockRepository.availableStock('PROD-001')).toBe(95);
  });

  it('p4_reserveStock_returnsUnreserved_whenStockIsInsufficient', () => {
    const stockRepository = new InMemoryStockRepository()
      .withAvailableStock('PROD-001', 2);
    const stockService = new RepositoryStockService(stockRepository);

    const items = [new OrderItem('PROD-001', 5, 10.0)];

    const result = stockService.reserveStock(items);

    expect(result).toHaveLength(1);
    expect(result[0].reserved).toBe(false);
  });

  it('p4_releaseStock_restoresQuantity_forSuccessfulReservations', () => {
    const stockRepository = new InMemoryStockRepository()
      .withAvailableStock('PROD-001', 90);
    const stockService = new RepositoryStockService(stockRepository);

    const reservations = [
      new StockReservation('PROD-001', 5, true),
      new StockReservation('PROD-002', 2, false),
    ];

    stockService.releaseStock(reservations);

    expect(stockRepository.availableStock('PROD-001')).toBe(95);
    expect(stockRepository.availableStock('PROD-002')).toBe(0);
  });
});
