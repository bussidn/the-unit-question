package service;

import domain.DiscountCode;
import infrastructure.Database;

/**
 * Discount code validation service.
 * Uses Database static calls (consistent with Part 1 architecture).
 */
public class DiscountCodeService {

    /**
     * Checks a discount code for a given customer.
     *
     * @return true if the code has already been used by this customer
     */
    public boolean checkDiscountCode(String customerId, DiscountCode discountCode) {
        return Database.checkDiscountCode(customerId, discountCode.name());
    }

    public void markAsUsed(String customerId, DiscountCode discountCode) {
        Database.markAsUsed(customerId, discountCode.name());
    }
}

