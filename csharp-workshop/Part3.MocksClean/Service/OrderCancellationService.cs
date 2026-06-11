using Part3.MocksClean.Domain;
using Part3.MocksClean.Repository;

namespace Part3.MocksClean.Service;

public class OrderCancellationService
{
    private readonly IOrderRepository _orderRepository;
    private readonly IPaymentService _paymentService;
    private readonly IStockService _stockService;
    private readonly IShippingService _shippingService;

    public OrderCancellationService(
        IOrderRepository orderRepository,
        IPaymentService paymentService,
        IStockService stockService,
        IShippingService shippingService)
    {
        _orderRepository = orderRepository;
        _paymentService = paymentService;
        _stockService = stockService;
        _shippingService = shippingService;
    }

    public CancellationResult CancelOrder(string orderId)
    {
        var order = _orderRepository.FindById(orderId);
        if (order == null)
        {
            return new CancellationResult.Failure("Order not found");
        }

        if (order.Status != OrderStatus.Confirmed)
        {
            return new CancellationResult.Failure("Order cannot be cancelled");
        }

        if (order.TransactionId != null)
        {
            // THE TRAP: treats false as ERROR, but PaymentService means "nothing to refund"
            if (!_paymentService.RefundPayment(order.TransactionId))
            {
                return new CancellationResult.Failure("Refund failed");
            }
        }

        if (order.TrackingNumber != null && !_shippingService.CancelShipment(order.TrackingNumber))
        {
            return new CancellationResult.Failure("Shipment cancellation failed");
        }

        var reservations = order.Items
            .Select(item => new StockReservation(item.ProductId, item.Quantity, true))
            .ToList();
        _stockService.ReleaseStock(reservations);
        _orderRepository.UpdateStatus(orderId, OrderStatus.Cancelled);
        return new CancellationResult.Success();
    }
}
