namespace Part4.Behavior.Domain;

public enum DiscountCode
{
    SUMMER20,
    ONCE50,
    WELCOME
}

public static class DiscountCodeExtensions
{
    public static int GetPercentage(this DiscountCode code) => code switch
    {
        DiscountCode.SUMMER20 => 20,
        DiscountCode.ONCE50   => 50,
        DiscountCode.WELCOME  => 10,
        _ => throw new ArgumentOutOfRangeException(nameof(code), code, null)
    };
}
