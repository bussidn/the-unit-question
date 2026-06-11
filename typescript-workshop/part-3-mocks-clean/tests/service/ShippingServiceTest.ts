import { describe, it, expect, vi } from 'vitest';
import { Order } from '../../src/domain/Order';
import { OrderItem } from '../../src/domain/OrderItem';
import { OrderStatus } from '../../src/domain/OrderStatus';
import { ShippingConfirmation } from '../../src/domain/ShippingConfirmation';
import type { ShippingGateway } from '../../src/gateway/ShippingGateway';
import { GatewayShippingService } from '../../src/service/GatewayShippingService';

describe('ShippingService', () => {
  it('p3_createShipment_callsGatewayAndReturnsConfirmation', () => {
    const shippingGateway = { createShipment: vi.fn(), cancelShipment: vi.fn() } as unknown as ShippingGateway;
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
    const expectedConfirmation = new ShippingConfirmation('ORDER-123', 'TRACK-ABC123', '2024-12-25');

    (shippingGateway as any).createShipment.mockReturnValue(expectedConfirmation);

    const result = shippingService.createShipment(order);

    expect(result).toBe(expectedConfirmation);
    expect((shippingGateway as any).createShipment).toHaveBeenCalledWith(
      expect.objectContaining({
        orderId: 'ORDER-123',
        customerId: 'CUST-456',
      }),
    );
    expect((shippingGateway as any).createShipment.mock.calls[0][0].items).toHaveLength(2);
  });

  it('p3_cancelShipment_callsGatewayAndReturnsResult', () => {
    const shippingGateway = { createShipment: vi.fn(), cancelShipment: vi.fn() } as unknown as ShippingGateway;
    const shippingService = new GatewayShippingService(shippingGateway);

    (shippingGateway as any).cancelShipment.mockReturnValue(true);

    expect(shippingService.cancelShipment('TRACK-ABC123')).toBe(true);
    expect((shippingGateway as any).cancelShipment).toHaveBeenCalledWith('TRACK-ABC123');
  });
});
