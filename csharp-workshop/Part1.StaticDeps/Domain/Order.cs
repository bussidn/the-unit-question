namespace Part1.StaticDeps.Domain;

public record Order(
    string Id,
    string CustomerId,
    IReadOnlyList<OrderItem> Items,
    OrderStatus Status,
    double TotalPrice,
    string? TransactionId,
    string? TrackingNumber)
{
    // Defensive copy — mirrors Java's compact constructor: public Order { items = List.copyOf(items); }
    public IReadOnlyList<OrderItem> Items { get; init; } = Items.ToList().AsReadOnly();

    public Order Confirm(double totalPrice, string transactionId, string trackingNumber) =>
        this with
        {
            Status = OrderStatus.Confirmed,
            TotalPrice = totalPrice,
            TransactionId = transactionId,
            TrackingNumber = trackingNumber
        };

    public Order Reject() =>
        this with { Status = OrderStatus.Rejected };

    public Order WithStatus(OrderStatus status) =>
        this with { Status = status };
}
