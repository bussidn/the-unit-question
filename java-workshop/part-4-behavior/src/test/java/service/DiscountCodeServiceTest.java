package service;

import domain.DiscountCode;
import helper.InMemoryDiscountCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiscountCodeServiceTest {

    private InMemoryDiscountCodeRepository discountCodeRepository;
    private DiscountCodeService discountCodeService;

    @BeforeEach
    void setUp() {
        discountCodeRepository = new InMemoryDiscountCodeRepository();
        discountCodeService = new DiscountCodeService(discountCodeRepository);
    }

    @Test
    void checkDiscountCode_returnsTrue_whenCodeHasAlreadyBeenUsed() {
        discountCodeRepository.withUsedCode("CUST-001", DiscountCode.SUMMER20);

        assertTrue(discountCodeService.checkDiscountCode("CUST-001", DiscountCode.SUMMER20));
    }

    @Test
    void checkDiscountCode_returnsFalse_whenCodeHasNotBeenUsed() {
        assertFalse(discountCodeService.checkDiscountCode("CUST-001", DiscountCode.SUMMER20));
    }

    @Test
    void markAsUsed_makesCodeCheckedAsUsed() {
        discountCodeService.markAsUsed("CUST-001", DiscountCode.SUMMER20);

        assertTrue(discountCodeRepository.isMarkedAsUsed("CUST-001", DiscountCode.SUMMER20));
    }
}

