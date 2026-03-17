package service;

import domain.OrderItem;
import domain.Stock;
import domain.StockReservation;
import infrastructure.Database;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * PowerMock style: We need to mock the Database static methods to test StockService
 */
class StockServiceTest {

    private StockService stockService;
    private MockedStatic<Database> mockedDatabase;

    @BeforeEach
    void setUp() {
        mockedDatabase = mockStatic(Database.class);
        stockService = new StockService();
    }

    @AfterEach
    void tearDown() {
        mockedDatabase.close();
    }

    @Test
    void checkAvailability_returnsTrue_whenAllItemsAreInStock() {
        // Given
        var items = List.of(
            new OrderItem("PROD-001", 5, 10.0),
            new OrderItem("PROD-002", 3, 20.0)
        );

        mockedDatabase.when(() -> Database.getStock("PROD-001"))
            .thenReturn(new Stock("PROD-001", 100));
        mockedDatabase.when(() -> Database.getStock("PROD-002"))
            .thenReturn(new Stock("PROD-002", 50));

        // When
        boolean result = stockService.checkAvailability(items);

        // Then
        assertTrue(result);
        mockedDatabase.verify(() -> Database.getStock("PROD-001"));
        mockedDatabase.verify(() -> Database.getStock("PROD-002"));
    }

    @Test
    void checkAvailability_returnsFalse_whenItemIsOutOfStock() {
        // Given
        var items = List.of(
            new OrderItem("PROD-001", 5, 10.0),
            new OrderItem("PROD-003", 3, 20.0)
        );

        mockedDatabase.when(() -> Database.getStock("PROD-001"))
            .thenReturn(new Stock("PROD-001", 100));
        mockedDatabase.when(() -> Database.getStock("PROD-003"))
            .thenReturn(new Stock("PROD-003", 0)); // Out of stock!

        // When
        boolean result = stockService.checkAvailability(items);

        // Then
        assertFalse(result);
    }

    @Test
    void checkAvailability_callsDatabaseOncePerItem() {
        // Given
        var items = List.of(
            new OrderItem("PROD-001", 1, 10.0),
            new OrderItem("PROD-002", 1, 20.0),
            new OrderItem("PROD-003", 1, 30.0)
        );

        mockedDatabase.when(() -> Database.getStock(anyString()))
            .thenReturn(new Stock("any", 100));

        // When
        stockService.checkAvailability(items);

        // Then
        mockedDatabase.verify(() -> Database.getStock("PROD-001"));
        mockedDatabase.verify(() -> Database.getStock("PROD-002"));
        mockedDatabase.verify(() -> Database.getStock("PROD-003"));
    }

    @Test
    void reserveStock_updatesDatabaseAndReturnsReservations() {
        // Given
        var items = List.of(new OrderItem("PROD-001", 5, 10.0));

        mockedDatabase.when(() -> Database.getStock("PROD-001"))
            .thenReturn(new Stock("PROD-001", 100));

        // When
        List<StockReservation> result = stockService.reserveStock(items);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).reserved());
        mockedDatabase.verify(() -> Database.updateStock("PROD-001", 95));
    }

    @Test
    void checkAvailability_withSingleItem_checksStockCorrectly() {
        // Given
        var items = List.of(new OrderItem("PROD-042", 10, 5.0));

        mockedDatabase.when(() -> Database.getStock("PROD-042"))
            .thenReturn(new Stock("PROD-042", 15));

        // When
        boolean result = stockService.checkAvailability(items);

        // Then
        assertTrue(result);
        mockedDatabase.verify(() -> Database.getStock("PROD-042"), times(1));
    }
}

