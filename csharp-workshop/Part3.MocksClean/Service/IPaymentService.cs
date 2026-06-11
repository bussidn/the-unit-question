using Part3.MocksClean.Domain;

namespace Part3.MocksClean.Service;

public interface IPaymentService
{
    PaymentResult ProcessPayment(string orderId, string customerId, double amount);
    bool RefundPayment(string transactionId);
}
