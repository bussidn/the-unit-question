import { OrderItem } from '../domain/OrderItem';
import { StockReservation } from '../domain/StockReservation';

export interface StockService {
  reserveStock(items: ReadonlyArray<OrderItem>): StockReservation[];
  releaseStock(reservations: StockReservation[]): void;
}
