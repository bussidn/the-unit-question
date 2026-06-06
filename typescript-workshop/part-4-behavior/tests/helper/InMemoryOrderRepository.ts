import { Order } from '../../src/domain/Order';
import { OrderStatus } from '../../src/domain/OrderStatus';
import { OrderRepository } from '../../src/repository/OrderRepository';

export class InMemoryOrderRepository implements OrderRepository {
  private readonly orders = new Map<string, Order>();

  findById(id: string): Order | null {
    return this.orders.get(id) ?? null;
  }

  save(order: Order): void {
    this.orders.set(order.id, order);
  }

  updateStatus(id: string, status: OrderStatus): void {
    const order = this.orders.get(id);
    if (order !== undefined) {
      this.orders.set(id, new Order(
        order.id,
        order.customerId,
        order.items,
        status,
        order.totalPrice,
        order.transactionId,
        order.trackingNumber,
      ));
    }
  }

  with(order: Order): this {
    this.orders.set(order.id, order);
    return this;
  }
}
