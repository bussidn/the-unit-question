package service;

import domain.OrderItem;
import domain.Stock;
import domain.StockReservation;
import repository.StockRepository;

import java.util.ArrayList;
import java.util.List;

public class RepositoryStockService implements StockService {
    private final StockRepository stockRepository;

    public RepositoryStockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public List<StockReservation> reserveStock(List<OrderItem> items) {
        List<StockReservation> reservations = new ArrayList<>();

        for (OrderItem item : items) {
            Stock stock = stockRepository.findByProductId(item.productId());
            if (stock != null && stock.availableQuantity() >= item.quantity()) {
                stockRepository.updateQuantity(item.productId(), stock.availableQuantity() - item.quantity());
                reservations.add(new StockReservation(item.productId(), item.quantity(), true));
            } else {
                reservations.add(new StockReservation(item.productId(), item.quantity(), false));
            }
        }

        return reservations;
    }

    @Override
    public void releaseStock(List<StockReservation> reservations) {
        for (StockReservation reservation : reservations) {
            if (!reservation.reserved()) {
                continue;
            }

            Stock stock = stockRepository.findByProductId(reservation.productId());
            if (stock != null) {
                stockRepository.updateQuantity(
                    reservation.productId(),
                    stock.availableQuantity() + reservation.quantity()
                );
            }
        }
    }
}