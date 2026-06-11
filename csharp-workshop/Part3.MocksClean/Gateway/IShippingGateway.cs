using Part3.MocksClean.Domain;

namespace Part3.MocksClean.Gateway;

public interface IShippingGateway
{
    ShippingConfirmation CreateShipment(ShippingRequest request);
    bool CancelShipment(string trackingNumber);
}
