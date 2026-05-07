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

class DiscountCodeServiceTest {

    private DiscountCodeService discountCodeService;
    private MockedStatic<Database> mockedDatabase;

    @BeforeEach
    void setUp() {
        mockedDatabase = mockStatic(Database.class);
        discountCodeService = new DiscountCodeService();
    }

    @AfterEach
    void tearDown() {
        mockedDatabase.close();
    }

    @Test
    void checkDiscountCode_returnsTrue_whenCodeHasAlreadyBeenUsed() {
        mockedDatabase.when(() -> Database.hasDiscountCodeBeenUsed("CUST-001", "SUMMER20"))
            .thenReturn(true);

        assertTrue(discountCodeService.checkDiscountCode("CUST-001", DiscountCode.SUMMER20));
    }

    @Test
    void checkDiscountCode_returnsFalse_whenCodeHasNotBeenUsed() {
        mockedDatabase.when(() -> Database.hasDiscountCodeBeenUsed("CUST-001", "SUMMER20"))
            .thenReturn(false);

        assertFalse(discountCodeService.checkDiscountCode("CUST-001", DiscountCode.SUMMER20));
    }

    @Test
    void markAsUsed_delegatesToDatabase() {
        discountCodeService.markAsUsed("CUST-001", DiscountCode.SUMMER20);

        mockedDatabase.verify(() -> Database.markDiscountCodeAsUsed("CUST-001", "SUMMER20"));
    }
}

