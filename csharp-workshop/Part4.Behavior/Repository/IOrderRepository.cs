using Part4.Behavior.Domain;

namespace Part4.Behavior.Repository;

public interface IOrderRepository
{
    Order? FindById(string orderId);
    void UpdateStatus(string orderId, OrderStatus status);
    void Save(Order order);
}
