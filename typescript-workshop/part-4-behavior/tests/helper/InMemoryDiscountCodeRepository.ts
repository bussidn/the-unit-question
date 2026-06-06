import { DiscountCode } from '../../src/domain/DiscountCode';
import { DiscountCodeRepository } from '../../src/repository/DiscountCodeRepository';

export class InMemoryDiscountCodeRepository implements DiscountCodeRepository {
  private readonly usedCodes = new Set<string>();

  private key(customerId: string, discountCode: DiscountCode): string {
    return customerId + ':' + discountCode;
  }

  checkDiscountCode(customerId: string, discountCode: DiscountCode): boolean {
    return this.usedCodes.has(this.key(customerId, discountCode));
  }

  markAsUsed(customerId: string, discountCode: DiscountCode): void {
    this.usedCodes.add(this.key(customerId, discountCode));
  }

  withUsedCode(customerId: string, discountCode: DiscountCode): this {
    this.usedCodes.add(this.key(customerId, discountCode));
    return this;
  }

  isMarkedAsUsed(customerId: string, discountCode: DiscountCode): boolean {
    return this.usedCodes.has(this.key(customerId, discountCode));
  }
}
