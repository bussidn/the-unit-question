import { DiscountCode } from '../domain/DiscountCode';
import { DiscountCodeRepository } from '../repository/DiscountCodeRepository';

export class DiscountCodeService {
  private readonly repository = new DiscountCodeRepository();

  /**
   * Returns true if the discount code has already been used by this customer.
   */
  checkDiscountCode(customerId: string, discountCode: DiscountCode): boolean {
    return this.repository.checkDiscountCode(customerId, discountCode);
  }

  markAsUsed(customerId: string, discountCode: DiscountCode): void {
    this.repository.markAsUsed(customerId, discountCode);
  }
}
