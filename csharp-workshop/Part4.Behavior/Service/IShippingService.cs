using Part4.Behavior.Domain;

namespace Part4.Behavior.Service;

public interface IShippingService
{
    ShippingConfirmation CreateShipment(Order order);
    bool CancelShipment(string trackingNumber);
}
