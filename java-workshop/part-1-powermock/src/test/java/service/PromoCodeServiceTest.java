package service;

import domain.DiscountCode;
import infrastructure.Database;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

class PromoCodeServiceTest {

    private PromoCodeService promoCodeService;
    private MockedStatic<Database> mockedDatabase;

    @BeforeEach
    void setUp() {
        mockedDatabase = mockStatic(Database.class);
        promoCodeService = new PromoCodeService();
    }

    @AfterEach
    void tearDown() {
        mockedDatabase.close();
    }

    @Test
    void checkPromoCode_returnsTrue_whenCodeHasAlreadyBeenUsed() {
        mockedDatabase.when(() -> Database.hasDiscountCodeBeenUsed("CUST-001", "SUMMER20"))
            .thenReturn(true);

        assertTrue(promoCodeService.checkPromoCode("CUST-001", DiscountCode.SUMMER20));
    }

    @Test
    void checkPromoCode_returnsFalse_whenCodeHasNotBeenUsed() {
        mockedDatabase.when(() -> Database.hasDiscountCodeBeenUsed("CUST-001", "SUMMER20"))
            .thenReturn(false);

        assertFalse(promoCodeService.checkPromoCode("CUST-001", DiscountCode.SUMMER20));
    }

    @Test
    void markAsUsed_delegatesToDatabase() {
        promoCodeService.markAsUsed("CUST-001", DiscountCode.SUMMER20);

        mockedDatabase.verify(() -> Database.markDiscountCodeAsUsed("CUST-001", "SUMMER20"));
    }
}

