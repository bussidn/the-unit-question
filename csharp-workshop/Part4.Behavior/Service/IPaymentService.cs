using Part4.Behavior.Domain;

namespace Part4.Behavior.Service;

public interface IPaymentService
{
    PaymentResult ProcessPayment(string orderId, string customerId, double amount);
    bool RefundPayment(string transactionId);
}
