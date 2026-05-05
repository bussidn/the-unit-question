package service;

import domain.DiscountCode;
import repository.DiscountCodeRepository;

/**
 * Promo code validation service.
 */
public class PromoCodeService {
    private final DiscountCodeRepository discountCodeRepository;

    public PromoCodeService(DiscountCodeRepository discountCodeRepository) {
        this.discountCodeRepository = discountCodeRepository;
    }

    /**
     * Checks a promo code for a given customer.
     *
     * @return true if the code has already been used by this customer
     */
    public boolean checkPromoCode(String customerId, DiscountCode discountCode) {
        return discountCodeRepository.hasBeenUsed(customerId, discountCode);
    }

    public void markAsUsed(String customerId, DiscountCode discountCode) {
        discountCodeRepository.markAsUsed(customerId, discountCode);
    }
}

