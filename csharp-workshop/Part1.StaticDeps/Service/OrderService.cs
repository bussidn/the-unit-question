using Part1.StaticDeps.Domain;
using Part1.StaticDeps.Repository;

namespace Part1.StaticDeps.Service;

/// <summary>
/// Main orchestrator — creates all dependencies internally.
/// This is typical legacy code: no dependency injection, everything is instantiated inline.
/// </summary>
public class OrderService
{
    public OrderResult PlaceOrder(Order order)
    {
        var stockService = new StockService();
        var paymentService = new PaymentService();
        var shippingService = new ShippingService();
        var orderRepository = new OrderRepository();

        if (!IsValid(order))
        {
            return new OrderResult.Failure(order.Reject(), "Invalid order");
        }

        var reservations = stockService.ReserveStock(order.Items);
        bool hasUnavailableItems = reservations.Any(r => !r.Reserved);
        if (hasUnavailableItems)
        {
            return new OrderResult.Failure(order.Reject(), "Insufficient stock");
        }

        double totalPrice = PricingService.CalculateTotal(order.Items);

        var payment = paymentService.ProcessPayment(order.Id, order.CustomerId, totalPrice);
        if (payment.Status != PaymentStatus.Success)
        {
            stockService.ReleaseStock(reservations);
            return new OrderResult.Failure(order.Reject(), "Payment failed");
        }

        var shipping = shippingService.CreateShipment(order);
        var confirmedOrder = order.Confirm(
            totalPrice,
            payment.TransactionId!,
            shipping.TrackingNumber);

        orderRepository.Save(confirmedOrder);
        return new OrderResult.Success(confirmedOrder, shipping);
    }

    // package-private in Java → internal virtual in C# to allow NSubstitute partial mock
    // [assembly: InternalsVisibleTo("Part1.StaticDeps.Tests")] is declared in the .csproj
    internal virtual bool IsValid(Order order) =>
        order.Status == OrderStatus.Pending
        && order.Items is not null
        && order.Items.Count > 0;
}
