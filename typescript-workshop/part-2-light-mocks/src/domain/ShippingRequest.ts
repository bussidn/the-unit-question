import { ShippingItem } from './ShippingItem';

export class ShippingRequest {
  constructor(
    readonly orderId: string,
    readonly customerId: string,
    readonly items: ReadonlyArray<ShippingItem>,
  ) {}
}
