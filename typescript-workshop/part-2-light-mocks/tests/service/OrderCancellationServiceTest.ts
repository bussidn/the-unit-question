import { describe, it, expect, vi, type Mock } from 'vitest';
import { Order } from '../../src/domain/Order';
import { OrderItem } from '../../src/domain/OrderItem';
import { OrderStatus } from '../../src/domain/OrderStatus';
import { StockReservation } from '../../src/domain/StockReservation';
import type { OrderRepository } from '../../src/repository/OrderRepository';
import type { PaymentService } from '../../src/service/PaymentService';
import type { ShippingService } from '../../src/service/ShippingService';
import type { StockService } from '../../src/service/StockService';
import { OrderCancellationService } from '../../src/service/OrderCancellationService';

describe('OrderCancellationService', () => {
  it('p2_cancelOrder_refundsPayment_whenOrderExists', () => {
    const orderRepository = { findById: vi.fn(), save: vi.fn(), updateStatus: vi.fn() } as unknown as OrderRepository;
    const paymentService = { processPayment: vi.fn(), refundPayment: vi.fn() } as unknown as PaymentService;
    const stockService = { reserveStock: vi.fn(), releaseStock: vi.fn() } as unknown as StockService;
    const shippingService = { createShipment: vi.fn(), cancelShipment: vi.fn() } as unknown as ShippingService;
    const service = new OrderCancellationService(orderRepository, paymentService, stockService, shippingService);

    const order = new Order(
      'ORDER-001',
      'CUST-001',
      [new OrderItem('PROD-001', 2, 25.0)],
      OrderStatus.Confirmed,
      50.0,
      'TXN-123',
      null,
    );

    (orderRepository as any).findById.mockReturnValue(order);
    (paymentService as any).refundPayment.mockReturnValue(true);

    const result = service.cancelOrder('ORDER-001');

    expect(result.kind).toBe('success');
    expect((paymentService as any).refundPayment).toHaveBeenCalledWith('TXN-123');
    expect((stockService as any).releaseStock).toHaveBeenCalledWith([new StockReservation('PROD-001', 2, true)]);
    expect((orderRepository as any).updateStatus).toHaveBeenCalledWith('ORDER-001', OrderStatus.Cancelled);
    expect((shippingService as any).cancelShipment).not.toHaveBeenCalled();
  });

  it('p2_cancelOrder_cancelsShipment_whenTrackingNumberExists', () => {
    const orderRepository = { findById: vi.fn(), save: vi.fn(), updateStatus: vi.fn() } as unknown as OrderRepository;
    const paymentService = { processPayment: vi.fn(), refundPayment: vi.fn() } as unknown as PaymentService;
    const stockService = { reserveStock: vi.fn(), releaseStock: vi.fn() } as unknown as StockService;
    const shippingService = { createShipment: vi.fn(), cancelShipment: vi.fn() } as unknown as ShippingService;
    const service = new OrderCancellationService(orderRepository, paymentService, stockService, shippingService);

    const order = new Order(
      'ORDER-001',
      'CUST-001',
      [new OrderItem('PROD-001', 2, 25.0)],
      OrderStatus.Confirmed,
      50.0,
      'TXN-123',
      'TRACK-456',
    );

    (orderRepository as any).findById.mockReturnValue(order);
    (paymentService as any).refundPayment.mockReturnValue(true);
    (shippingService as any).cancelShipment.mockReturnValue(true);

    const result = service.cancelOrder('ORDER-001');

    expect(result.kind).toBe('success');
    expect((paymentService as any).refundPayment).toHaveBeenCalledWith('TXN-123');
    expect((stockService as any).releaseStock).toHaveBeenCalledWith([new StockReservation('PROD-001', 2, true)]);
    expect((shippingService as any).cancelShipment).toHaveBeenCalledWith('TRACK-456');
    expect((orderRepository as any).updateStatus).toHaveBeenCalledWith('ORDER-001', OrderStatus.Cancelled);
  });

  it('p2_cancelOrder_fails_whenRefundFails', () => {
    const orderRepository = { findById: vi.fn(), save: vi.fn(), updateStatus: vi.fn() } as unknown as OrderRepository;
    const paymentService = { processPayment: vi.fn(), refundPayment: vi.fn() } as unknown as PaymentService;
    const stockService = { reserveStock: vi.fn(), releaseStock: vi.fn() } as unknown as StockService;
    const shippingService = { createShipment: vi.fn(), cancelShipment: vi.fn() } as unknown as ShippingService;
    const service = new OrderCancellationService(orderRepository, paymentService, stockService, shippingService);

    const order = new Order(
      'ORDER-001',
      'CUST-001',
      [new OrderItem('PROD-001', 2, 25.0)],
      OrderStatus.Confirmed,
      50.0,
      'TXN-123',
      'TRACK-456',
    );

    (orderRepository as any).findById.mockReturnValue(order);
    (paymentService as any).refundPayment.mockReturnValue(false);

    const result = service.cancelOrder('ORDER-001');

    expect(result.kind).toBe('failure');
    if (result.kind === 'failure') {
      expect(result.reason).toBe('Refund failed');
    }
    expect((paymentService as any).refundPayment).toHaveBeenCalledWith('TXN-123');
    expect((stockService as any).releaseStock).not.toHaveBeenCalled();
    expect((shippingService as any).cancelShipment).not.toHaveBeenCalled();
    expect((orderRepository as any).updateStatus).not.toHaveBeenCalled();
  });

  it('p2_cancelOrder_fails_whenOrderDoesNotExist', () => {
    const orderRepository = { findById: vi.fn(), save: vi.fn(), updateStatus: vi.fn() } as unknown as OrderRepository;
    const paymentService = { processPayment: vi.fn(), refundPayment: vi.fn() } as unknown as PaymentService;
    const stockService = { reserveStock: vi.fn(), releaseStock: vi.fn() } as unknown as StockService;
    const shippingService = { createShipment: vi.fn(), cancelShipment: vi.fn() } as unknown as ShippingService;
    const service = new OrderCancellationService(orderRepository, paymentService, stockService, shippingService);

    (orderRepository as any).findById.mockReturnValue(null);

    const result = service.cancelOrder('ORDER-001');

    expect(result.kind).toBe('failure');
    if (result.kind === 'failure') {
      expect(result.reason).toBe('Order not found');
    }
    expect((orderRepository as any).findById).toHaveBeenCalledWith('ORDER-001');
    expect((paymentService as any).processPayment).not.toHaveBeenCalled();
    expect((stockService as any).releaseStock).not.toHaveBeenCalled();
    expect((shippingService as any).cancelShipment).not.toHaveBeenCalled();
  });
});
