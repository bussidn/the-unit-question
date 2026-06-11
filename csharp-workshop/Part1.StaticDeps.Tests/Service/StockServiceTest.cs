using Part1.StaticDeps.Domain;
using Part1.StaticDeps.Service;
using Xunit;

namespace Part1.StaticDeps.Tests.Service;

/// <summary>
/// All tests in this class require static mocking of <see cref="Part1.StaticDeps.Infrastructure.Database"/>.
/// In Java, Mockito.mockStatic() handles this. In C#, NSubstitute cannot mock static methods.
/// Use JustMock or Typemock for the equivalent capability.
/// </summary>
public class StockServiceTest
{
    [Fact(Skip = "Static mocking requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockStatic().")]
    public void P1_ReserveStock_UpdatesDatabaseAndReturnsReservations()
    {
        // Given
        // Database.GetStock("PROD-001") stubbed to return Stock("PROD-001", 100)
        var items = new List<OrderItem> { new("PROD-001", 5, 10.0) }.AsReadOnly();

        // When
        // stockService.ReserveStock(items)

        // Then
        // result.Count == 1
        // result[0].Reserved == true
        // Database.UpdateStock("PROD-001", 95) called once
    }
}
