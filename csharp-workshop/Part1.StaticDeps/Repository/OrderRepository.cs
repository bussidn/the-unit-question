using Part1.StaticDeps.Domain;
using Part1.StaticDeps.Infrastructure;

namespace Part1.StaticDeps.Repository;

public class OrderRepository
{
    public Order? FindById(string orderId) =>
        Database.GetOrder(orderId);

    public void UpdateStatus(string orderId, OrderStatus status) =>
        Database.UpdateOrderStatus(orderId, status);

    public void Save(Order order) =>
        Database.SaveOrder(order);
}
