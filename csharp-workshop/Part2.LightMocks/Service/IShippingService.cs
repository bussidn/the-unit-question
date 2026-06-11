using Part2.LightMocks.Domain;

namespace Part2.LightMocks.Service;

public interface IShippingService
{
    ShippingConfirmation CreateShipment(Order order);
    bool CancelShipment(string trackingNumber);
}
