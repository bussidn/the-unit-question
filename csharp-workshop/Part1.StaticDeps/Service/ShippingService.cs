using Part1.StaticDeps.Domain;
using Part1.StaticDeps.Infrastructure;

namespace Part1.StaticDeps.Service;

/// <summary>
/// Creates ShippingApi with new — requires constructor mocking (JustMock/Typemock) to test.
/// Java equivalent requires PowerMock to mock the constructor.
/// </summary>
public class ShippingService
{
    public ShippingConfirmation CreateShipment(Order order)
    {
        var api = new ShippingApi();
        var request = new ShippingRequest(
            order.Id,
            order.CustomerId,
            order.Items
                .Select(item => new ShippingItem(item.ProductId, item.Quantity))
                .ToList()
                .AsReadOnly());
        return api.CreateShipment(request);
    }

    public bool CancelShipment(string trackingNumber)
    {
        var api = new ShippingApi();
        return api.CancelShipment(trackingNumber);
    }
}
