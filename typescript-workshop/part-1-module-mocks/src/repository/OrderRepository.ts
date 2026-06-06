import { Order } from '../domain/Order';
import { OrderStatus } from '../domain/OrderStatus';

export class OrderRepository {
  save(order: Order): void {
    throw new Error('Not implemented — requires database connection');
  }

  findById(id: string): Order | null {
    throw new Error('Not implemented — requires database connection');
  }

  updateStatus(id: string, status: OrderStatus): void {
    throw new Error('Not implemented — requires database connection');
  }
}
