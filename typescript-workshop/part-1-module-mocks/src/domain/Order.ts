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

  // CORRECT version: takes transactionId AND trackingNumber
  // (unlike Parts 2-4 which ignore them — that is THE TRAP)
  confirm(totalPrice: number, transactionId: string | null, trackingNumber: string | null): Order {
    return new Order(
      this.id,
      this.customerId,
      this.items,
      OrderStatus.Confirmed,
      totalPrice,
      transactionId,
      trackingNumber,
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
