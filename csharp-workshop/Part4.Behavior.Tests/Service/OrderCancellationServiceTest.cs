using NSubstitute;
using Part4.Behavior.Domain;
using Part4.Behavior.Gateway;
using Part4.Behavior.Service;
using Part4.Behavior.Tests.Helper;
using Xunit;

namespace Part4.Behavior.Tests.Service;

public class OrderCancellationServiceTest
{
    private readonly IPaymentGateway _paymentGateway = Substitute.For<IPaymentGateway>();
    private readonly IShippingGateway _shippingGateway = Substitute.For<IShippingGateway>();
    private readonly InMemoryOrderRepository _orderRepository;
    private readonly InMemoryStockRepository _stockRepository;
    private readonly OrderCancellationService _service;

    public OrderCancellationServiceTest()
    {
        _orderRepository = new InMemoryOrderRepository();
        _stockRepository = new InMemoryStockRepository();

        IStockService stockService = new RepositoryStockService(_stockRepository);
        IPaymentService paymentService = new GatewayPaymentService(_paymentGateway);
        IShippingService shippingService = new GatewayShippingService(_shippingGateway);

        _service = new OrderCancellationService(_orderRepository, paymentService, stockService, shippingService);
    }

    [Fact]
    public void P4_CancelOrder_Succeeds_WhenPaymentRefundedAndShipmentCancelled()
    {
        var order = OrderBuilder.Confirmed()
            .WithId("ORDER-001")
            .WithTransaction("TXN-123")
            .WithTracking("TRACK-456")
            .WithItems(new List<OrderItem> { new("PROD-001", 2, 25.0) })
            .Build();

        GivenOrderExists(order);
        GivenStockAvailable("PROD-001", 10);
        GivenRefundSucceeds();
        GivenShipmentCancelled();

        CancellationResult result = _service.CancelOrder("ORDER-001");

        AssertSuccess(result);
        _paymentGateway.Received(1).Refund("TXN-123");
        _shippingGateway.Received(1).CancelShipment("TRACK-456");
        Assert.Equal(OrderStatus.Cancelled, _orderRepository.FindById("ORDER-001")!.Status);
    }

    [Fact]
    public void P4_CancelOrder_Succeeds_WhenNoShipmentExists()
    {
        var order = OrderBuilder.Confirmed()
            .WithId("ORDER-002")
            .WithTransaction("TXN-456")
            .WithoutTracking()
            .WithItems(new List<OrderItem> { new("PROD-001", 1, 30.0) })
            .Build();

        GivenOrderExists(order);
        GivenStockAvailable("PROD-001", 5);
        GivenRefundSucceeds();

        CancellationResult result = _service.CancelOrder("ORDER-002");

        AssertSuccess(result);
        _paymentGateway.Received(1).Refund("TXN-456");
        Assert.Equal(OrderStatus.Cancelled, _orderRepository.FindById("ORDER-002")!.Status);
    }

    [Fact]
    public void P4_CancelOrder_Fails_WhenRefundFails()
    {
        var order = OrderBuilder.Confirmed()
            .WithId("ORDER-001")
            .WithTransaction("TXN-123")
            .WithoutTracking()
            .WithItems(new List<OrderItem> { new("PROD-001", 1, 30.0) })
            .Build();

        GivenOrderExists(order);
        GivenRefundFails();

        CancellationResult result = _service.CancelOrder("ORDER-001");

        AssertFailure(result, "Refund failed");
        Assert.Equal(OrderStatus.Confirmed, _orderRepository.FindById("ORDER-001")!.Status);
    }

    [Fact]
    public void P4_CancelOrder_Fails_WhenOrderDoesNotExist()
    {
        CancellationResult result = _service.CancelOrder("ORDER-UNKNOWN");

        AssertFailure(result, "Order not found");
    }

    private void GivenOrderExists(Order order)
    {
        _orderRepository.With(order);
    }

    private void GivenStockAvailable(string productId, int quantity)
    {
        _stockRepository.WithAvailableStock(productId, quantity);
    }

    private void GivenRefundSucceeds()
    {
        _paymentGateway.Refund(Arg.Any<string>()).Returns(true);
    }

    private void GivenRefundFails()
    {
        _paymentGateway.Refund(Arg.Any<string>()).Returns(false);
    }

    private void GivenShipmentCancelled()
    {
        _shippingGateway.CancelShipment(Arg.Any<string>()).Returns(true);
    }

    private static void AssertSuccess(CancellationResult result)
    {
        Assert.Equal(new CancellationResult.Success(), result);
    }

    private static void AssertFailure(CancellationResult result, string reason)
    {
        Assert.Equal(new CancellationResult.Failure(reason), result);
    }
}
