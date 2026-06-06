import { describe, it, expect } from 'vitest';
import { DiscountCode } from '../../src/domain/DiscountCode';
import { DiscountCodeService } from '../../src/service/DiscountCodeService';
import { InMemoryDiscountCodeRepository } from '../helper/InMemoryDiscountCodeRepository';

describe('DiscountCodeService', () => {
  it('p4_checkDiscountCode_returnsTrue_whenCodeHasAlreadyBeenUsed', () => {
    const repository = new InMemoryDiscountCodeRepository()
      .withUsedCode('CUST-001', DiscountCode.SUMMER20);
    const discountCodeService = new DiscountCodeService(repository);

    expect(discountCodeService.checkDiscountCode('CUST-001', DiscountCode.SUMMER20)).toBe(true);
  });

  it('p4_checkDiscountCode_returnsFalse_whenCodeHasNotBeenUsed', () => {
    const repository = new InMemoryDiscountCodeRepository();
    const discountCodeService = new DiscountCodeService(repository);

    expect(discountCodeService.checkDiscountCode('CUST-001', DiscountCode.SUMMER20)).toBe(false);
  });

  it('p4_markAsUsed_storesCodeInRepository', () => {
    const repository = new InMemoryDiscountCodeRepository();
    const discountCodeService = new DiscountCodeService(repository);

    discountCodeService.markAsUsed('CUST-001', DiscountCode.SUMMER20);

    expect(repository.isMarkedAsUsed('CUST-001', DiscountCode.SUMMER20)).toBe(true);
  });
});
