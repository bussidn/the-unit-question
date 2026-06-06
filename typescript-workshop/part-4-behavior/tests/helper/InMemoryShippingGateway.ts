import { ShippingConfirmation } from '../../src/domain/ShippingConfirmation';
import { ShippingRequest } from '../../src/domain/ShippingRequest';
import { ShippingGateway } from '../../src/gateway/ShippingGateway';

export class InMemoryShippingGateway implements ShippingGateway {
  private shipmentCreated = false;
  private confirmation: ShippingConfirmation | null = null;

  createShipment(request: ShippingRequest): ShippingConfirmation {
    this.shipmentCreated = true;
    this.confirmation = new ShippingConfirmation(
      request.orderId,
      'TRACK-' + request.orderId,
      '2024-12-25',
    );
    return this.confirmation;
  }

  cancelShipment(_trackingNumber: string): boolean {
    return true;
  }

  hasCreatedShipment(): boolean {
    return this.shipmentCreated;
  }

  lastConfirmation(): ShippingConfirmation | null {
    return this.confirmation;
  }
}
