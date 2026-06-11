using Part3.MocksClean.Domain;

namespace Part3.MocksClean.Tests.Helper;

public sealed class OrderBuilder
{
    private string _id = "ORDER-DEFAULT";
    private string _customerId = "CUST-001";
    private IReadOnlyList<OrderItem> _items = new List<OrderItem> { new("PROD-001", 1, 20.0) };
    private OrderStatus _status = OrderStatus.Pending;
    private double _totalPrice = 20.0;
    private string? _transactionId;
    private string? _trackingNumber;

    private OrderBuilder() { }

    public static OrderBuilder AnOrder() => new();

    public static OrderBuilder Confirmed() => AnOrder().WithStatus(OrderStatus.Confirmed);

    public OrderBuilder WithId(string id)
    {
        _id = id;
        return this;
    }

    public OrderBuilder WithCustomerId(string customerId)
    {
        _customerId = customerId;
        return this;
    }

    public OrderBuilder WithItems(IReadOnlyList<OrderItem> items)
    {
        _items = items.ToList().AsReadOnly();
        return this;
    }

    public OrderBuilder WithStatus(OrderStatus status)
    {
        _status = status;
        return this;
    }

    public OrderBuilder WithTotalPrice(double totalPrice)
    {
        _totalPrice = totalPrice;
        return this;
    }

    public OrderBuilder WithTransaction(string transactionId)
    {
        _transactionId = transactionId;
        return this;
    }

    public OrderBuilder WithoutTransaction()
    {
        _transactionId = null;
        return this;
    }

    public OrderBuilder WithTracking(string trackingNumber)
    {
        _trackingNumber = trackingNumber;
        return this;
    }

    public OrderBuilder WithoutTracking()
    {
        _trackingNumber = null;
        return this;
    }

    public Order Build() =>
        new(_id, _customerId, _items, _status, _totalPrice, _transactionId, _trackingNumber);
}
