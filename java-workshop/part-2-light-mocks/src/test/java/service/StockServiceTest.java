package service;

import domain.OrderItem;
import domain.Stock;
import domain.StockReservation;
import org.junit.jupiter.api.Test;
import repository.StockRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StockServiceTest {

    @Test
    void reserveStock_updatesRepositoryAndReturnsReservations() {
        StockRepository stockRepository = mock(StockRepository.class);
        RepositoryStockService stockService = new RepositoryStockService(stockRepository);

        var items = List.of(new OrderItem("PROD-001", 5, 10.0));

        when(stockRepository.findByProductId("PROD-001")).thenReturn(new Stock("PROD-001", 100));

        List<StockReservation> result = stockService.reserveStock(items);

        assertEquals(1, result.size());
        assertTrue(result.get(0).reserved());
        verify(stockRepository).updateQuantity("PROD-001", 95);
    }

    @Test
    void releaseStock_restoresQuantity_forSuccessfulReservations() {
        StockRepository stockRepository = mock(StockRepository.class);
        RepositoryStockService stockService = new RepositoryStockService(stockRepository);

        var reservations = List.of(
            new StockReservation("PROD-001", 5, true),
            new StockReservation("PROD-002", 2, false)
        );

        when(stockRepository.findByProductId("PROD-001")).thenReturn(new Stock("PROD-001", 90));

        stockService.releaseStock(reservations);

        verify(stockRepository).updateQuantity("PROD-001", 95);
        verify(stockRepository, never()).findByProductId("PROD-002");
    }
}