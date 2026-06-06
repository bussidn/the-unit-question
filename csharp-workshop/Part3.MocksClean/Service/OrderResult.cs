using Part3.MocksClean.Domain;

namespace Part3.MocksClean.Service;

public abstract record OrderResult
{
    public sealed record Success(Order Order, ShippingConfirmation ShippingConfirmation) : OrderResult;
    public sealed record Failure(Order Order, string Reason) : OrderResult;
}
