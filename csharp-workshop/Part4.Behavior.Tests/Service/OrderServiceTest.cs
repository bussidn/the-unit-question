using Part4.Behavior.Domain;
using Part4.Behavior.Service;
using Part4.Behavior.Tests.Helper;
using Xunit;

namespace Part4.Behavior.Tests.Service;

public class OrderServiceTest
{
    private readonly InMemoryOrderRepository _orderRepository;
    private readonly InMemoryPaymentGateway _paymentGateway;
    private readonly InMemoryShippingGateway _shippingGateway;
    private readonly InMemoryStockRepository _stockRepository;
    private readonly OrderService _orderService;

    public OrderServiceTest()
    {
        _orderRepository = new InMemoryOrderRepository();
        _stockRepository = new InMemoryStockRepository();
        _paymentGateway = new InMemoryPaymentGateway();
        _shippingGateway = new InMemoryShippingGateway();

        _orderService = new OrderService(
            _orderRepository,
            new RepositoryStockService(_stockRepository),
            new PricingService(),
            new GatewayPaymentService(_paymentGateway),
            new GatewayShippingService(_shippingGateway)
        );
    }

    // ══════════════════════════════════════════════════════════════════════════
    // YOUR EXERCISE — See README for full instructions
    // ══════════════════════════════════════════════════════════════════════════

    // --- Step 1: Reject already-used discount codes ---
    // 1.3. Create an InMemoryDiscountCodeRepository (already provided in Helper/).
    //      Wire a DiscountCodeService into OrderService via the constructor.
    //      Create a given helper to set up the discount code state.
    // 1.4. Fill in the GIVEN/WHEN/THEN below
    [Fact(Skip = "TODO")]
    public void P4_OrderIsRejected_WhenDiscountCodeIsAlreadyUsed()
    {
        // GIVEN an order with discount code SUMMER20
        // AND this code has already been used by this customer

        // WHEN the customer places the order

        // THEN the order is rejected with reason "Discount code already used"
        // AND no payment is triggered
    }
    // ✅ 1.5. Run: dotnet test Part4.Behavior.Tests/ — then back to README for Step 2
    // ─── END OF STEP 1 ──────────────────────────────────────────────────────



    // --- Step 2: Apply discount to the price ---
    // 2.2. Create a given helper for a valid discount code.
    //      Don't forget to set up sufficient stock.
    // 2.3. Fill in the GIVEN/WHEN/THEN below
    [Fact(Skip = "TODO")]
    public void P4_OrderIsConfirmed_WhenDiscountCodeIsValid()
    {
        // GIVEN an order with 2 items at €55 each (subtotal: €110)
        // AND discount code SUMMER20 (-20%)
        // AND the code has not yet been used by this customer
        // AND sufficient stock

        // WHEN the customer places the order

        // THEN payment is processed for €105.60
        // AND the order is confirmed
    }

    // ✅ 2.4. Run: dotnet test Part4.Behavior.Tests/ — then back to README for Step 3
    // ─── END OF STEP 2 ──────────────────────────────────────────────────────



    // --- Step 3: Mark as used after payment ---
    // 3.2. Update the test above to also verify that the discount code is marked as used.
    //      Hint: Assert.True(_discountCodeRepository.IsUsed("CUST-001", DiscountCode.SUMMER20));
    // 3.3. Fill in the GIVEN/WHEN/THEN below
    [Fact(Skip = "TODO")]
    public void P4_DiscountCodeIsMarkedAsUsed_AfterSuccessfulPayment()
    {
        // GIVEN an order with a valid discount code
        // AND payment succeeds

        // WHEN the customer places the order

        // THEN the discount code is marked as used
    }
    // ✅ 3.4. Run: dotnet test Part4.Behavior.Tests/ ✅
    // ─── END OF STEP 3 ──────────────────────────────────────────────────────

    // ══════════════════════════════════════════════════════════════════════════
    // REFERENCE TESTS — existing tests and helpers, for inspiration
    // ══════════════════════════════════════════════════════════════════════════

    // ── Given helpers ─────────────────────────────────────────────────────────

    private static OrderBuilder AnOrder() =>
        OrderBuilder.AnOrder()
            .WithId("ORDER-001")
            .WithCustomerId("CUST-001")
            .WithItems(new List<OrderItem> { new("PROD-001", 1, 20.0) });

    private void GivenStockIsAvailableFor(Order order)
    {
        foreach (var item in order.Items)
        {
            _stockRepository.WithAvailableStock(item.ProductId, item.Quantity);
        }
    }

    private void GivenPaymentIsDeclined()
    {
        _paymentGateway.WillDecline();
    }

    // ── Validation ────────────────────────────────────────────────────────────

    [Fact]
    public void P4_OrderIsRejected_WhenOrderIsAlreadyConfirmed()
    {
        var order = AnOrder().WithStatus(OrderStatus.Confirmed).Build();

        OrderResult result = _orderService.PlaceOrder(order);

        Assert.IsType<OrderResult.Failure>(result);
        Assert.Equal("Invalid order", ((OrderResult.Failure)result).Reason);
    }

    [Fact]
    public void P4_OrderIsRejected_WhenOrderHasNoItems()
    {
        var order = AnOrder().WithItems(new List<OrderItem>()).Build();

        OrderResult result = _orderService.PlaceOrder(order);

        Assert.IsType<OrderResult.Failure>(result);
        Assert.Equal("Invalid order", ((OrderResult.Failure)result).Reason);
    }

    [Fact]
    public void P4_NoPaymentIsCharged_WhenOrderIsInvalid()
    {
        var order = AnOrder().WithItems(new List<OrderItem>()).Build();

        _orderService.PlaceOrder(order);

        Assert.False(_paymentGateway.WasCharged());
    }

    // ── Happy path ────────────────────────────────────────────────────────────

    [Fact]
    public void P4_OrderIsConfirmed_WhenAllStepsSucceed()
    {
        var order = AnOrder().Build();
        GivenStockIsAvailableFor(order);

        OrderResult result = _orderService.PlaceOrder(order);

        Assert.IsType<OrderResult.Success>(result);
        Assert.Equal(OrderStatus.Confirmed, ((OrderResult.Success)result).Order.Status);
    }

    [Fact]
    public void P4_CalculatedPriceIsStoredInOrder_WhenAllStepsSucceed()
    {
        var order = AnOrder().Build();
        GivenStockIsAvailableFor(order);

        OrderResult result = _orderService.PlaceOrder(order);

        double realTotal = 29.0; // subtotal=20, tax=4 (20%), shipping=5 (below 100€ threshold)
        Assert.Equal(realTotal, ((OrderResult.Success)result).Order.TotalPrice);
    }

    [Fact]
    public void P4_ShippingConfirmationIsReturned_WhenAllStepsSucceed()
    {
        var order = AnOrder().Build();
        GivenStockIsAvailableFor(order);

        OrderResult result = _orderService.PlaceOrder(order);

        Assert.Equal(_shippingGateway.LastConfirmation(), ((OrderResult.Success)result).ShippingConfirmation);
    }

    // ── Stock unavailable ─────────────────────────────────────────────────────

    [Fact]
    public void P4_OrderFails_WhenStockIsUnavailable()
    {
        var order = AnOrder().Build();
        // stockRepository is empty: no stock added

        OrderResult result = _orderService.PlaceOrder(order);

        Assert.IsType<OrderResult.Failure>(result);
        Assert.Equal("Insufficient stock", ((OrderResult.Failure)result).Reason);
    }

    [Fact]
    public void P4_PaymentIsNotCharged_WhenStockIsUnavailable()
    {
        var order = AnOrder().Build();

        _orderService.PlaceOrder(order);

        Assert.False(_paymentGateway.WasCharged());
    }

    [Fact]
    public void P4_ShipmentIsNotCreated_WhenStockIsUnavailable()
    {
        var order = AnOrder().Build();

        _orderService.PlaceOrder(order);

        Assert.False(_shippingGateway.HasCreatedShipment());
    }

    // ── Payment declined ──────────────────────────────────────────────────────

    [Fact]
    public void P4_OrderFails_WhenPaymentIsDeclined()
    {
        var order = AnOrder().Build();
        GivenStockIsAvailableFor(order);
        GivenPaymentIsDeclined();

        OrderResult result = _orderService.PlaceOrder(order);

        Assert.IsType<OrderResult.Failure>(result);
        Assert.Equal("Payment failed", ((OrderResult.Failure)result).Reason);
    }

    [Fact]
    public void P4_ShipmentIsNotCreated_WhenPaymentIsDeclined()
    {
        var order = AnOrder().Build();
        GivenStockIsAvailableFor(order);
        GivenPaymentIsDeclined();

        _orderService.PlaceOrder(order);

        Assert.False(_shippingGateway.HasCreatedShipment());
    }

    [Fact]
    public void P4_StockIsReleased_WhenPaymentIsDeclined()
    {
        var order = AnOrder().Build();
        GivenStockIsAvailableFor(order);
        GivenPaymentIsDeclined();

        _orderService.PlaceOrder(order);

        foreach (var item in order.Items)
        {
            Assert.Equal(item.Quantity, _stockRepository.AvailableStock(item.ProductId));
        }
    }
}
