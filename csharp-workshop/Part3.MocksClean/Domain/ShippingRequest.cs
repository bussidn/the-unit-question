namespace Part3.MocksClean.Domain;

public record ShippingRequest(string OrderId, string CustomerId, IReadOnlyList<ShippingItem> Items);
