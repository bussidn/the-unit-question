namespace Part2.LightMocks.Domain;

public record ShippingRequest(string OrderId, string CustomerId, IReadOnlyList<ShippingItem> Items);
