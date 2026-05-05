package helper;

import domain.DiscountCode;
import repository.DiscountCodeRepository;

import java.util.HashSet;
import java.util.Set;

public class InMemoryDiscountCodeRepository implements DiscountCodeRepository {
    private final Set<String> usedCodes = new HashSet<>();

    public InMemoryDiscountCodeRepository withUsedCode(String customerId, DiscountCode discountCode) {
        usedCodes.add(key(customerId, discountCode));
        return this;
    }

    public boolean isMarkedAsUsed(String customerId, DiscountCode discountCode) {
        return usedCodes.contains(key(customerId, discountCode));
    }

    @Override
    public boolean hasBeenUsed(String customerId, DiscountCode discountCode) {
        return usedCodes.contains(key(customerId, discountCode));
    }

    @Override
    public void markAsUsed(String customerId, DiscountCode discountCode) {
        usedCodes.add(key(customerId, discountCode));
    }

    private String key(String customerId, DiscountCode discountCode) {
        return customerId + ":" + discountCode.name();
    }
}

