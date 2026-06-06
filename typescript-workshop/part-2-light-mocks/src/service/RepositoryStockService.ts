import { OrderItem } from '../domain/OrderItem';
import { StockReservation } from '../domain/StockReservation';
import { StockRepository } from '../repository/StockRepository';
import { StockService } from './StockService';

export class RepositoryStockService implements StockService {
  constructor(private readonly stockRepository: StockRepository) {}

  reserveStock(items: ReadonlyArray<OrderItem>): StockReservation[] {
    return items.map((item) => {
      const stock = this.stockRepository.findByProductId(item.productId);
      if (stock !== null && stock.availableQuantity >= item.quantity) {
        this.stockRepository.updateQuantity(item.productId, stock.availableQuantity - item.quantity);
        return new StockReservation(item.productId, item.quantity, true);
      }
      return new StockReservation(item.productId, item.quantity, false);
    });
  }

  releaseStock(reservations: StockReservation[]): void {
    for (const reservation of reservations) {
      if (reservation.reserved) {
        const stock = this.stockRepository.findByProductId(reservation.productId);
        if (stock !== null) {
          this.stockRepository.updateQuantity(reservation.productId, stock.availableQuantity + reservation.quantity);
        }
      }
    }
  }
}
