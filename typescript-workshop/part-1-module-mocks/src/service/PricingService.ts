import { DiscountCode, discountPercentage } from '../domain/DiscountCode';
import { OrderItem } from '../domain/OrderItem';

const TAX_RATE = 0.20;
const FREE_SHIPPING_THRESHOLD = 100.0;
const SHIPPING_COST = 5.00;

export function calculateTotal(items: ReadonlyArray<OrderItem>): number {
  const subtotal = items.reduce((sum, item) => sum + item.quantity * item.unitPrice, 0);
  const tax = subtotal * TAX_RATE;
  const shipping = subtotal >= FREE_SHIPPING_THRESHOLD ? 0 : SHIPPING_COST;
  return subtotal + tax + shipping;
}

export function calculateTotalWithDiscount(items: ReadonlyArray<OrderItem>, discountCode: DiscountCode): number {
  const subtotal = items.reduce((sum, item) => sum + item.quantity * item.unitPrice, 0);
  const discountedSubtotal = subtotal * (1 - discountPercentage[discountCode] / 100);
  const tax = discountedSubtotal * TAX_RATE;
  const shipping = discountedSubtotal >= FREE_SHIPPING_THRESHOLD ? 0 : SHIPPING_COST;
  return discountedSubtotal + tax + shipping;
}
