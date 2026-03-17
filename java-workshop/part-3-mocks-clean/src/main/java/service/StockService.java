package service;

import domain.StockReservation;

import java.util.List;

public interface StockService {
    void releaseStock(List<StockReservation> reservations);
}