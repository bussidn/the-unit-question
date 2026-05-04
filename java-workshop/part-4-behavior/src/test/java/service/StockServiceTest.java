package service;

import domain.OrderItem;
import domain.StockReservation;
import helper.InMemoryStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StockServiceTest {

    private InMemoryStockRepository stockRepository;
    private RepositoryStockService stockService;

    @BeforeEach
    void setUp() {
        stockRepository = new InMemoryStockRepository();
        stockService = new RepositoryStockService(stockRepository);
    }

    @Test
    void checkAvailability_returnsTrue_whenAllItemsAreInStock() {
        stockRepository.withAvailableStock("PROD-001", 100)
                       .withAvailableStock("PROD-002", 50);

        var items = List.of(
            new OrderItem("PROD-001", 5, 10.0),
            new OrderItem("PROD-002", 3, 20.0)
        );

        assertTrue(stockService.checkAvailability(items));
    }

    @Test
    void checkAvailability_returnsFalse_whenItemIsOutOfStock() {
        stockRepository.withAvailableStock("PROD-001", 100);

        var items = List.of(
            new OrderItem("PROD-001", 5, 10.0),
            new OrderItem("PROD-003", 3, 20.0)
        );

        assertFalse(stockService.checkAvailability(items));
    }

    @Test
    void reserveStock_updatesStockAndReturnsReservations() {
        stockRepository.withAvailableStock("PROD-001", 100);

        List<StockReservation> result = stockService.reserveStock(List.of(new OrderItem("PROD-001", 5, 10.0)));

        assertEquals(1, result.size());
        assertTrue(result.getFirst().reserved());
        assertEquals(95, stockRepository.findByProductId("PROD-001").availableQuantity());
    }

    @Test
    void releaseStock_restoresQuantity_forSuccessfulReservations() {
        stockRepository.withAvailableStock("PROD-001", 90);

        stockService.releaseStock(List.of(
            new StockReservation("PROD-001", 5, true),
            new StockReservation("PROD-002", 2, false)
        ));

        assertEquals(95, stockRepository.findByProductId("PROD-001").availableQuantity());
        assertEquals(0, stockRepository.findByProductId("PROD-002").availableQuantity());
    }
}

