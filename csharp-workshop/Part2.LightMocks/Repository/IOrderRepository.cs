using Part2.LightMocks.Domain;

namespace Part2.LightMocks.Repository;

public interface IOrderRepository
{
    Order? FindById(string orderId);
    void UpdateStatus(string orderId, OrderStatus status);
    void Save(Order order);
}
