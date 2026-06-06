using Part1.StaticDeps.Domain;
using Part1.StaticDeps.Infrastructure;

namespace Part1.StaticDeps.Service;

/// <summary>
/// Payment service with hardcoded PaymentGateway dependency (static calls).
/// </summary>
public class PaymentService
{
    public PaymentResult ProcessPayment(string orderId, string customerId, double amount)
    {
        if (amount <= 0)
        {
            return new PaymentResult(orderId, PaymentStatus.Failed, null);
        }

        var gatewayResult = PaymentGateway.ProcessPayment(customerId, amount);
        return new PaymentResult(orderId, gatewayResult.Status, gatewayResult.TransactionId);
    }

    public bool RefundPayment(string transactionId) =>
        PaymentGateway.Refund(transactionId);
}
