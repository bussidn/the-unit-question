using Part3.MocksClean.Domain;

namespace Part3.MocksClean.Gateway;

public interface IPaymentGateway
{
    PaymentResult Process(PaymentRequest request);
    bool Refund(string transactionId);
}
