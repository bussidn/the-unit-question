import { Order } from '../domain/Order';
import { ShippingConfirmation } from '../domain/ShippingConfirmation';

export class ShippingService {
  createShipment(order: Order): ShippingConfirmation {
    throw new Error('Not implemented — requires shipping API connection');
  }

  cancelShipment(trackingNumber: string): boolean {
    throw new Error('Not implemented — requires shipping API connection');
  }
}
