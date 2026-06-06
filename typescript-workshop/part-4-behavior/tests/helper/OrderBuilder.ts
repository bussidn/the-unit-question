import { Order } from '../../src/domain/Order';
import { OrderItem } from '../../src/domain/OrderItem';
import { OrderStatus } from '../../src/domain/OrderStatus';

export class OrderBuilder {
  private id = 'ORDER-DEFAULT';
  private customerId = 'CUST-001';
  private items: OrderItem[] = [new OrderItem('PROD-001', 1, 20.0)];
  private status = OrderStatus.Pending;
  private totalPrice = 20.0;
  private transactionId: string | null = null;
  private trackingNumber: string | null = null;

  private constructor() {}

  static anOrder(): OrderBuilder { return new OrderBuilder(); }
  static confirmed(): OrderBuilder { return new OrderBuilder().withStatus(OrderStatus.Confirmed); }

  withId(id: string): OrderBuilder { this.id = id; return this; }
  withCustomerId(customerId: string): OrderBuilder { this.customerId = customerId; return this; }
  withItems(items: OrderItem[]): OrderBuilder { this.items = [...items]; return this; }
  withStatus(status: OrderStatus): OrderBuilder { this.status = status; return this; }
  withTotalPrice(totalPrice: number): OrderBuilder { this.totalPrice = totalPrice; return this; }
  withTransaction(transactionId: string): OrderBuilder { this.transactionId = transactionId; return this; }
  withoutTransaction(): OrderBuilder { this.transactionId = null; return this; }
  withTracking(trackingNumber: string): OrderBuilder { this.trackingNumber = trackingNumber; return this; }
  withoutTracking(): OrderBuilder { this.trackingNumber = null; return this; }
  build(): Order { return new Order(this.id, this.customerId, this.items, this.status, this.totalPrice, this.transactionId, this.trackingNumber); }
}
