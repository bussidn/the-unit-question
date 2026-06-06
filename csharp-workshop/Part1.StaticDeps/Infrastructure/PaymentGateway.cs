using Part1.StaticDeps.Domain;

namespace Part1.StaticDeps.Infrastructure;

/// <summary>
/// Static payment gateway — requires static mocking (JustMock/Typemock) to test.
/// Java equivalent requires PowerMock.mockStatic().
/// </summary>
public static class PaymentGateway
{
    public static PaymentResult ProcessPayment(string customerId, double amount)
    {
        if (amount > 0)
        {
            return new PaymentResult("", PaymentStatus.Success, Guid.NewGuid().ToString());
        }
        return new PaymentResult("", PaymentStatus.Failed, null);
    }

    public static bool Refund(string transactionId)
    {
        return true;
    }
}
