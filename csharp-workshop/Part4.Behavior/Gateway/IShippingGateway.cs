using Part4.Behavior.Domain;

namespace Part4.Behavior.Gateway;

public interface IShippingGateway
{
    ShippingConfirmation CreateShipment(ShippingRequest request);
    bool CancelShipment(string trackingNumber);
}
