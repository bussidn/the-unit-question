package repository;

import domain.Stock;

public interface StockRepository {
    Stock findByProductId(String productId);

    void updateQuantity(String productId, int newQuantity);
}