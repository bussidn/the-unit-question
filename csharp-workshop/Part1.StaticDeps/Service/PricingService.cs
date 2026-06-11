using Part1.StaticDeps.Domain;

namespace Part1.StaticDeps.Service;

/// <summary>
/// Static utility class for price calculations.
/// </summary>
public static class PricingService
{
    public const double TaxRate = 0.20;
    public const double FreeShippingThreshold = 100.0;
    public const double ShippingCost = 5.00;

    public static double CalculateTotal(IReadOnlyList<OrderItem> items)
    {
        double subtotal = CalculateSubtotal(items);
        double tax = CalculateTax(subtotal);
        double shipping = CalculateShipping(subtotal);
        return subtotal + tax + shipping;
    }

    public static double CalculateTotal(IReadOnlyList<OrderItem> items, DiscountCode discountCode)
    {
        double subtotal = CalculateSubtotal(items);
        double discountedSubtotal = ApplyDiscount(subtotal, discountCode);
        double tax = CalculateTax(discountedSubtotal);
        double shipping = CalculateShipping(discountedSubtotal);
        return discountedSubtotal + tax + shipping;
    }

    private static double CalculateSubtotal(IReadOnlyList<OrderItem> items) =>
        items.Sum(item => item.Quantity * item.UnitPrice);

    private static double ApplyDiscount(double subtotal, DiscountCode discountCode)
    {
        double discountFactor = 1.0 - (discountCode.GetPercentage() / 100.0);
        return subtotal * discountFactor;
    }

    private static double CalculateTax(double subtotal) =>
        subtotal * TaxRate;

    private static double CalculateShipping(double subtotal) =>
        subtotal >= FreeShippingThreshold ? 0.0 : ShippingCost;
}
