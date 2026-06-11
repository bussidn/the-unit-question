using Part3.MocksClean.Domain;
using Part3.MocksClean.Repository;

namespace Part3.MocksClean.Service;

public class RepositoryStockService : IStockService
{
    private readonly IStockRepository _stockRepository;

    public RepositoryStockService(IStockRepository stockRepository)
    {
        _stockRepository = stockRepository;
    }

    public List<StockReservation> ReserveStock(IReadOnlyList<OrderItem> items)
    {
        var reservations = new List<StockReservation>();

        foreach (var item in items)
        {
            var stock = _stockRepository.FindByProductId(item.ProductId);
            if (stock != null && stock.AvailableQuantity >= item.Quantity)
            {
                _stockRepository.UpdateQuantity(item.ProductId, stock.AvailableQuantity - item.Quantity);
                reservations.Add(new StockReservation(item.ProductId, item.Quantity, true));
            }
            else
            {
                reservations.Add(new StockReservation(item.ProductId, item.Quantity, false));
            }
        }

        return reservations;
    }

    public void ReleaseStock(List<StockReservation> reservations)
    {
        foreach (var reservation in reservations)
        {
            if (!reservation.Reserved)
            {
                continue;
            }

            var stock = _stockRepository.FindByProductId(reservation.ProductId);
            if (stock != null)
            {
                _stockRepository.UpdateQuantity(
                    reservation.ProductId,
                    stock.AvailableQuantity + reservation.Quantity
                );
            }
        }
    }
}
