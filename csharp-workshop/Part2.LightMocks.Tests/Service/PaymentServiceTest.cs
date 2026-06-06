using NSubstitute;
using Part2.LightMocks.Domain;
using Part2.LightMocks.Gateway;
using Part2.LightMocks.Service;
using Xunit;

namespace Part2.LightMocks.Tests.Service;

public class PaymentServiceTest
{
    [Fact]
    public void P2_ProcessPayment_ReturnsSuccess_WhenGatewaySucceeds()
    {
        var paymentGateway = Substitute.For<IPaymentGateway>();
        var paymentService = new GatewayPaymentService(paymentGateway);

        paymentGateway.Process(Arg.Any<PaymentRequest>())
            .Returns(new PaymentResult("", PaymentStatus.Success, "TXN-789"));

        var result = paymentService.ProcessPayment("ORDER-123", "CUST-456", 100.0);

        Assert.Equal(PaymentStatus.Success, result.Status);
        Assert.Equal("ORDER-123", result.OrderId);
        Assert.Equal("TXN-789", result.TransactionId);
        paymentGateway.Received(1).Process(Arg.Is<PaymentRequest>(request =>
            request.OrderId == "ORDER-123"
            && request.CustomerId == "CUST-456"
            && request.Amount == 100.0
        ));
    }

    [Fact]
    public void P2_ProcessPayment_ReturnsFailure_ForZeroAmount_WithoutCallingGateway()
    {
        var paymentGateway = Substitute.For<IPaymentGateway>();
        var paymentService = new GatewayPaymentService(paymentGateway);

        paymentGateway.Process(Arg.Any<PaymentRequest>())
            .Returns(new PaymentResult("", PaymentStatus.Success, "TXN-789"));

        var result = paymentService.ProcessPayment("ORDER-123", "CUST-456", 0.0);

        Assert.Equal(PaymentStatus.Failed, result.Status);
        Assert.Equal("ORDER-123", result.OrderId);
        paymentGateway.DidNotReceiveWithAnyArgs();
    }

    [Fact]
    public void P2_ProcessPayment_ReturnsFailure_WhenGatewayFails()
    {
        var paymentGateway = Substitute.For<IPaymentGateway>();
        var paymentService = new GatewayPaymentService(paymentGateway);

        paymentGateway.Process(Arg.Any<PaymentRequest>())
            .Returns(new PaymentResult("", PaymentStatus.Failed, null));

        var result = paymentService.ProcessPayment("ORDER-123", "CUST-456", 100.0);

        Assert.Equal(PaymentStatus.Failed, result.Status);
        Assert.Null(result.TransactionId);
    }

    [Fact]
    public void P2_RefundPayment_DelegatesToGateway()
    {
        var paymentGateway = Substitute.For<IPaymentGateway>();
        var paymentService = new GatewayPaymentService(paymentGateway);

        paymentGateway.Refund("TXN-123").Returns(true);

        Assert.True(paymentService.RefundPayment("TXN-123"));
        paymentGateway.Received(1).Refund("TXN-123");
    }
}
