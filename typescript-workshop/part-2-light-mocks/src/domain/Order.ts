import { OrderItem } from './OrderItem';
import { OrderStatus } from './OrderStatus';

export class Order {
  constructor(
    readonly id: string,
    readonly customerId: string,
    readonly items: ReadonlyArray<OrderItem>,
    readonly status: OrderStatus,
    readonly totalPrice: number,
    readonly transactionId: string | null,
    readonly trackingNumber: string | null,
  ) {}

  confirm(totalPrice: number): Order {
    return new Order(
      this.id,
      this.customerId,
      this.items,
      OrderStatus.Confirmed,
      totalPrice,
      this.transactionId,
      this.trackingNumber,
    );
  }

  reject(): Order {
    return new Order(
      this.id,
      this.customerId,
      this.items,
      OrderStatus.Rejected,
      this.totalPrice,
      this.transactionId,
      this.trackingNumber,
    );
  }
}
