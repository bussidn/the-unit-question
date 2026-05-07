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
class DiscountCodeServiceTest {

    @Mock
    private DiscountCodeRepository discountCodeRepository;

    @InjectMocks
    private DiscountCodeService discountCodeService;

    @Test
    void checkDiscountCode_returnsTrue_whenCodeHasAlreadyBeenUsed() {
        when(discountCodeRepository.checkDiscountCode("CUST-001", DiscountCode.SUMMER20)).thenReturn(true);

        assertTrue(discountCodeService.checkDiscountCode("CUST-001", DiscountCode.SUMMER20));
    }

    @Test
    void checkDiscountCode_returnsFalse_whenCodeHasNotBeenUsed() {
        when(discountCodeRepository.checkDiscountCode("CUST-001", DiscountCode.SUMMER20)).thenReturn(false);

        assertFalse(discountCodeService.checkDiscountCode("CUST-001", DiscountCode.SUMMER20));
    }

    @Test
    void markAsUsed_delegatesToRepository() {
        discountCodeService.markAsUsed("CUST-001", DiscountCode.SUMMER20);

        verify(discountCodeRepository).markAsUsed("CUST-001", DiscountCode.SUMMER20);
    }
}


