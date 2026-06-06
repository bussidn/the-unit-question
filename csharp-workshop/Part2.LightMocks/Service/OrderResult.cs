using Part2.LightMocks.Domain;

namespace Part2.LightMocks.Service;

public abstract record OrderResult
{
    public sealed record Success(Order Order, ShippingConfirmation ShippingConfirmation) : OrderResult;
    public sealed record Failure(Order Order, string Reason) : OrderResult;
}
