package service;

import domain.DiscountCode;
import infrastructure.DatabaseDiscountCodeRepository;
import repository.DiscountCodeRepository;

/**
 * Discount code validation service.
 * Instantiates its own repository (consistent with Part 1 architecture).
 */
public class DiscountCodeService {

    private final DiscountCodeRepository repository;

    public DiscountCodeService() {
        // Instantiates repository directly - requires real database connection
        this.repository = new DatabaseDiscountCodeRepository();
    }

    /**
     * Checks a discount code for a given customer.
     *
     * @return true if the code has already been used by this customer
     */
    public boolean checkDiscountCode(String customerId, DiscountCode discountCode) {
        return repository.checkDiscountCode(customerId, discountCode);
    }

    public void markAsUsed(String customerId, DiscountCode discountCode) {
        repository.markAsUsed(customerId, discountCode);
    }
}

