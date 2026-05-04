package service;

import domain.OrderItem;
import domain.Stock;
import domain.StockReservation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.StockRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private RepositoryStockService stockService;

    @Test
    void checkAvailability_returnsTrue_whenAllItemsAreInStock() {
        when(stockRepository.findByProductId("PROD-001")).thenReturn(new Stock("PROD-001", 100));
        when(stockRepository.findByProductId("PROD-002")).thenReturn(new Stock("PROD-002", 50));

        var items = List.of(
            new OrderItem("PROD-001", 5, 10.0),
            new OrderItem("PROD-002", 3, 20.0)
        );

        assertTrue(stockService.checkAvailability(items));
    }

    @Test
    void checkAvailability_returnsFalse_whenItemIsOutOfStock() {
        when(stockRepository.findByProductId("PROD-001")).thenReturn(new Stock("PROD-001", 100));
        when(stockRepository.findByProductId("PROD-003")).thenReturn(new Stock("PROD-003", 0));

        var items = List.of(
            new OrderItem("PROD-001", 5, 10.0),
            new OrderItem("PROD-003", 3, 20.0)
        );

        assertFalse(stockService.checkAvailability(items));
    }

    @Test
    void reserveStock_updatesRepositoryAndReturnsReservations() {
        when(stockRepository.findByProductId("PROD-001")).thenReturn(new Stock("PROD-001", 100));

        List<StockReservation> result = stockService.reserveStock(List.of(new OrderItem("PROD-001", 5, 10.0)));

        assertEquals(1, result.size());
        assertTrue(result.getFirst().reserved());
        verify(stockRepository).updateQuantity("PROD-001", 95);
    }

    @Test
    void releaseStock_restoresQuantity_forSuccessfulReservations() {
        when(stockRepository.findByProductId("PROD-001")).thenReturn(new Stock("PROD-001", 90));

        stockService.releaseStock(List.of(
            new StockReservation("PROD-001", 5, true),
            new StockReservation("PROD-002", 2, false)
        ));

        verify(stockRepository).updateQuantity("PROD-001", 95);
        verify(stockRepository, never()).findByProductId("PROD-002");
    }
}

