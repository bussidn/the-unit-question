import { Order } from '../domain/Order';
import { OrderStatus } from '../domain/OrderStatus';

export interface OrderRepository {
  findById(orderId: string): Order | null;
  save(order: Order): void;
  updateStatus(orderId: string, status: OrderStatus): void;
}
