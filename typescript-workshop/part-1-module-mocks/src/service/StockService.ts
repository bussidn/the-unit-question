import { OrderItem } from '../domain/OrderItem';
import { StockReservation } from '../domain/StockReservation';

export class StockService {
  reserveStock(items: ReadonlyArray<OrderItem>): StockReservation[] {
    throw new Error('Not implemented — requires database connection');
  }

  releaseStock(reservations: StockReservation[]): void {
    throw new Error('Not implemented — requires database connection');
  }
}
