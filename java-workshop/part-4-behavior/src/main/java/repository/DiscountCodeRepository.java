package repository;

import domain.DiscountCode;

public interface DiscountCodeRepository {
    boolean checkDiscountCode(String customerId, DiscountCode discountCode);
    void markAsUsed(String customerId, DiscountCode discountCode);
}

