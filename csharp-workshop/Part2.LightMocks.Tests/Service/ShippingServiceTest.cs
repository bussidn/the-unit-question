using NSubstitute;
using Part2.LightMocks.Domain;
using Part2.LightMocks.Gateway;
using Part2.LightMocks.Service;
using Xunit;

namespace Part2.LightMocks.Tests.Service;

public class ShippingServiceTest
{
    [Fact]
    public void P2_CreateShipment_CallsGatewayAndReturnsConfirmation()
    {
        var shippingGateway = Substitute.For<IShippingGateway>();
        var shippingService = new GatewayShippingService(shippingGateway);

        var order = new Order(
            "ORDER-123",
            "CUST-456",
            new List<OrderItem>
            {
                new("PROD-001", 2, 10.0),
                new("PROD-002", 1, 20.0)
            },
            OrderStatus.Pending,
            0.0,
            null,
            null
        );
        var expectedConfirmation = new ShippingConfirmation("ORDER-123", "TRACK-ABC123", "2024-12-25");

        shippingGateway.CreateShipment(Arg.Any<ShippingRequest>()).Returns(expectedConfirmation);

        var result = shippingService.CreateShipment(order);

        Assert.Equal(expectedConfirmation, result);
        shippingGateway.Received(1).CreateShipment(Arg.Is<ShippingRequest>(request =>
            request.OrderId == "ORDER-123"
            && request.CustomerId == "CUST-456"
            && request.Items.Count == 2
        ));
    }

    [Fact]
    public void P2_CancelShipment_CallsGatewayAndReturnsResult()
    {
        var shippingGateway = Substitute.For<IShippingGateway>();
        var shippingService = new GatewayShippingService(shippingGateway);

        shippingGateway.CreateShipment(Arg.Any<ShippingRequest>()).Returns(
            new ShippingConfirmation("ORDER-123", "TRACK-ABC123", "2024-12-25")
        );
        shippingGateway.CancelShipment("TRACK-ABC123").Returns(true);

        Assert.True(shippingService.CancelShipment("TRACK-ABC123"));
        shippingGateway.Received(1).CancelShipment("TRACK-ABC123");
    }
}
