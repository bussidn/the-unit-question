package service;

import domain.DiscountCode;
import org.junit.jupiter.api.Test;
import repository.DiscountCodeRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PromoCodeServiceTest {

    @Test
    void checkPromoCode_returnsTrue_whenCodeHasAlreadyBeenUsed() {
        DiscountCodeRepository repository = mock(DiscountCodeRepository.class);
        PromoCodeService promoCodeService = new PromoCodeService(repository);

        when(repository.hasBeenUsed("CUST-001", DiscountCode.SUMMER20)).thenReturn(true);

        assertTrue(promoCodeService.checkPromoCode("CUST-001", DiscountCode.SUMMER20));
    }

    @Test
    void checkPromoCode_returnsFalse_whenCodeHasNotBeenUsed() {
        DiscountCodeRepository repository = mock(DiscountCodeRepository.class);
        PromoCodeService promoCodeService = new PromoCodeService(repository);

        when(repository.hasBeenUsed("CUST-001", DiscountCode.SUMMER20)).thenReturn(false);

        assertFalse(promoCodeService.checkPromoCode("CUST-001", DiscountCode.SUMMER20));
    }

    @Test
    void markAsUsed_delegatesToRepository() {
        DiscountCodeRepository repository = mock(DiscountCodeRepository.class);
        PromoCodeService promoCodeService = new PromoCodeService(repository);

        promoCodeService.markAsUsed("CUST-001", DiscountCode.SUMMER20);

        verify(repository).markAsUsed("CUST-001", DiscountCode.SUMMER20);
    }
}

