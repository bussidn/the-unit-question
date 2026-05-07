package service;

import domain.DiscountCode;
import repository.DiscountCodeRepository;

/**
 * Discount code validation service.
 */
public class DiscountCodeService {
    private final DiscountCodeRepository discountCodeRepository;

    public DiscountCodeService(DiscountCodeRepository discountCodeRepository) {
        this.discountCodeRepository = discountCodeRepository;
    }

    /**
     * Checks a discount code for a given customer.
     *
     * @return true if the code has already been used by this customer
     */
    public boolean checkDiscountCode(String customerId, DiscountCode discountCode) {
        return discountCodeRepository.checkDiscountCode(customerId, discountCode);
    }

    public void markAsUsed(String customerId, DiscountCode discountCode) {
        discountCodeRepository.markAsUsed(customerId, discountCode);
    }
}

