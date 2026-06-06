using Part4.Behavior.Domain;
using Part4.Behavior.Gateway;

namespace Part4.Behavior.Tests.Helper;

public class InMemoryShippingGateway : IShippingGateway
{
    private ShippingConfirmation? _lastConfirmation;

    public ShippingConfirmation CreateShipment(ShippingRequest request)
    {
        _lastConfirmation = new ShippingConfirmation(
            request.OrderId,
            "TRACK-" + request.OrderId,
            "2024-12-25"
        );
        return _lastConfirmation;
    }

    public bool CancelShipment(string trackingNumber) => true;

    public bool HasCreatedShipment() => _lastConfirmation != null;

    public ShippingConfirmation? LastConfirmation() => _lastConfirmation;
}
