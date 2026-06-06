import { Order } from '../domain/Order';
import { ShippingConfirmation } from '../domain/ShippingConfirmation';
import { ShippingItem } from '../domain/ShippingItem';
import { ShippingRequest } from '../domain/ShippingRequest';
import { ShippingGateway } from '../gateway/ShippingGateway';
import { ShippingService } from './ShippingService';

export class GatewayShippingService implements ShippingService {
  constructor(private readonly shippingGateway: ShippingGateway) {}

  createShipment(order: Order): ShippingConfirmation {
    const items = order.items.map((item) => new ShippingItem(item.productId, item.quantity));
    const request = new ShippingRequest(order.id, order.customerId, items);
    return this.shippingGateway.createShipment(request);
  }

  cancelShipment(trackingNumber: string): boolean {
    return this.shippingGateway.cancelShipment(trackingNumber);
  }
}
