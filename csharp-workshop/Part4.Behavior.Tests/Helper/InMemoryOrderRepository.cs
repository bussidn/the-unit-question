using Part4.Behavior.Domain;
using Part4.Behavior.Repository;

namespace Part4.Behavior.Tests.Helper;

public class InMemoryOrderRepository : IOrderRepository
{
    private readonly Dictionary<string, Order> _orders = new();

    public InMemoryOrderRepository With(Order order)
    {
        _orders[order.Id] = order;
        return this;
    }

    public Order? FindById(string orderId)
    {
        _orders.TryGetValue(orderId, out var order);
        return order;
    }

    public void UpdateStatus(string orderId, OrderStatus status)
    {
        if (_orders.TryGetValue(orderId, out var order))
        {
            _orders[orderId] = new Order(
                order.Id, order.CustomerId, order.Items,
                status, order.TotalPrice, order.TransactionId, order.TrackingNumber
            );
        }
    }

    public void Save(Order order)
    {
        _orders[order.Id] = order;
    }
}
