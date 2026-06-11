using Part4.Behavior.Domain;

namespace Part4.Behavior.Service;

public interface IStockService
{
    List<StockReservation> ReserveStock(IReadOnlyList<OrderItem> items);
    void ReleaseStock(List<StockReservation> reservations);
}
