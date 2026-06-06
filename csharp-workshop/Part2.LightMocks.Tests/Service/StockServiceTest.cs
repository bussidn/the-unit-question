using NSubstitute;
using Part2.LightMocks.Domain;
using Part2.LightMocks.Repository;
using Part2.LightMocks.Service;
using Xunit;

namespace Part2.LightMocks.Tests.Service;

public class StockServiceTest
{
    [Fact]
    public void P2_ReserveStock_UpdatesRepositoryAndReturnsReservations()
    {
        var stockRepository = Substitute.For<IStockRepository>();
        var stockService = new RepositoryStockService(stockRepository);

        var items = new List<OrderItem> { new("PROD-001", 5, 10.0) };
        stockRepository.FindByProductId("PROD-001").Returns(new Stock("PROD-001", 100));

        var result = stockService.ReserveStock(items);

        Assert.Single(result);
        Assert.True(result[0].Reserved);
        stockRepository.Received(1).UpdateQuantity("PROD-001", 95);
    }

    [Fact]
    public void P2_ReleaseStock_RestoresQuantity_ForSuccessfulReservations()
    {
        var stockRepository = Substitute.For<IStockRepository>();
        var stockService = new RepositoryStockService(stockRepository);

        var reservations = new List<StockReservation>
        {
            new("PROD-001", 5, true),
            new("PROD-002", 2, false)
        };

        stockRepository.FindByProductId("PROD-001").Returns(new Stock("PROD-001", 90));

        stockService.ReleaseStock(reservations);

        stockRepository.Received(1).UpdateQuantity("PROD-001", 95);
        stockRepository.DidNotReceive().FindByProductId("PROD-002");
    }
}
