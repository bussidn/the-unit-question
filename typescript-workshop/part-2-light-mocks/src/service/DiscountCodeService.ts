import { DiscountCode } from '../domain/DiscountCode';
import { DiscountCodeRepository } from '../repository/DiscountCodeRepository';

export class DiscountCodeService {
  constructor(private readonly discountCodeRepository: DiscountCodeRepository) {}

  /**
   * Returns true if the discount code has already been used by this customer.
   */
  checkDiscountCode(customerId: string, discountCode: DiscountCode): boolean {
    return this.discountCodeRepository.checkDiscountCode(customerId, discountCode);
  }

  markAsUsed(customerId: string, discountCode: DiscountCode): void {
    this.discountCodeRepository.markAsUsed(customerId, discountCode);
  }
}
