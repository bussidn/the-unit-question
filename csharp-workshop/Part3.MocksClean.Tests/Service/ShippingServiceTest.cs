using NSubstitute;
using Part3.MocksClean.Domain;
using Part3.MocksClean.Gateway;
using Part3.MocksClean.Service;
using Part3.MocksClean.Tests.Helper;
using Xunit;

namespace Part3.MocksClean.Tests.Service;

public class ShippingServiceTest
{
    private readonly IShippingGateway _shippingGateway = Substitute.For<IShippingGateway>();
    private readonly GatewayShippingService _shippingService;

    public ShippingServiceTest()
    {
        _shippingService = new GatewayShippingService(_shippingGateway);
    }

    [Fact]
    public void P3_CreateShipment_ReturnsGatewayConfirmation()
    {
        var order = OrderBuilder.Confirmed().WithId("ORDER-001").Build();
        var confirmation = new ShippingConfirmation("ORDER-001", "TRACK-123", "2025-12-25");
        _shippingGateway.CreateShipment(Arg.Any<ShippingRequest>()).Returns(confirmation);

        var result = _shippingService.CreateShipment(order);

        Assert.Equal(confirmation, result);
        _shippingGateway.Received(1).CreateShipment(Arg.Any<ShippingRequest>());
    }

    [Fact]
    public void P3_CancelShipment_DelegatesToGateway()
    {
        _shippingGateway.CancelShipment(Arg.Any<string>()).Returns(true);

        Assert.True(_shippingService.CancelShipment("TRACK-123"));
        _shippingGateway.Received(1).CancelShipment("TRACK-123");
    }
}
