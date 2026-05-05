package service;

import domain.DiscountCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.DiscountCodeRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromoCodeServiceTest {

    @Mock
    private DiscountCodeRepository discountCodeRepository;

    @InjectMocks
    private PromoCodeService promoCodeService;

    @Test
    void checkPromoCode_returnsTrue_whenCodeHasAlreadyBeenUsed() {
        when(discountCodeRepository.hasBeenUsed("CUST-001", DiscountCode.SUMMER20)).thenReturn(true);

        assertTrue(promoCodeService.checkPromoCode("CUST-001", DiscountCode.SUMMER20));
    }

    @Test
    void checkPromoCode_returnsFalse_whenCodeHasNotBeenUsed() {
        when(discountCodeRepository.hasBeenUsed("CUST-001", DiscountCode.SUMMER20)).thenReturn(false);

        assertFalse(promoCodeService.checkPromoCode("CUST-001", DiscountCode.SUMMER20));
    }

    @Test
    void markAsUsed_delegatesToRepository() {
        promoCodeService.markAsUsed("CUST-001", DiscountCode.SUMMER20);

        verify(discountCodeRepository).markAsUsed("CUST-001", DiscountCode.SUMMER20);
    }
}

