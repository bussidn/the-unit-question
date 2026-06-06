import { Order } from '../domain/Order';
import { ShippingConfirmation } from '../domain/ShippingConfirmation';

export interface ShippingService {
  createShipment(order: Order): ShippingConfirmation;
  cancelShipment(trackingNumber: string): boolean;
}
