package service;

import domain.OrderItem;
import domain.StockReservation;

import java.util.List;

public interface StockService {
    List<StockReservation> reserveStock(List<OrderItem> items);

    void releaseStock(List<StockReservation> reservations);
}