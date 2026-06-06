using Part2.LightMocks.Domain;

namespace Part2.LightMocks.Service;

public interface IPaymentService
{
    PaymentResult ProcessPayment(string orderId, string customerId, double amount);
    bool RefundPayment(string transactionId);
}
