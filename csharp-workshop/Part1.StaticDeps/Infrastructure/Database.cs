using Part1.StaticDeps.Domain;

namespace Part1.StaticDeps.Infrastructure;

/// <summary>
/// Simulates a database singleton with static state.
/// In production code, static mutable state like this is a test smell:
/// it makes tests order-dependent and hard to isolate. Requires static
/// mocking (JustMock/Typemock) or careful reset logic in tests.
/// Compare: Java version requires PowerMock to mock.
/// </summary>
public static class Database
{
    private static readonly Dictionary<string, Stock> Stocks = new();
    private static readonly Dictionary<string, Order> Orders = new();
    private static readonly Dictionary<string, HashSet<string>> UsedDiscountCodes = new();

    static Database()
    {
        Stocks["PROD-001"] = new Stock("PROD-001", 100);
        Stocks["PROD-002"] = new Stock("PROD-002", 50);
        Stocks["PROD-003"] = new Stock("PROD-003", 0);
    }

    public static Stock? GetStock(string productId) =>
        Stocks.TryGetValue(productId, out var stock) ? stock : null;

    public static void UpdateStock(string productId, int newQuantity)
    {
        if (Stocks.ContainsKey(productId))
        {
            Stocks[productId] = new Stock(productId, newQuantity);
        }
    }

    public static Order? GetOrder(string orderId) =>
        Orders.TryGetValue(orderId, out var order) ? order : null;

    public static void SaveOrder(Order order) =>
        Orders[order.Id] = order;

    public static void UpdateOrderStatus(string orderId, OrderStatus status)
    {
        if (Orders.TryGetValue(orderId, out var existingOrder))
        {
            Orders[orderId] = existingOrder.WithStatus(status);
        }
    }

    public static bool CheckDiscountCode(string customerId, string discountCode)
    {
        return UsedDiscountCodes.TryGetValue(customerId, out var codes)
            && codes.Contains(discountCode);
    }

    public static void MarkAsUsed(string customerId, string discountCode)
    {
        if (!UsedDiscountCodes.ContainsKey(customerId))
        {
            UsedDiscountCodes[customerId] = new HashSet<string>();
        }
        UsedDiscountCodes[customerId].Add(discountCode);
    }
}
