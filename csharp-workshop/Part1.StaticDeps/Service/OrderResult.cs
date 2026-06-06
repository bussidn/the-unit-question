using Part1.StaticDeps.Domain;

namespace Part1.StaticDeps.Service;

public abstract record OrderResult
{
    public sealed record Success(Order Order, ShippingConfirmation ShippingConfirmation) : OrderResult;
    public sealed record Failure(Order Order, string Reason) : OrderResult;
}
