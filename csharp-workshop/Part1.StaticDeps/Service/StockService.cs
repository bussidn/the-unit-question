using Part1.StaticDeps.Domain;
using Part1.StaticDeps.Infrastructure;

namespace Part1.StaticDeps.Service;

/// <summary>
/// Stock service with hardcoded Database dependency (static calls).
/// Requires static mocking (JustMock/Typemock) to test in isolation.
/// Java equivalent requires PowerMock.mockStatic().
/// </summary>
public class StockService
{
    public List<StockReservation> ReserveStock(IReadOnlyList<OrderItem> items)
    {
        var reservations = new List<StockReservation>();

        foreach (var item in items)
        {
            var stock = Database.GetStock(item.ProductId);
            if (stock is not null && stock.AvailableQuantity >= item.Quantity)
            {
                Database.UpdateStock(item.ProductId, stock.AvailableQuantity - item.Quantity);
                reservations.Add(new StockReservation(item.ProductId, item.Quantity, true));
            }
            else
            {
                reservations.Add(new StockReservation(item.ProductId, item.Quantity, false));
            }
        }

        return reservations;
    }

    public void ReleaseStock(IReadOnlyList<StockReservation> reservations)
    {
        foreach (var reservation in reservations)
        {
            if (!reservation.Reserved)
            {
                continue;
            }

            var stock = Database.GetStock(reservation.ProductId);
            if (stock is not null)
            {
                Database.UpdateStock(
                    reservation.ProductId,
                    stock.AvailableQuantity + reservation.Quantity);
            }
        }
    }
}
