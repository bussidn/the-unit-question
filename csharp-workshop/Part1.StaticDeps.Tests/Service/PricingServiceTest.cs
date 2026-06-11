using Part1.StaticDeps.Domain;
using Part1.StaticDeps.Service;
using Xunit;

namespace Part1.StaticDeps.Tests.Service;

public class PricingServiceTest
{
    // ─── Without discount code ────────────────────────────────────────────────

    [Fact]
    public void P1_CalculateTotal_WithNoDiscount_AppliesShippingWhenSubtotalBelowThreshold()
    {
        var items = new List<OrderItem> { new("PROD-001", 1, 20.0) }.AsReadOnly();
        // subtotal = 20, tax = 4, shipping = 5 → 29.0
        Assert.Equal(29.0, PricingService.CalculateTotal(items), precision: 2);
    }

    [Fact]
    public void P1_CalculateTotal_WithNoDiscount_NoShippingWhenSubtotalAtOrAboveThreshold()
    {
        var items = new List<OrderItem> { new("PROD-001", 5, 20.0) }.AsReadOnly();
        // subtotal = 100, tax = 20, shipping = 0 → 120.0
        Assert.Equal(120.0, PricingService.CalculateTotal(items), precision: 2);
    }

    // ─── With discount code ───────────────────────────────────────────────────

    [Fact]
    public void P1_CalculateTotal_WithDiscount_ThresholdAppliedOnDiscountedSubtotal()
    {
        var items = new List<OrderItem> { new("PROD-001", 2, 55.0) }.AsReadOnly();
        // gross subtotal = 110, after SUMMER20 (-20%) = 88
        // 88 < 100 → shipping = 5, tax = 17.60 → total = 110.60
        Assert.Equal(110.60, PricingService.CalculateTotal(items, DiscountCode.SUMMER20), precision: 2);
    }

    [Fact]
    public void P1_CalculateTotal_WithDiscount_NoShippingWhenDiscountedSubtotalAboveThreshold()
    {
        var items = new List<OrderItem> { new("PROD-001", 3, 70.0) }.AsReadOnly();
        // gross subtotal = 210, after SUMMER20 (-20%) = 168
        // 168 >= 100 → shipping = 0, tax = 33.60 → total = 201.60
        Assert.Equal(201.60, PricingService.CalculateTotal(items, DiscountCode.SUMMER20), precision: 2);
    }

    [Fact]
    public void P1_CalculateTotal_WithONCE50_HalvesPriceAndAppliesShipping()
    {
        var items = new List<OrderItem> { new("PROD-001", 2, 55.0) }.AsReadOnly();
        // gross subtotal = 110, after ONCE50 (-50%) = 55
        // 55 < 100 → shipping = 5, tax = 11 → total = 71.0
        Assert.Equal(71.0, PricingService.CalculateTotal(items, DiscountCode.ONCE50), precision: 2);
    }
}
