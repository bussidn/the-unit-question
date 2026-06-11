using Part2.LightMocks.Domain;

namespace Part2.LightMocks.Service;

public interface IStockService
{
    List<StockReservation> ReserveStock(IReadOnlyList<OrderItem> items);
    void ReleaseStock(List<StockReservation> reservations);
}
