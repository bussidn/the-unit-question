using NSubstitute;
using Part2.LightMocks.Domain;
using Part2.LightMocks.Repository;
using Part2.LightMocks.Service;
using Xunit;

namespace Part2.LightMocks.Tests.Service;

public class OrderCancellationServiceTest
{
    [Fact]
    public void P2_CancelOrder_RefundsPayment_WhenOrderExists()
    {
        var orderRepository = Substitute.For<IOrderRepository>();
        var paymentService = Substitute.For<IPaymentService>();
        var stockService = Substitute.For<IStockService>();
        var shippingService = Substitute.For<IShippingService>();
        var service = new OrderCancellationService(orderRepository, paymentService, stockService, shippingService);

        var order = new Order(
            "ORDER-001",
            "CUST-001",
            new List<OrderItem> { new("PROD-001", 2, 25.0) },
            OrderStatus.Confirmed,
            50.0,
            "TXN-123",
            null
        );

        orderRepository.FindById("ORDER-001").Returns(order);
        paymentService.RefundPayment("TXN-123").Returns(true);
        shippingService.CancelShipment("TRACK-456").Returns(true);

        CancellationResult result = service.CancelOrder("ORDER-001");

        Assert.Equal(new CancellationResult.Success(), result);
        paymentService.Received(1).RefundPayment("TXN-123");
        stockService.Received(1).ReleaseStock(new List<StockReservation> { new("PROD-001", 2, true) });
        orderRepository.Received(1).UpdateStatus("ORDER-001", OrderStatus.Cancelled);
        shippingService.DidNotReceiveWithAnyArgs();
    }

    [Fact]
    public void P2_CancelOrder_CancelsShipment_WhenTrackingNumberExists()
    {
        var orderRepository = Substitute.For<IOrderRepository>();
        var paymentService = Substitute.For<IPaymentService>();
        var stockService = Substitute.For<IStockService>();
        var shippingService = Substitute.For<IShippingService>();
        var service = new OrderCancellationService(orderRepository, paymentService, stockService, shippingService);

        var order = new Order(
            "ORDER-001",
            "CUST-001",
            new List<OrderItem> { new("PROD-001", 2, 25.0) },
            OrderStatus.Confirmed,
            50.0,
            "TXN-123",
            "TRACK-456"
        );

        orderRepository.FindById("ORDER-001").Returns(order);
        paymentService.RefundPayment("TXN-123").Returns(true);
        shippingService.CancelShipment("TRACK-456").Returns(true);

        CancellationResult result = service.CancelOrder("ORDER-001");

        Assert.Equal(new CancellationResult.Success(), result);
        paymentService.Received(1).RefundPayment("TXN-123");
        stockService.Received(1).ReleaseStock(new List<StockReservation> { new("PROD-001", 2, true) });
        shippingService.Received(1).CancelShipment("TRACK-456");
        orderRepository.Received(1).UpdateStatus("ORDER-001", OrderStatus.Cancelled);
    }

    [Fact]
    public void P2_CancelOrder_Fails_WhenRefundFails()
    {
        var orderRepository = Substitute.For<IOrderRepository>();
        var paymentService = Substitute.For<IPaymentService>();
        var stockService = Substitute.For<IStockService>();
        var shippingService = Substitute.For<IShippingService>();
        var service = new OrderCancellationService(orderRepository, paymentService, stockService, shippingService);

        var order = new Order(
            "ORDER-001",
            "CUST-001",
            new List<OrderItem> { new("PROD-001", 2, 25.0) },
            OrderStatus.Confirmed,
            50.0,
            "TXN-123",
            "TRACK-456"
        );

        orderRepository.FindById("ORDER-001").Returns(order);
        paymentService.RefundPayment("TXN-123").Returns(false);
        shippingService.CancelShipment("TRACK-456").Returns(true);

        CancellationResult result = service.CancelOrder("ORDER-001");

        Assert.Equal(new CancellationResult.Failure("Refund failed"), result);
        paymentService.Received(1).RefundPayment("TXN-123");
        stockService.DidNotReceive().ReleaseStock(Arg.Any<List<StockReservation>>());
        shippingService.DidNotReceive().CancelShipment(Arg.Any<string>());
        orderRepository.DidNotReceive().UpdateStatus(Arg.Any<string>(), Arg.Any<OrderStatus>());
    }

    [Fact]
    public void P2_CancelOrder_Fails_WhenOrderDoesNotExist()
    {
        var orderRepository = Substitute.For<IOrderRepository>();
        var paymentService = Substitute.For<IPaymentService>();
        var stockService = Substitute.For<IStockService>();
        var shippingService = Substitute.For<IShippingService>();
        var service = new OrderCancellationService(orderRepository, paymentService, stockService, shippingService);

        orderRepository.FindById("ORDER-001").Returns((Order?)null);

        CancellationResult result = service.CancelOrder("ORDER-001");

        Assert.Equal(new CancellationResult.Failure("Order not found"), result);
        orderRepository.Received(1).FindById("ORDER-001");
        paymentService.DidNotReceiveWithAnyArgs();
        stockService.DidNotReceiveWithAnyArgs();
        shippingService.DidNotReceiveWithAnyArgs();
    }
}
