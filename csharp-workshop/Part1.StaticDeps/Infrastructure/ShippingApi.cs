using Part1.StaticDeps.Domain;

namespace Part1.StaticDeps.Infrastructure;

/// <summary>
/// External API client — instantiated directly in services (bad practice).
/// Requires constructor mocking (JustMock/Typemock) to test in isolation.
/// Java equivalent requires PowerMock to mock the constructor.
/// </summary>
public class ShippingApi
{
    private readonly string _baseUrl = "https://shipping.example.com";

    public ShippingConfirmation CreateShipment(ShippingRequest request)
    {
        Console.WriteLine($"Calling shipping API at {_baseUrl}");
        return new ShippingConfirmation(
            request.OrderId,
            "TRACK-" + Guid.NewGuid().ToString()[..8],
            "2024-12-25");
    }

    public bool CancelShipment(string trackingNumber)
    {
        Console.WriteLine($"Cancelling shipment via {_baseUrl} for {trackingNumber}");
        return true;
    }
}
