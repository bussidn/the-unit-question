// DiscountCodeService creates its DiscountCodeRepository inline (no DI).
// To test it in isolation we must mock BOTH the DiscountCodeService module
// (for use in OrderServiceTest) and the DiscountCodeRepository module.
//
// This test shows how to mock the nested dependency.

vi.mock('../../src/repository/DiscountCodeRepository');

import { describe, it, expect, vi } from 'vitest';
import { DiscountCode } from '../../src/domain/DiscountCode';
import { DiscountCodeRepository } from '../../src/repository/DiscountCodeRepository';
import { DiscountCodeService } from '../../src/service/DiscountCodeService';

describe('DiscountCodeService', () => {
  it('p1_checkDiscountCode_returnsTrue_whenCodeHasAlreadyBeenUsed', () => {
    // The DiscountCodeRepository constructor is mocked, so `new DiscountCodeRepository()`
    // inside DiscountCodeService returns a mock instance.
    vi.mocked(DiscountCodeRepository.prototype.checkDiscountCode).mockReturnValue(true);

    const service = new DiscountCodeService();
    expect(service.checkDiscountCode('CUST-001', DiscountCode.SUMMER20)).toBe(true);
  });

  it('p1_checkDiscountCode_returnsFalse_whenCodeHasNotBeenUsed', () => {
    vi.mocked(DiscountCodeRepository.prototype.checkDiscountCode).mockReturnValue(false);

    const service = new DiscountCodeService();
    expect(service.checkDiscountCode('CUST-001', DiscountCode.SUMMER20)).toBe(false);
  });

  it('p1_markAsUsed_delegatesToRepository', () => {
    const service = new DiscountCodeService();
    service.markAsUsed('CUST-001', DiscountCode.SUMMER20);

    expect(vi.mocked(DiscountCodeRepository.prototype.markAsUsed))
      .toHaveBeenCalledWith('CUST-001', DiscountCode.SUMMER20);
  });
});
