using Part4.Behavior.Domain;
using Part4.Behavior.Service;
using Part4.Behavior.Tests.Helper;
using Xunit;

namespace Part4.Behavior.Tests.Service;

public class StockServiceTest
{
    private readonly InMemoryStockRepository _stockRepository;
    private readonly RepositoryStockService _stockService;

    public StockServiceTest()
    {
        _stockRepository = new InMemoryStockRepository();
        _stockService = new RepositoryStockService(_stockRepository);
    }

    [Fact]
    public void P4_ReserveStock_UpdatesStockAndReturnsReservations()
    {
        _stockRepository.WithAvailableStock("PROD-001", 100);

        var result = _stockService.ReserveStock(new List<OrderItem> { new("PROD-001", 5, 10.0) });

        Assert.Single(result);
        Assert.True(result[0].Reserved);
        Assert.Equal(95, _stockRepository.FindByProductId("PROD-001")!.AvailableQuantity);
    }

    [Fact]
    public void P4_ReleaseStock_RestoresQuantity_ForSuccessfulReservations()
    {
        _stockRepository.WithAvailableStock("PROD-001", 90);

        _stockService.ReleaseStock(new List<StockReservation>
        {
            new("PROD-001", 5, true),
            new("PROD-002", 2, false)
        });

        Assert.Equal(95, _stockRepository.FindByProductId("PROD-001")!.AvailableQuantity);
        Assert.Equal(0, _stockRepository.FindByProductId("PROD-002")!.AvailableQuantity);
    }
}
