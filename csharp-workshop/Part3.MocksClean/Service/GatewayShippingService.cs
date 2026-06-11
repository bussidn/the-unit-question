using Part3.MocksClean.Domain;
using Part3.MocksClean.Gateway;

namespace Part3.MocksClean.Service;

public class GatewayShippingService : IShippingService
{
    private readonly IShippingGateway _shippingGateway;

    public GatewayShippingService(IShippingGateway shippingGateway)
    {
        _shippingGateway = shippingGateway;
    }

    public ShippingConfirmation CreateShipment(Order order)
    {
        var request = new ShippingRequest(
            order.Id,
            order.CustomerId,
            order.Items.Select(item => new ShippingItem(item.ProductId, item.Quantity)).ToList()
        );
        return _shippingGateway.CreateShipment(request);
    }

    public bool CancelShipment(string trackingNumber)
    {
        return _shippingGateway.CancelShipment(trackingNumber);
    }
}
