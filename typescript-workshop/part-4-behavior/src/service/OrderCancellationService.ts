import { OrderStatus } from '../domain/OrderStatus';
import { StockReservation } from '../domain/StockReservation';
import { CancellationResult } from '../domain/CancellationResult';
import { OrderRepository } from '../repository/OrderRepository';
import { PaymentService } from './PaymentService';
import { ShippingService } from './ShippingService';
import { StockService } from './StockService';

export class OrderCancellationService {
  constructor(
    private readonly orderRepository: OrderRepository,
    private readonly paymentService: PaymentService,
    private readonly stockService: StockService,
    private readonly shippingService: ShippingService,
  ) {}

  cancelOrder(orderId: string): CancellationResult {
    const order = this.orderRepository.findById(orderId);
    if (order === null) {
      return CancellationResult.failure('Order not found');
    }

    if (order.transactionId !== null) {
      const refunded = this.paymentService.refundPayment(order.transactionId);
      if (!refunded) {
        return CancellationResult.failure('Refund failed');
      }
    }

    const reservations = order.items.map(
      (item) => new StockReservation(item.productId, item.quantity, true),
    );
    this.stockService.releaseStock(reservations);

    if (order.trackingNumber !== null) {
      this.shippingService.cancelShipment(order.trackingNumber);
    }

    this.orderRepository.updateStatus(orderId, OrderStatus.Cancelled);
    return CancellationResult.success();
  }
}
