using Part4.Behavior.Domain;
using Part4.Behavior.Repository;

namespace Part4.Behavior.Tests.Helper;

public class InMemoryStockRepository : IStockRepository
{
    private readonly Dictionary<string, int> _stock = new();

    public InMemoryStockRepository WithAvailableStock(string productId, int quantity)
    {
        _stock[productId] = quantity;
        return this;
    }

    public int AvailableStock(string productId)
    {
        _stock.TryGetValue(productId, out var qty);
        return qty;
    }

    public Stock? FindByProductId(string productId)
    {
        _stock.TryGetValue(productId, out var qty);
        return new Stock(productId, qty);
    }

    public void UpdateQuantity(string productId, int newQuantity)
    {
        _stock[productId] = newQuantity;
    }
}
