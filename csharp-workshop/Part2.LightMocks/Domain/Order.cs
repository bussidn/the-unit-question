namespace Part2.LightMocks.Domain;

public record Order(
    string Id,
    string CustomerId,
    IReadOnlyList<OrderItem> Items,
    OrderStatus Status,
    double TotalPrice,
    string? TransactionId,
    string? TrackingNumber)
{
    public Order(
        string id,
        string customerId,
        IReadOnlyList<OrderItem> items,
        OrderStatus status,
        double totalPrice,
        string? transactionId,
        string? trackingNumber)
        : this(id, customerId, items.ToList().AsReadOnly(), status, totalPrice, transactionId, trackingNumber)
    {
    }

    public Order Confirm(double totalPrice) =>
        this with { Status = OrderStatus.Confirmed, TotalPrice = totalPrice };

    public Order Reject() =>
        this with { Status = OrderStatus.Rejected };
}
