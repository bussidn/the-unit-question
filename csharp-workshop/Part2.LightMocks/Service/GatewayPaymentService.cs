using Part2.LightMocks.Domain;
using Part2.LightMocks.Gateway;

namespace Part2.LightMocks.Service;

public class GatewayPaymentService : IPaymentService
{
    private readonly IPaymentGateway _paymentGateway;

    public GatewayPaymentService(IPaymentGateway paymentGateway)
    {
        _paymentGateway = paymentGateway;
    }

    public PaymentResult ProcessPayment(string orderId, string customerId, double amount)
    {
        if (amount <= 0)
        {
            return new PaymentResult(orderId, PaymentStatus.Failed, null);
        }

        var request = new PaymentRequest(orderId, customerId, amount);
        var gatewayResult = _paymentGateway.Process(request);
        return new PaymentResult(orderId, gatewayResult.Status, gatewayResult.TransactionId);
    }

    public bool RefundPayment(string transactionId)
    {
        return _paymentGateway.Refund(transactionId);
    }
}
