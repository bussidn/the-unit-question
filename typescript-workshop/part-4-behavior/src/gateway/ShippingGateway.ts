import { ShippingConfirmation } from '../domain/ShippingConfirmation';
import { ShippingRequest } from '../domain/ShippingRequest';

export interface ShippingGateway {
  createShipment(request: ShippingRequest): ShippingConfirmation;
  cancelShipment(trackingNumber: string): boolean;
}
