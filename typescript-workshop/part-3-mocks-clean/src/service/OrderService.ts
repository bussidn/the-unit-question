import { Order } from '../domain/Order';
import { OrderStatus } from '../domain/OrderStatus';
import { PaymentStatus } from '../domain/PaymentStatus';
import { OrderRepository } from '../repository/OrderRepository';
import { OrderResult } from './OrderResult';
import { PaymentService } from './PaymentService';
import { PricingService } from './PricingService';
import { ShippingService } from './ShippingService';
import { StockService } from './StockService';

export class OrderService {
  constructor(
    private readonly orderRepository: OrderRepository,
    private readonly stockService: StockService,
    private readonly pricingService: PricingService,
    private readonly paymentService: PaymentService,
    private readonly shippingService: ShippingService,
  ) {}

  placeOrder(order: Order): OrderResult {
    if (!this.isValid(order)) {
      return OrderResult.failure(order.reject(), 'Invalid order');
    }

    const reservations = this.stockService.reserveStock(order.items);
    if (reservations.some((r) => !r.reserved)) {
      return OrderResult.failure(order.reject(), 'Insufficient stock');
    }

    const totalPrice = this.pricingService.calculateTotal(order.items);
    const paymentResult = this.paymentService.processPayment(order.id, order.customerId, totalPrice);

    if (paymentResult.status !== PaymentStatus.Success) {
      this.stockService.releaseStock(reservations);
      return OrderResult.failure(order.reject(), 'Payment failed');
    }

    const shippingConfirmation = this.shippingService.createShipment(order);
    const confirmedOrder = order.confirm(totalPrice);
    this.orderRepository.save(confirmedOrder);
    return OrderResult.success(confirmedOrder, shippingConfirmation);
  }

  private isValid(order: Order): boolean {
    return order.status === OrderStatus.Pending && order.items.length > 0;
  }
}
