package infrastructure;

import domain.Order;
import domain.OrderStatus;
import domain.Stock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Simulates a database singleton - requires PowerMock to mock.
 */
public final class Database {
    private static final Map<String, Stock> stocks = new HashMap<>();
    private static final Map<String, Order> orders = new HashMap<>();
    private static final Map<String, Set<String>> usedDiscountCodes = new HashMap<>();

    static {
        stocks.put("PROD-001", new Stock("PROD-001", 100));
        stocks.put("PROD-002", new Stock("PROD-002", 50));
        stocks.put("PROD-003", new Stock("PROD-003", 0));
    }

    private Database() {
        // Prevent instantiation
    }

    public static Stock getStock(String productId) {
        return stocks.get(productId);
    }

    public static void updateStock(String productId, int newQuantity) {
        Stock existingStock = stocks.get(productId);
        if (existingStock != null) {
            stocks.put(productId, new Stock(productId, newQuantity));
        }
    }

    public static Order getOrder(String orderId) {
        return orders.get(orderId);
    }

    public static void saveOrder(Order order) {
        orders.put(order.id(), order);
    }

    public static void updateOrderStatus(String orderId, OrderStatus status) {
        Order existingOrder = orders.get(orderId);
        if (existingOrder != null) {
            orders.put(orderId, existingOrder.withStatus(status));
        }
    }

    public static boolean hasDiscountCodeBeenUsed(String customerId, String discountCode) {
        var codes = usedDiscountCodes.get(customerId);
        return codes != null && codes.contains(discountCode);
    }

    public static void markDiscountCodeAsUsed(String customerId, String discountCode) {
        usedDiscountCodes.computeIfAbsent(customerId, k -> new HashSet<>()).add(discountCode);
    }
}
