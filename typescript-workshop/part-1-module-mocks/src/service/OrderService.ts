import { DiscountCode } from '../domain/DiscountCode';
import { Order } from '../domain/Order';
import { OrderStatus } from '../domain/OrderStatus';
import { PaymentStatus } from '../domain/PaymentStatus';
import { OrderRepository } from '../repository/OrderRepository';
import { calculateTotal, calculateTotalWithDiscount } from './PricingService';
import { OrderResult } from './OrderResult';
import { PaymentService } from './PaymentService';
import { ShippingService } from './ShippingService';
import { StockService } from './StockService';

export class OrderService {
  placeOrder(order: Order, discountCode?: DiscountCode): OrderResult {
    const stockService = new StockService();
    const paymentService = new PaymentService();
    const shippingService = new ShippingService();
    const orderRepository = new OrderRepository();

    if (!this.isValid(order)) {
      return OrderResult.failure(order.reject(), 'Invalid order');
    }

    const reservations = stockService.reserveStock(order.items);
    if (reservations.some((r) => !r.reserved)) {
      return OrderResult.failure(order.reject(), 'Insufficient stock');
    }

    const totalPrice = discountCode !== undefined
      ? calculateTotalWithDiscount(order.items, discountCode)
      : calculateTotal(order.items);

    const paymentResult = paymentService.processPayment(order.id, order.customerId, totalPrice);
    if (paymentResult.status !== PaymentStatus.Success) {
      stockService.releaseStock(reservations);
      return OrderResult.failure(order.reject(), 'Payment failed');
    }

    const shippingConfirmation = shippingService.createShipment(order);
    const confirmedOrder = order.confirm(
      totalPrice,
      paymentResult.transactionId,
      shippingConfirmation.trackingNumber,
    );

    orderRepository.save(confirmedOrder);
    return OrderResult.success(confirmedOrder, shippingConfirmation);
  }

  isValid(order: Order): boolean {
    return order.status === OrderStatus.Pending && order.items.length > 0;
  }
}
