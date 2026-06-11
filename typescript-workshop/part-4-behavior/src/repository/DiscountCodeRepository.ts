import { DiscountCode } from '../domain/DiscountCode';

export interface DiscountCodeRepository {
  checkDiscountCode(customerId: string, discountCode: DiscountCode): boolean;
  markAsUsed(customerId: string, discountCode: DiscountCode): void;
}
