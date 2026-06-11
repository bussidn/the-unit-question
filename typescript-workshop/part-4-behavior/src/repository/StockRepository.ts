import { Stock } from '../domain/Stock';

export interface StockRepository {
  findByProductId(productId: string): Stock | null;
  updateQuantity(productId: string, newQuantity: number): void;
}
