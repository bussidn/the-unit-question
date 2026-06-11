using Part1.StaticDeps.Domain;
using Part1.StaticDeps.Service;
using Xunit;

namespace Part1.StaticDeps.Tests.Service;

/// <summary>
/// All tests require constructor interception of <see cref="Part1.StaticDeps.Infrastructure.ShippingApi"/>.
/// In Java, Mockito.mockConstruction() intercepts every <c>new ShippingApi()</c> call inside
/// the method under test. In C#, NSubstitute cannot intercept constructors.
/// Use JustMock or Typemock for the equivalent capability.
/// </summary>
public class ShippingServiceTest
{
    [Fact(Skip = "mockConstruction requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockConstruction().")]
    public void P1_CreateShipment_CallsApiAndReturnsConfirmation()
    {
        // Given
        // mockConstruction(ShippingApi) stubs CreateShipment to return
        // ShippingConfirmation("ORDER-123", "TRACK-ABC123", "2024-12-25")
        var order = new Order(
            "ORDER-123",
            "CUST-456",
            new List<OrderItem>
            {
                new("PROD-001", 2, 10.0),
                new("PROD-002", 1, 20.0)
            }.AsReadOnly(),
            OrderStatus.Pending,
            0.0,
            null,
            null);

        // When
        // shippingService.CreateShipment(order)

        // Then
        // result == expectedConfirmation
        // exactly 1 ShippingApi was constructed
        // CreateShipment called with request where orderId="ORDER-123",
        //   customerId="CUST-456", items.Count==2
    }

    [Fact(Skip = "mockConstruction requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockConstruction().")]
    public void P1_CancelShipment_CallsApiAndReturnsResult()
    {
        // Given
        // mockConstruction(ShippingApi) stubs CancelShipment("TRACK-ABC123") to return true

        // When
        // shippingService.CancelShipment("TRACK-ABC123")

        // Then result == true, CancelShipment called with "TRACK-ABC123"
    }
}
