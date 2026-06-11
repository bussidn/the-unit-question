using Part3.MocksClean.Domain;

namespace Part3.MocksClean.Repository;

public interface IOrderRepository
{
    Order? FindById(string orderId);
    void UpdateStatus(string orderId, OrderStatus status);
    void Save(Order order);
}
