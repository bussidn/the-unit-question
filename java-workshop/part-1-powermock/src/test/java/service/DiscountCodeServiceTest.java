package service;

import domain.DiscountCode;
import infrastructure.DatabaseDiscountCodeRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DiscountCodeServiceTest {

    @Test
    void p1_checkDiscountCode_returnsTrue_whenCodeHasAlreadyBeenUsed() {
        try (var mockedRepo = mockConstruction(DatabaseDiscountCodeRepository.class,
                (mock, ctx) -> when(mock.checkDiscountCode("CUST-001", DiscountCode.SUMMER20)).thenReturn(true))) {

            var discountCodeService = new DiscountCodeService();

            assertTrue(discountCodeService.checkDiscountCode("CUST-001", DiscountCode.SUMMER20));
        }
    }

    @Test
    void p1_checkDiscountCode_returnsFalse_whenCodeHasNotBeenUsed() {
        try (var mockedRepo = mockConstruction(DatabaseDiscountCodeRepository.class,
                (mock, ctx) -> when(mock.checkDiscountCode("CUST-001", DiscountCode.SUMMER20)).thenReturn(false))) {

            var discountCodeService = new DiscountCodeService();

            assertFalse(discountCodeService.checkDiscountCode("CUST-001", DiscountCode.SUMMER20));
        }
    }

    @Test
    void p1_markAsUsed_delegatesToRepository() {
        try (var mockedRepo = mockConstruction(DatabaseDiscountCodeRepository.class)) {

            var discountCodeService = new DiscountCodeService();
            discountCodeService.markAsUsed("CUST-001", DiscountCode.SUMMER20);

            verify(mockedRepo.constructed().getFirst()).markAsUsed("CUST-001", DiscountCode.SUMMER20);
        }
    }
}

