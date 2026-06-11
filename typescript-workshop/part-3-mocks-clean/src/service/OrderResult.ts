import { Order } from '../domain/Order';
import { ShippingConfirmation } from '../domain/ShippingConfirmation';

export type OrderResult =
  | { readonly kind: 'success'; readonly order: Order; readonly shippingConfirmation: ShippingConfirmation }
  | { readonly kind: 'failure'; readonly order: Order; readonly reason: string };

export const OrderResult = {
  success: (order: Order, shippingConfirmation: ShippingConfirmation): OrderResult =>
    ({ kind: 'success', order, shippingConfirmation }),
  failure: (order: Order, reason: string): OrderResult =>
    ({ kind: 'failure', order, reason }),
};
