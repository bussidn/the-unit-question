using Part3.MocksClean.Domain;

namespace Part3.MocksClean.Service;

public interface IShippingService
{
    ShippingConfirmation CreateShipment(Order order);
    bool CancelShipment(string trackingNumber);
}
