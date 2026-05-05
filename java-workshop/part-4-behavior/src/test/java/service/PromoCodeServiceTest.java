package service;

import domain.DiscountCode;
import helper.InMemoryDiscountCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromoCodeServiceTest {

    private InMemoryDiscountCodeRepository discountCodeRepository;
    private PromoCodeService promoCodeService;

    @BeforeEach
    void setUp() {
        discountCodeRepository = new InMemoryDiscountCodeRepository();
        promoCodeService = new PromoCodeService(discountCodeRepository);
    }

    @Test
    void checkPromoCode_returnsTrue_whenCodeHasAlreadyBeenUsed() {
        discountCodeRepository.withUsedCode("CUST-001", DiscountCode.SUMMER20);

        assertTrue(promoCodeService.checkPromoCode("CUST-001", DiscountCode.SUMMER20));
    }

    @Test
    void checkPromoCode_returnsFalse_whenCodeHasNotBeenUsed() {
        assertFalse(promoCodeService.checkPromoCode("CUST-001", DiscountCode.SUMMER20));
    }

    @Test
    void markAsUsed_makesCodeCheckedAsUsed() {
        promoCodeService.markAsUsed("CUST-001", DiscountCode.SUMMER20);

        assertTrue(discountCodeRepository.isMarkedAsUsed("CUST-001", DiscountCode.SUMMER20));
    }
}

