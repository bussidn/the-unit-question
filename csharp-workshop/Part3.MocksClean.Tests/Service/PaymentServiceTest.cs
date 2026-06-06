using NSubstitute;
using Part3.MocksClean.Domain;
using Part3.MocksClean.Gateway;
using Part3.MocksClean.Service;
using Xunit;

namespace Part3.MocksClean.Tests.Service;

public class PaymentServiceTest
{
    private readonly IPaymentGateway _paymentGateway = Substitute.For<IPaymentGateway>();
    private readonly GatewayPaymentService _paymentService;

    public PaymentServiceTest()
    {
        _paymentService = new GatewayPaymentService(_paymentGateway);
    }

    [Fact]
    public void P3_ProcessPayment_ReturnsSuccess_WhenGatewayAcceptsPayment()
    {
        var gatewayResponse = new PaymentResult("ORDER-001", PaymentStatus.Success, "TXN-123");
        _paymentGateway.Process(Arg.Any<PaymentRequest>()).Returns(gatewayResponse);

        var result = _paymentService.ProcessPayment("ORDER-001", "CUST-001", 29.0);

        Assert.Equal(PaymentStatus.Success, result.Status);
        Assert.Equal("TXN-123", result.TransactionId);
    }

    [Fact]
    public void P3_ProcessPayment_ReturnsFailed_WhenAmountIsZero()
    {
        var result = _paymentService.ProcessPayment("ORDER-001", "CUST-001", 0.0);

        Assert.Equal(PaymentStatus.Failed, result.Status);
    }

    [Fact]
    public void P3_RefundPayment_ReturnsTrue_WhenGatewayConfirmsRefund()
    {
        _paymentGateway.Refund("TXN-123").Returns(true);

        Assert.True(_paymentService.RefundPayment("TXN-123"));
    }

    [Fact]
    public void P3_RefundPayment_ReturnsFalse_WhenGatewayDeclines()
    {
        _paymentGateway.Refund("TXN-123").Returns(false);

        Assert.False(_paymentService.RefundPayment("TXN-123"));
    }
}
