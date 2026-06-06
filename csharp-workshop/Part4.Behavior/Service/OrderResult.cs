using Part4.Behavior.Domain;

namespace Part4.Behavior.Service;

public abstract record OrderResult
{
    public sealed record Success(Order Order, ShippingConfirmation ShippingConfirmation) : OrderResult;
    public sealed record Failure(Order Order, string Reason) : OrderResult;
}
