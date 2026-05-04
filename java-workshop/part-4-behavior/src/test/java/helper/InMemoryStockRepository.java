package helper;

import domain.Stock;
import repository.StockRepository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryStockRepository implements StockRepository {
    private final Map<String, Integer> stock = new HashMap<>();

    public InMemoryStockRepository withAvailableStock(String productId, int quantity) {
        stock.put(productId, quantity);
        return this;
    }

    @Override
    public Stock findByProductId(String productId) {
        return new Stock(productId, stock.getOrDefault(productId, 0));
    }

    @Override
    public void updateQuantity(String productId, int newQuantity) {
        stock.put(productId, newQuantity);
    }
}

