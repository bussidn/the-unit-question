using Part1.StaticDeps.Domain;
using Part1.StaticDeps.Service;
using Xunit;

namespace Part1.StaticDeps.Tests.Service;

/// <summary>
/// THE STATIC-DEPS APPROACH — Unit = The Method
/// <para>
/// Here, "unit" means the method itself: <see cref="OrderService.PlaceOrder"/>.
/// We isolate it from all collaborators — StockService, PaymentService, ShippingService,
/// and the static PricingService — without touching the OrderService instance itself.
/// </para>
/// <para>
/// In Java (Mockito), this is achieved with:
///   - mockConstruction() — to intercept every new call inside placeOrder()
///   - mockStatic()       — to stub the static call to PricingService.calculateTotal()
/// </para>
/// <para>
/// In C# with NSubstitute, neither constructor interception nor static mocking is
/// supported. The equivalent requires JustMock or Typemock.
/// Tests that depend on those capabilities are marked [Fact(Skip = "...")].
/// </para>
/// <para>
/// The IsValid tests work directly because the method is internal virtual and
/// InternalsVisibleTo is configured in the .csproj.
/// </para>
/// </summary>
public class OrderServiceTest
{
    // ══════════════════════════════════════════════════════════════════════════
    // YOUR EXERCISE — See README for full instructions
    // ══════════════════════════════════════════════════════════════════════════

    // --- Step 1: Reject already-used discount codes ---
    [Fact(Skip = "TODO")]
    public void P1_OrderIsRejected_WhenDiscountCodeIsAlreadyUsed()
    {
        // GIVEN an order with discount code SUMMER20
        // AND this code has already been used by this customer

        // WHEN the customer places the order

        // THEN the order is rejected with reason "Discount code already used"
        // AND no payment is triggered
        //
        // Note: mockConstruction requires JustMock/Typemock in C#.
        // Java equivalent uses Mockito.mockConstruction().
    }

    // --- Step 2: Apply discount to the price ---
    [Fact(Skip = "TODO")]
    public void P1_OrderIsConfirmed_WhenDiscountCodeIsValid()
    {
        // GIVEN an order with 2 items at €55 each (subtotal: €110)
        // AND discount code SUMMER20 (-20%)
        // AND the code has not yet been used by this customer

        // WHEN the customer places the order

        // THEN payment is processed for €105.60
        // AND the order is confirmed
        //
        // Note: mockConstruction requires JustMock/Typemock in C#.
        // Java equivalent uses Mockito.mockConstruction().
    }

    // --- Step 3: Mark as used after payment ---
    [Fact(Skip = "TODO")]
    public void P1_DiscountCodeIsMarkedAsUsed_AfterSuccessfulPayment()
    {
        // GIVEN an order with a valid discount code
        // AND payment succeeds

        // WHEN the customer places the order

        // THEN the discount code is marked as used
        //
        // Note: mockConstruction requires JustMock/Typemock in C#.
        // Java equivalent uses Mockito.mockConstruction().
    }

    // ══════════════════════════════════════════════════════════════════════════
    // REFERENCE TESTS — existing tests, for inspiration
    // ══════════════════════════════════════════════════════════════════════════

    // ========== VALIDATION (IsValid tested directly — internal virtual) ==========

    [Fact]
    public void P1_IsValid_ReturnsTrue_WhenOrderIsPendingWithItems()
    {
        var order = new Order(
            "ORDER-001",
            "CUST-001",
            new List<OrderItem> { new("PROD-001", 1, 10.0) }.AsReadOnly(),
            OrderStatus.Pending,
            0.0,
            null,
            null);

        bool result = new OrderService().IsValid(order);

        Assert.True(result);
    }

    [Fact]
    public void P1_IsValid_ReturnsFalse_WhenOrderIsAlreadyConfirmed()
    {
        var order = new Order(
            "ORDER-001",
            "CUST-001",
            new List<OrderItem> { new("PROD-001", 1, 10.0) }.AsReadOnly(),
            OrderStatus.Confirmed,
            50.0,
            "TXN-001",
            "TRACK-001");

        bool result = new OrderService().IsValid(order);

        Assert.False(result);
    }

    [Fact]
    public void P1_IsValid_ReturnsFalse_WhenOrderHasNoItems()
    {
        var order = new Order(
            "ORDER-001",
            "CUST-001",
            new List<OrderItem>().AsReadOnly(),
            OrderStatus.Pending,
            0.0,
            null,
            null);

        bool result = new OrderService().IsValid(order);

        Assert.False(result);
    }

    // ========== REFERENCE TESTS (require constructor mocking) ====================
    // These tests are skipped because NSubstitute cannot intercept constructors.
    // In Java, Mockito.mockConstruction() handles this transparently.

    [Fact(Skip = "mockConstruction requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockConstruction().")]
    public void P1_OrderIsRejected_WhenValidationFails()
    {
        // In Java: uses spyOn + whenPrivate(spy, "isValid").thenReturn(false)
        // and mockConstruction for all four collaborators.
    }

    [Fact(Skip = "mockConstruction requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockConstruction().")]
    public void P1_OrderIsConfirmed_WhenAllStepsSucceed()
    {
        // In Java: mockConstruction for StockService, PaymentService, ShippingService,
        // OrderRepository + mockStatic for PricingService.
    }

    [Fact(Skip = "mockConstruction requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockConstruction().")]
    public void P1_CalculatedPriceIsSentToPayment()
    {
    }

    [Fact(Skip = "mockConstruction requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockConstruction().")]
    public void P1_CustomerIdIsUsedForPayment()
    {
    }

    [Fact(Skip = "mockConstruction requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockConstruction().")]
    public void P1_TransactionIdIsStoredInConfirmedOrder()
    {
    }

    [Fact(Skip = "mockConstruction requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockConstruction().")]
    public void P1_TrackingNumberIsStoredInConfirmedOrder()
    {
    }

    [Fact(Skip = "mockConstruction requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockConstruction().")]
    public void P1_AllOrderItemsAreCheckedForAvailability()
    {
    }

    [Fact(Skip = "mockConstruction requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockConstruction().")]
    public void P1_OrderFails_WhenStockIsUnavailable()
    {
    }

    [Fact(Skip = "mockConstruction requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockConstruction().")]
    public void P1_PaymentIsNotProcessed_WhenStockIsUnavailable()
    {
    }

    [Fact(Skip = "mockConstruction requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockConstruction().")]
    public void P1_ShipmentIsNotCreated_WhenStockIsUnavailable()
    {
    }

    [Fact(Skip = "mockConstruction requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockConstruction().")]
    public void P1_OrderFails_WhenPaymentIsDeclined()
    {
    }

    [Fact(Skip = "mockConstruction requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockConstruction().")]
    public void P1_StockIsReleased_WhenPaymentFails()
    {
    }

    [Fact(Skip = "mockConstruction requires JustMock/Typemock in C#. Java equivalent uses Mockito.mockConstruction().")]
    public void P1_ShipmentIsNotCreated_WhenPaymentFails()
    {
    }
}
