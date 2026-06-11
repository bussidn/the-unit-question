using NSubstitute;
using Part4.Behavior.Domain;
using Part4.Behavior.Gateway;
using Part4.Behavior.Service;
using Xunit;

namespace Part4.Behavior.Tests.Service;

public class PaymentServiceTest
{
    private readonly IPaymentGateway _paymentGateway = Substitute.For<IPaymentGateway>();
    private readonly GatewayPaymentService _paymentService;

    public PaymentServiceTest()
    {
        _paymentService = new GatewayPaymentService(_paymentGateway);
    }

    [Fact]
    public void P4_ProcessPayment_ReturnsSuccess_WhenGatewayAcceptsPayment()
    {
        _paymentGateway.Process(Arg.Any<PaymentRequest>())
            .Returns(new PaymentResult("ORDER-001", PaymentStatus.Success, "TXN-123"));

        var result = _paymentService.ProcessPayment("ORDER-001", "CUST-001", 29.0);

        Assert.Equal(PaymentStatus.Success, result.Status);
        Assert.Equal("TXN-123", result.TransactionId);
    }

    [Fact]
    public void P4_ProcessPayment_ReturnsFailed_WhenAmountIsZero()
    {
        var result = _paymentService.ProcessPayment("ORDER-001", "CUST-001", 0.0);

        Assert.Equal(PaymentStatus.Failed, result.Status);
        _paymentGateway.DidNotReceiveWithAnyArgs();
    }

    [Fact]
    public void P4_RefundPayment_ReturnsTrue_WhenGatewayConfirmsRefund()
    {
        _paymentGateway.Refund("TXN-123").Returns(true);

        Assert.True(_paymentService.RefundPayment("TXN-123"));
    }

    [Fact]
    public void P4_RefundPayment_ReturnsFalse_WhenGatewayDeclines()
    {
        _paymentGateway.Refund("TXN-123").Returns(false);

        Assert.False(_paymentService.RefundPayment("TXN-123"));
    }
}
