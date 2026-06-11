using NSubstitute;
using Part3.MocksClean.Domain;
using Part3.MocksClean.Repository;
using Part3.MocksClean.Service;
using Part3.MocksClean.Tests.Helper;
using Xunit;

namespace Part3.MocksClean.Tests.Service;

public class OrderCancellationServiceTest
{
    private readonly IOrderRepository _orderRepository = Substitute.For<IOrderRepository>();
    private readonly IPaymentService _paymentService = Substitute.For<IPaymentService>();
    private readonly IStockService _stockService = Substitute.For<IStockService>();
    private readonly IShippingService _shippingService = Substitute.For<IShippingService>();
    private readonly OrderCancellationService _service;
    private readonly Order _confirmedOrder;

    public OrderCancellationServiceTest()
    {
        _service = new OrderCancellationService(_orderRepository, _paymentService, _stockService, _shippingService);
        _confirmedOrder = OrderBuilder.Confirmed()
            .WithId("ORDER-001")
            .WithTransaction("TXN-123")
            .WithTracking("TRACK-456")
            .Build();
    }

    [Fact]
    public void P3_CancelOrder_Succeeds_WhenAllStepsPass()
    {
        GivenOrderExists(_confirmedOrder);
        GivenRefundSucceeds();
        GivenShipmentCancelled();

        CancellationResult result = _service.CancelOrder(_confirmedOrder.Id);

        AssertSuccess(result);
        _paymentService.Received(1).RefundPayment("TXN-123");
        _shippingService.Received(1).CancelShipment("TRACK-456");
        VerifyStockReleasedFor(_confirmedOrder);
        _orderRepository.Received(1).UpdateStatus("ORDER-001", OrderStatus.Cancelled);
    }

    [Fact]
    public void P3_CancelOrder_Fails_WhenRefundFails()
    {
        GivenOrderExists(_confirmedOrder);
        GivenRefundFails();

        CancellationResult result = _service.CancelOrder("ORDER-001");

        AssertFailure(result, "Refund failed");
        _paymentService.Received(1).RefundPayment("TXN-123");
        _orderRepository.DidNotReceive().UpdateStatus(Arg.Any<string>(), Arg.Any<OrderStatus>());
        _shippingService.DidNotReceiveWithAnyArgs();
        _stockService.DidNotReceiveWithAnyArgs();
    }

    [Fact]
    public void P3_CancelOrder_SkipsShipmentCancellation_WhenOrderHasNoTrackingNumber()
    {
        var orderWithoutShipment = OrderBuilder.Confirmed()
            .WithId("ORDER-002")
            .WithTransaction("TXN-456")
            .WithoutTracking()
            .Build();

        GivenOrderExists(orderWithoutShipment);
        GivenRefundSucceeds();

        CancellationResult result = _service.CancelOrder(orderWithoutShipment.Id);

        AssertSuccess(result);
        _paymentService.Received(1).RefundPayment("TXN-456");
        _shippingService.DidNotReceiveWithAnyArgs();
        VerifyStockReleasedFor(orderWithoutShipment);
        _orderRepository.Received(1).UpdateStatus("ORDER-002", OrderStatus.Cancelled);
    }

    [Fact]
    public void P3_CancelOrder_Fails_WhenOrderDoesNotExist()
    {
        _orderRepository.FindById("ORDER-UNKNOWN").Returns((Order?)null);

        CancellationResult result = _service.CancelOrder("ORDER-UNKNOWN");

        AssertFailure(result, "Order not found");
        _paymentService.DidNotReceiveWithAnyArgs();
        _stockService.DidNotReceiveWithAnyArgs();
        _shippingService.DidNotReceiveWithAnyArgs();
    }

    private void GivenOrderExists(Order order)
    {
        _orderRepository.FindById(order.Id).Returns(order);
    }

    private void GivenRefundSucceeds()
    {
        _paymentService.RefundPayment(Arg.Any<string>()).Returns(true);
    }

    private void GivenRefundFails()
    {
        _paymentService.RefundPayment(Arg.Any<string>()).Returns(false);
    }

    private void GivenShipmentCancelled()
    {
        _shippingService.CancelShipment(Arg.Any<string>()).Returns(true);
    }

    private void VerifyStockReleasedFor(Order order)
    {
        var expectedReservations = order.Items
            .Select(item => new StockReservation(item.ProductId, item.Quantity, true))
            .ToList();
        _stockService.Received(1).ReleaseStock(expectedReservations);
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
