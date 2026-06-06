using Part1.StaticDeps.Domain;
using Part1.StaticDeps.Repository;

namespace Part1.StaticDeps.Service;

public class OrderCancellationService
{
    public CancellationResult CancelOrder(string orderId)
    {
        var paymentService = new PaymentService();
        var stockService = new StockService();
        var shippingService = new ShippingService();
        var orderRepository = new OrderRepository();

        var order = orderRepository.FindById(orderId);
        if (order is null)
        {
            return new CancellationResult.Failure("Order not found");
        }

        if (order.TransactionId is not null && !paymentService.RefundPayment(order.TransactionId))
        {
            return new CancellationResult.Failure("Refund failed");
        }

        var reservations = order.Items
            .Select(item => new StockReservation(item.ProductId, item.Quantity, true))
            .ToList();
        stockService.ReleaseStock(reservations);

        if (order.TrackingNumber is not null)
        {
            shippingService.CancelShipment(order.TrackingNumber);
        }

        orderRepository.UpdateStatus(orderId, OrderStatus.Cancelled);
        return new CancellationResult.Success();
    }
}
