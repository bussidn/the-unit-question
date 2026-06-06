using NSubstitute;
using Part3.MocksClean.Domain;
using Part3.MocksClean.Repository;
using Part3.MocksClean.Service;
using Xunit;

namespace Part3.MocksClean.Tests.Service;

public class StockServiceTest
{
    private readonly IStockRepository _stockRepository = Substitute.For<IStockRepository>();
    private readonly RepositoryStockService _stockService;

    public StockServiceTest()
    {
        _stockService = new RepositoryStockService(_stockRepository);
    }

    [Fact]
    public void P3_ReserveStock_UpdatesRepositoryAndReturnsReservations()
    {
        _stockRepository.FindByProductId("PROD-001").Returns(new Stock("PROD-001", 100));

        var result = _stockService.ReserveStock(new List<OrderItem> { new("PROD-001", 5, 10.0) });

        Assert.Single(result);
        Assert.True(result[0].Reserved);
        _stockRepository.Received(1).UpdateQuantity("PROD-001", 95);
    }

    [Fact]
    public void P3_ReleaseStock_RestoresQuantity_ForSuccessfulReservations()
    {
        _stockRepository.FindByProductId("PROD-001").Returns(new Stock("PROD-001", 90));

        _stockService.ReleaseStock(new List<StockReservation>
        {
            new("PROD-001", 5, true),
            new("PROD-002", 2, false)
        });

        _stockRepository.Received(1).UpdateQuantity("PROD-001", 95);
        _stockRepository.DidNotReceive().FindByProductId("PROD-002");
    }
}
