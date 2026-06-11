using Part4.Behavior.Domain;
using Part4.Behavior.Service;
using Xunit;

namespace Part4.Behavior.Tests.Service;

public class PricingServiceTest
{
    private readonly PricingService _pricingService = new();

    // ─── Without discount code ────────────────────────────────────────────────

    [Fact]
    public void P4_CalculateTotal_WithNoDiscount_AppliesShippingWhenSubtotalBelowThreshold()
    {
        var items = new List<OrderItem> { new("PROD-001", 1, 20.0) };
        // subtotal = 20, tax = 4, shipping = 5 → 29.0
        Assert.Equal(29.0, _pricingService.CalculateTotal(items), 2);
    }

    [Fact]
    public void P4_CalculateTotal_WithNoDiscount_NoShippingWhenSubtotalAtOrAboveThreshold()
    {
        var items = new List<OrderItem> { new("PROD-001", 5, 20.0) };
        // subtotal = 100, tax = 20, shipping = 0 → 120.0
        Assert.Equal(120.0, _pricingService.CalculateTotal(items), 2);
    }

    // ─── With discount code ───────────────────────────────────────────────────

    [Fact]
    public void P4_CalculateTotal_WithDiscount_ThresholdAppliedOnDiscountedSubtotal()
    {
        var items = new List<OrderItem> { new("PROD-001", 2, 55.0) };
        // gross subtotal = 110, after SUMMER20 (-20%) = 88
        // 88 < 100 → shipping = 5, tax = 17.60 → total = 110.60
        Assert.Equal(110.60, _pricingService.CalculateTotal(items, DiscountCode.SUMMER20), 2);
    }

    [Fact]
    public void P4_CalculateTotal_WithDiscount_NoShippingWhenDiscountedSubtotalAboveThreshold()
    {
        var items = new List<OrderItem> { new("PROD-001", 3, 70.0) };
        // gross subtotal = 210, after SUMMER20 (-20%) = 168
        // 168 >= 100 → shipping = 0, tax = 33.60 → total = 201.60
        Assert.Equal(201.60, _pricingService.CalculateTotal(items, DiscountCode.SUMMER20), 2);
    }

    [Fact]
    public void P4_CalculateTotal_WithONCE50_HalvesPriceAndAppliesShipping()
    {
        var items = new List<OrderItem> { new("PROD-001", 2, 55.0) };
        // gross subtotal = 110, after ONCE50 (-50%) = 55
        // 55 < 100 → shipping = 5, tax = 11 → total = 71.0
        Assert.Equal(71.0, _pricingService.CalculateTotal(items, DiscountCode.ONCE50), 2);
    }
}
