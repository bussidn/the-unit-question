using Part3.MocksClean.Domain;
using Part3.MocksClean.Repository;

namespace Part3.MocksClean.Service;

public class OrderService
{
    private readonly IOrderRepository _orderRepository;
    private readonly IStockService _stockService;
    private readonly PricingService _pricingService;
    private readonly IPaymentService _paymentService;
    private readonly IShippingService _shippingService;

    public OrderService(
        IOrderRepository orderRepository,
        IStockService stockService,
        PricingService pricingService,
        IPaymentService paymentService,
        IShippingService shippingService)
    {
        _orderRepository = orderRepository;
        _stockService = stockService;
        _pricingService = pricingService;
        _paymentService = paymentService;
        _shippingService = shippingService;
    }

    public OrderResult PlaceOrder(Order order)
    {
        if (!IsValid(order))
        {
            return new OrderResult.Failure(order.Reject(), "Invalid order");
        }

        var reservations = _stockService.ReserveStock(order.Items);
        if (reservations.Any(r => !r.Reserved))
        {
            return new OrderResult.Failure(order.Reject(), "Insufficient stock");
        }

        double totalPrice = _pricingService.CalculateTotal(order.Items);
        var paymentResult = _paymentService.ProcessPayment(order.Id, order.CustomerId, totalPrice);

        if (paymentResult.Status != PaymentStatus.Success)
        {
            _stockService.ReleaseStock(reservations);
            return new OrderResult.Failure(order.Reject(), "Payment failed");
        }

        var shippingConfirmation = _shippingService.CreateShipment(order);
        var confirmedOrder = order.Confirm(totalPrice);
        _orderRepository.Save(confirmedOrder);
        return new OrderResult.Success(confirmedOrder, shippingConfirmation);
    }

    private static bool IsValid(Order order)
    {
        return order.Status == OrderStatus.Pending
            && order.Items != null
            && order.Items.Count > 0;
    }
}
