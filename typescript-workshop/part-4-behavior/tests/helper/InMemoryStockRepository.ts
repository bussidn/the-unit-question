import { Stock } from '../../src/domain/Stock';
import { StockRepository } from '../../src/repository/StockRepository';

export class InMemoryStockRepository implements StockRepository {
  private readonly stocks = new Map<string, number>();

  findByProductId(productId: string): Stock | null {
    const quantity = this.stocks.get(productId);
    if (quantity === undefined) return null;
    return new Stock(productId, quantity);
  }

  updateQuantity(productId: string, newQuantity: number): void {
    this.stocks.set(productId, newQuantity);
  }

  withAvailableStock(productId: string, quantity: number): this {
    this.stocks.set(productId, quantity);
    return this;
  }

  availableStock(productId: string): number {
    return this.stocks.get(productId) ?? 0;
  }
}
