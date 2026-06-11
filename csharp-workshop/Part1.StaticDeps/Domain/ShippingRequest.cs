namespace Part1.StaticDeps.Domain;

public record ShippingRequest(string OrderId, string CustomerId, IReadOnlyList<ShippingItem> Items)
{
    // Defensive copy — mirrors Java's compact constructor: public ShippingRequest { items = List.copyOf(items); }
    public IReadOnlyList<ShippingItem> Items { get; init; } = Items.ToList().AsReadOnly();
}
