import { describe, it, expect } from 'vitest';
import { Order } from '../../src/domain/Order';
import { OrderItem } from '../../src/domain/OrderItem';
import { OrderStatus } from '../../src/domain/OrderStatus';
import { GatewayShippingService } from '../../src/service/GatewayShippingService';
import { InMemoryShippingGateway } from '../helper/InMemoryShippingGateway';

describe('ShippingService', () => {
  it('p4_createShipment_callsGatewayAndReturnsConfirmation', () => {
    const shippingGateway = new InMemoryShippingGateway();
    const shippingService = new GatewayShippingService(shippingGateway);

    const order = new Order(
      'ORDER-123',
      'CUST-456',
      [new OrderItem('PROD-001', 2, 10.0), new OrderItem('PROD-002', 1, 20.0)],
      OrderStatus.Pending,
      0.0,
      null,
      null,
    );

    const result = shippingService.createShipment(order);

    expect(result.orderId).toBe('ORDER-123');
    expect(result.trackingNumber).toBe('TRACK-ORDER-123');
    expect(shippingGateway.hasCreatedShipment()).toBe(true);
    expect(shippingGateway.lastConfirmation()).not.toBeNull();
  });

  it('p4_cancelShipment_callsGatewayAndReturnsResult', () => {
    const shippingGateway = new InMemoryShippingGateway();
    const shippingService = new GatewayShippingService(shippingGateway);

    expect(shippingService.cancelShipment('TRACK-ABC123')).toBe(true);
  });
});
