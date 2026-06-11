using Part2.LightMocks.Domain;

namespace Part2.LightMocks.Gateway;

public interface IPaymentGateway
{
    PaymentResult Process(PaymentRequest request);
    bool Refund(string transactionId);
}
