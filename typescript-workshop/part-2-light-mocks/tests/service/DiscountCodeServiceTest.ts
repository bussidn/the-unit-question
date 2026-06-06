import { describe, it, expect, vi } from 'vitest';
import { DiscountCode } from '../../src/domain/DiscountCode';
import type { DiscountCodeRepository } from '../../src/repository/DiscountCodeRepository';
import { DiscountCodeService } from '../../src/service/DiscountCodeService';

describe('DiscountCodeService', () => {
  it('p2_checkDiscountCode_returnsTrue_whenCodeHasAlreadyBeenUsed', () => {
    const repository = { checkDiscountCode: vi.fn(), markAsUsed: vi.fn() } as unknown as DiscountCodeRepository;
    const discountCodeService = new DiscountCodeService(repository);

    (repository as any).checkDiscountCode.mockReturnValue(true);

    expect(discountCodeService.checkDiscountCode('CUST-001', DiscountCode.SUMMER20)).toBe(true);
  });

  it('p2_checkDiscountCode_returnsFalse_whenCodeHasNotBeenUsed', () => {
    const repository = { checkDiscountCode: vi.fn(), markAsUsed: vi.fn() } as unknown as DiscountCodeRepository;
    const discountCodeService = new DiscountCodeService(repository);

    (repository as any).checkDiscountCode.mockReturnValue(false);

    expect(discountCodeService.checkDiscountCode('CUST-001', DiscountCode.SUMMER20)).toBe(false);
  });

  it('p2_markAsUsed_delegatesToRepository', () => {
    const repository = { checkDiscountCode: vi.fn(), markAsUsed: vi.fn() } as unknown as DiscountCodeRepository;
    const discountCodeService = new DiscountCodeService(repository);

    discountCodeService.markAsUsed('CUST-001', DiscountCode.SUMMER20);

    expect((repository as any).markAsUsed).toHaveBeenCalledWith('CUST-001', DiscountCode.SUMMER20);
  });
});
