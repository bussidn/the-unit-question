import { describe, it, expect } from 'vitest';
import { OrderItem } from '../../src/domain/OrderItem';
import { OrderStatus } from '../../src/domain/OrderStatus';
import { StockReservation } from '../../src/domain/StockReservation';
import { GatewayPaymentService } from '../../src/service/GatewayPaymentService';
import { GatewayShippingService } from '../../src/service/GatewayShippingService';
import { OrderCancellationService } from '../../src/service/OrderCancellationService';
import { RepositoryStockService } from '../../src/service/RepositoryStockService';
import { InMemoryOrderRepository } from '../helper/InMemoryOrderRepository';
import { InMemoryPaymentGateway } from '../helper/InMemoryPaymentGateway';
import { InMemoryShippingGateway } from '../helper/InMemoryShippingGateway';
import { InMemoryStockRepository } from '../helper/InMemoryStockRepository';
import { OrderBuilder } from '../helper/OrderBuilder';

describe('OrderCancellationService', () => {
  it('p4_cancelOrder_refundsPayment_whenOrderExists', () => {
    const orderRepository = new InMemoryOrderRepository();
    const paymentGateway = new InMemoryPaymentGateway();
    const stockRepository = new InMemoryStockRepository();
    const shippingGateway = new InMemoryShippingGateway();
    const service = new OrderCancellationService(
      orderRepository,
      new GatewayPaymentService(paymentGateway),
      new RepositoryStockService(stockRepository),
      new GatewayShippingService(shippingGateway),
    );

    const order = OrderBuilder.confirmed()
      .withId('ORDER-001').withCustomerId('CUST-001')
      .withItems([new OrderItem('PROD-001', 2, 25.0)])
      .withTotalPrice(50.0)
      .withTransaction('TXN-123')
      .withoutTracking()
      .build();

    orderRepository.with(order);
    stockRepository.withAvailableStock('PROD-001', 0);

    const result = service.cancelOrder('ORDER-001');

    expect(result.kind).toBe('success');
    expect(paymentGateway.wasRefunded('TXN-123')).toBe(true);
    expect(stockRepository.availableStock('PROD-001')).toBe(2);
    expect(shippingGateway.hasCreatedShipment()).toBe(false);
  });

  it('p4_cancelOrder_cancelsShipment_whenTrackingNumberExists', () => {
    const orderRepository = new InMemoryOrderRepository();
    const paymentGateway = new InMemoryPaymentGateway();
    const stockRepository = new InMemoryStockRepository();
    const shippingGateway = new InMemoryShippingGateway();
    const service = new OrderCancellationService(
      orderRepository,
      new GatewayPaymentService(paymentGateway),
      new RepositoryStockService(stockRepository),
      new GatewayShippingService(shippingGateway),
    );

    const order = OrderBuilder.confirmed()
      .withId('ORDER-001').withCustomerId('CUST-001')
      .withItems([new OrderItem('PROD-001', 2, 25.0)])
      .withTotalPrice(50.0)
      .withTransaction('TXN-123')
      .withTracking('TRACK-456')
      .build();

    orderRepository.with(order);
    stockRepository.withAvailableStock('PROD-001', 0);

    const result = service.cancelOrder('ORDER-001');

    expect(result.kind).toBe('success');
    expect(paymentGateway.wasRefunded('TXN-123')).toBe(true);
    expect(stockRepository.availableStock('PROD-001')).toBe(2);
  });

  it('p4_cancelOrder_fails_whenOrderDoesNotExist', () => {
    const orderRepository = new InMemoryOrderRepository();
    const paymentGateway = new InMemoryPaymentGateway();
    const stockRepository = new InMemoryStockRepository();
    const shippingGateway = new InMemoryShippingGateway();
    const service = new OrderCancellationService(
      orderRepository,
      new GatewayPaymentService(paymentGateway),
      new RepositoryStockService(stockRepository),
      new GatewayShippingService(shippingGateway),
    );

    const result = service.cancelOrder('ORDER-001');

    expect(result.kind).toBe('failure');
    if (result.kind === 'failure') {
      expect(result.reason).toBe('Order not found');
    }
    expect(paymentGateway.wasCharged()).toBe(false);
  });

  it('p4_cancelOrder_updatesOrderStatusToCancelled', () => {
    const orderRepository = new InMemoryOrderRepository();
    const paymentGateway = new InMemoryPaymentGateway();
    const stockRepository = new InMemoryStockRepository();
    const shippingGateway = new InMemoryShippingGateway();
    const service = new OrderCancellationService(
      orderRepository,
      new GatewayPaymentService(paymentGateway),
      new RepositoryStockService(stockRepository),
      new GatewayShippingService(shippingGateway),
    );

    const order = OrderBuilder.confirmed()
      .withId('ORDER-001').withCustomerId('CUST-001')
      .withItems([new OrderItem('PROD-001', 1, 25.0)])
      .withTotalPrice(25.0)
      .withoutTransaction()
      .withoutTracking()
      .build();

    orderRepository.with(order);
    stockRepository.withAvailableStock('PROD-001', 0);

    service.cancelOrder('ORDER-001');

    const updated = orderRepository.findById('ORDER-001');
    expect(updated?.status).toBe(OrderStatus.Cancelled);
  });
});
