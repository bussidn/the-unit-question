using Part3.MocksClean.Domain;

namespace Part3.MocksClean.Service;

public interface IStockService
{
    List<StockReservation> ReserveStock(IReadOnlyList<OrderItem> items);
    void ReleaseStock(List<StockReservation> reservations);
}
