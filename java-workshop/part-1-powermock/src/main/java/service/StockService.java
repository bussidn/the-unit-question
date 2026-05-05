package service;

import domain.OrderItem;
import domain.Stock;
import domain.StockReservation;
import infrastructure.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Stock service with hardcoded Database dependency (static calls).
 * Forces PowerMock-style mocking.
 */
public class StockService {

    public List<StockReservation> reserveStock(List<OrderItem> items) {
        List<StockReservation> reservations = new ArrayList<>();

        for (OrderItem item : items) {
            Stock stock = Database.getStock(item.productId());
            if (stock != null && stock.availableQuantity() >= item.quantity()) {
                Database.updateStock(item.productId(), stock.availableQuantity() - item.quantity());
                reservations.add(new StockReservation(item.productId(), item.quantity(), true));
            } else {
                reservations.add(new StockReservation(item.productId(), item.quantity(), false));
            }
        }

        return reservations;
    }

    public void releaseStock(List<StockReservation> reservations) {
        for (StockReservation reservation : reservations) {
            if (!reservation.reserved()) {
                continue;
            }

            Stock stock = Database.getStock(reservation.productId());
            if (stock != null) {
                Database.updateStock(
                    reservation.productId(),
                    stock.availableQuantity() + reservation.quantity()
                );
            }
        }
    }
}