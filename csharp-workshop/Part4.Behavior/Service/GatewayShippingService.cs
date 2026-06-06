using Part4.Behavior.Domain;
using Part4.Behavior.Gateway;

namespace Part4.Behavior.Service;

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
