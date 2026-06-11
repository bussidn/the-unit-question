using Part4.Behavior.Domain;

namespace Part4.Behavior.Gateway;

public interface IPaymentGateway
{
    PaymentResult Process(PaymentRequest request);
    bool Refund(string transactionId);
}
