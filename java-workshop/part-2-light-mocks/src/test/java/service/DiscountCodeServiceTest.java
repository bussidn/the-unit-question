package service;

import domain.DiscountCode;
import org.junit.jupiter.api.Test;
import repository.DiscountCodeRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DiscountCodeServiceTest {

    @Test
    void checkDiscountCode_returnsTrue_whenCodeHasAlreadyBeenUsed() {
        DiscountCodeRepository repository = mock(DiscountCodeRepository.class);
        DiscountCodeService discountCodeService = new DiscountCodeService(repository);

        when(repository.hasBeenUsed("CUST-001", DiscountCode.SUMMER20)).thenReturn(true);

        assertTrue(discountCodeService.checkDiscountCode("CUST-001", DiscountCode.SUMMER20));
    }

    @Test
    void checkDiscountCode_returnsFalse_whenCodeHasNotBeenUsed() {
        DiscountCodeRepository repository = mock(DiscountCodeRepository.class);
        DiscountCodeService discountCodeService = new DiscountCodeService(repository);

        when(repository.hasBeenUsed("CUST-001", DiscountCode.SUMMER20)).thenReturn(false);

        assertFalse(discountCodeService.checkDiscountCode("CUST-001", DiscountCode.SUMMER20));
    }

    @Test
    void markAsUsed_delegatesToRepository() {
        DiscountCodeRepository repository = mock(DiscountCodeRepository.class);
        DiscountCodeService discountCodeService = new DiscountCodeService(repository);

        discountCodeService.markAsUsed("CUST-001", DiscountCode.SUMMER20);

        verify(repository).markAsUsed("CUST-001", DiscountCode.SUMMER20);
    }
}

