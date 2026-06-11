using Part2.LightMocks.Domain;

namespace Part2.LightMocks.Gateway;

public interface IShippingGateway
{
    ShippingConfirmation CreateShipment(ShippingRequest request);
    bool CancelShipment(string trackingNumber);
}
