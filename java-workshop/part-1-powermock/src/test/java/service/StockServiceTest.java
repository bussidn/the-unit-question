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

}

