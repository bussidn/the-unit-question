import { DiscountCode } from '../domain/DiscountCode';

export class DiscountCodeRepository {
  checkDiscountCode(customerId: string, discountCode: DiscountCode): boolean {
    throw new Error('Not implemented — requires database connection');
  }

  markAsUsed(customerId: string, discountCode: DiscountCode): void {
    throw new Error('Not implemented — requires database connection');
  }
}
