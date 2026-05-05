package service;

import domain.DiscountCode;
import infrastructure.Database;

/**
 * Promo code validation service.
 * Uses Database static calls (consistent with Part 1 architecture).
 */
public class PromoCodeService {

    /**
     * Checks a promo code for a given customer.
     *
     * @return true if the code has already been used by this customer
     */
    public boolean checkPromoCode(String customerId, DiscountCode discountCode) {
        return Database.hasDiscountCodeBeenUsed(customerId, discountCode.name());
    }

    public void markAsUsed(String customerId, DiscountCode discountCode) {
        Database.markDiscountCodeAsUsed(customerId, discountCode.name());
    }
}

