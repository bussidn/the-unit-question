using NSubstitute;
using Part2.LightMocks.Domain;
using Part2.LightMocks.Repository;
using Part2.LightMocks.Service;
using Xunit;

namespace Part2.LightMocks.Tests.Service;

public class OrderServiceTest
{
    private readonly IStockService _stockService;
    private readonly PricingService _pricingService;
    private readonly IPaymentService _paymentService;
    private readonly IShippingService _shippingService;
    private readonly IOrderRepository _orderRepository;
    private readonly OrderService _orderService;

    public OrderServiceTest()
    {
        _stockService = Substitute.For<IStockService>();
        _pricingService = Substitute.For<PricingService>();
        _paymentService = Substitute.For<IPaymentService>();
        _shippingService = Substitute.For<IShippingService>();
        _orderRepository = Substitute.For<IOrderRepository>();
        _orderService = new OrderService(_orderRepository, _stockService, _pricingService, _paymentService, _shippingService);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // YOUR EXERCISE — See README for full instructions
    // ══════════════════════════════════════════════════════════════════════════

    // --- Step 1: Reject already-used discount codes ---
    // 1.4. Add _discountCodeService = Substitute.For<IDiscountCodeService>(); as a field.
    //      Inject it via the OrderService constructor. Stub with .Returns(true/false).
    // 1.5. Fill in the GIVEN/WHEN/THEN below
    [Fact(Skip = "TODO")]
    public void P2_OrderIsRejected_WhenDiscountCodeIsAlreadyUsed()
    {
        // GIVEN an order with discount code SUMMER20
        // AND this code has already been used by this customer

        // WHEN the customer places the order

        // THEN the order is rejected with reason "Discount code already used"
        // AND no payment is triggered
        //     Hint: _paymentService.DidNotReceiveWithAnyArgs()
    }
    // ✅ 1.6. Run: dotnet test Part2.LightMocks.Tests/ — then back to README for Step 2
    // ─── END OF STEP 1 ──────────────────────────────────────────────────────



    // --- Step 2: Apply discount to the price ---
    // 2.2. Stub _pricingService.CalculateTotal(items, discountCode) to return the discounted total.
    //      Wire all the mocks needed for a successful order.
    // 2.3. Fill in the GIVEN/WHEN/THEN below
    [Fact(Skip = "TODO")]
    public void P2_OrderIsConfirmed_WhenDiscountCodeIsValid()
    {
        // GIVEN an order with 2 items at €55 each (subtotal: €110)
        // AND discount code SUMMER20 (-20%)
        // AND the code has not yet been used by this customer

        // WHEN the customer places the order

        // THEN payment is processed for €105.60
        // AND the order is confirmed
    }

    // ✅ 2.4. Run: dotnet test Part2.LightMocks.Tests/ — then back to README for Step 3
    // ─── END OF STEP 2 ──────────────────────────────────────────────────────



    // --- Step 3: Mark as used after payment ---
    // 3.2. Update the test above to also verify that MarkAsUsed is called on DiscountCodeService.
    //      Hint: _discountCodeService.Received(1).MarkAsUsed(customerId, discountCode);
    // 3.3. Fill in the GIVEN/WHEN/THEN below
    [Fact(Skip = "TODO")]
    public void P2_DiscountCodeIsMarkedAsUsed_AfterSuccessfulPayment()
    {
        // GIVEN an order with a valid discount code
        // AND payment succeeds

        // WHEN the customer places the order

        // THEN the discount code is marked as used
    }
    // ✅ 3.4. Run: dotnet test Part2.LightMocks.Tests/ ✅
    // ─── END OF STEP 3 ──────────────────────────────────────────────────────

    // ══════════════════════════════════════════════════════════════════════════
    // REFERENCE TESTS — existing tests, for inspiration
    // ══════════════════════════════════════════════════════════════════════════

    // ── Validation ────────────────────────────────────────────────────────────

    [Fact]
    public void P2_OrderIsRejected_WhenOrderIsAlreadyConfirmed()
    {
        var order = new Order("ORDER-001", "CUST-001", new List<OrderItem> { new("PROD-001", 1, 20.0) }, OrderStatus.Confirmed, 50.0, "TXN-001", "TRACK-001");

        OrderResult result = _orderService.PlaceOrder(order);

        Assert.IsType<OrderResult.Failure>(result);
        Assert.Equal("Invalid order", ((OrderResult.Failure)result).Reason);
    }

    [Fact]
    public void P2_OrderIsRejected_WhenOrderHasNoItems()
    {
        var order = new Order("ORDER-001", "CUST-001", new List<OrderItem>(), OrderStatus.Pending, 0.0, null, null);

        OrderResult result = _orderService.PlaceOrder(order);

        Assert.IsType<OrderResult.Failure>(result);
        Assert.Equal("Invalid order", ((OrderResult.Failure)result).Reason);
    }

    [Fact]
    public void P2_NoStockIsReserved_WhenOrderIsInvalid()
    {
        var order = new Order("ORDER-001", "CUST-001", new List<OrderItem>(), OrderStatus.Pending, 0.0, null, null);

        _orderService.PlaceOrder(order);

        _stockService.DidNotReceiveWithAnyArgs();
    }

    // ── Happy path ────────────────────────────────────────────────────────────

    [Fact]
    public void P2_OrderIsConfirmed_WhenAllStepsSucceed()
    {
        var order = new Order("ORDER-001", "CUST-001", new List<OrderItem> { new("PROD-001", 1, 20.0) }, OrderStatus.Pending, 0.0, null, null);
        _pricingService.CalculateTotal(Arg.Any<IReadOnlyList<OrderItem>>()).Returns(29.0);
        _paymentService.ProcessPayment(Arg.Any<string>(), Arg.Any<string>(), Arg.Any<double>())
            .Returns(new PaymentResult("ORDER-001", PaymentStatus.Success, "TXN-123"));
        _stockService.ReserveStock(Arg.Any<IReadOnlyList<OrderItem>>())
            .Returns(new List<StockReservation> { new("PROD-001", 1, true) });
        _shippingService.CreateShipment(Arg.Any<Order>())
            .Returns(new ShippingConfirmation("ORDER-001", "TRACK-456", "2024-12-25"));

        OrderResult result = _orderService.PlaceOrder(order);

        Assert.IsType<OrderResult.Success>(result);
        Assert.Equal(OrderStatus.Confirmed, ((OrderResult.Success)result).Order.Status);
    }

    [Fact]
    public void P2_CalculatedPriceIsStoredInOrder_WhenAllStepsSucceed()
    {
        var order = new Order("ORDER-001", "CUST-001", new List<OrderItem> { new("PROD-001", 1, 20.0) }, OrderStatus.Pending, 0.0, null, null);
        _pricingService.CalculateTotal(Arg.Any<IReadOnlyList<OrderItem>>()).Returns(29.0);
        _paymentService.ProcessPayment(Arg.Any<string>(), Arg.Any<string>(), Arg.Any<double>())
            .Returns(new PaymentResult("ORDER-001", PaymentStatus.Success, "TXN-123"));
        _stockService.ReserveStock(Arg.Any<IReadOnlyList<OrderItem>>())
            .Returns(new List<StockReservation> { new("PROD-001", 1, true) });
        _shippingService.CreateShipment(Arg.Any<Order>())
            .Returns(new ShippingConfirmation("ORDER-001", "TRACK-456", "2024-12-25"));

        OrderResult result = _orderService.PlaceOrder(order);

        Assert.Equal(29.0, ((OrderResult.Success)result).Order.TotalPrice);
    }

    [Fact]
    public void P2_ShippingConfirmationIsReturned_WhenAllStepsSucceed()
    {
        var order = new Order("ORDER-001", "CUST-001", new List<OrderItem> { new("PROD-001", 1, 20.0) }, OrderStatus.Pending, 0.0, null, null);
        var shipment = new ShippingConfirmation("ORDER-001", "TRACK-456", "2024-12-25");
        _pricingService.CalculateTotal(Arg.Any<IReadOnlyList<OrderItem>>()).Returns(29.0);
        _paymentService.ProcessPayment(Arg.Any<string>(), Arg.Any<string>(), Arg.Any<double>())
            .Returns(new PaymentResult("ORDER-001", PaymentStatus.Success, "TXN-123"));
        _stockService.ReserveStock(Arg.Any<IReadOnlyList<OrderItem>>())
            .Returns(new List<StockReservation> { new("PROD-001", 1, true) });
        _shippingService.CreateShipment(Arg.Any<Order>()).Returns(shipment);

        OrderResult result = _orderService.PlaceOrder(order);

        Assert.Equal(shipment, ((OrderResult.Success)result).ShippingConfirmation);
    }

    // ── Stock unavailable ─────────────────────────────────────────────────────

    [Fact]
    public void P2_OrderFails_WhenStockIsUnavailable()
    {
        _stockService.ReserveStock(Arg.Any<IReadOnlyList<OrderItem>>())
            .Returns(new List<StockReservation> { new("PROD-001", 1, false) });

        OrderResult result = _orderService.PlaceOrder(new Order("ORDER-001", "CUST-001", new List<OrderItem> { new("PROD-001", 1, 20.0) }, OrderStatus.Pending, 0.0, null, null));

        Assert.IsType<OrderResult.Failure>(result);
        Assert.Equal("Insufficient stock", ((OrderResult.Failure)result).Reason);
    }

    [Fact]
    public void P2_PaymentIsNotCharged_WhenStockIsUnavailable()
    {
        _stockService.ReserveStock(Arg.Any<IReadOnlyList<OrderItem>>())
            .Returns(new List<StockReservation> { new("PROD-001", 1, false) });

        _orderService.PlaceOrder(new Order("ORDER-001", "CUST-001", new List<OrderItem> { new("PROD-001", 1, 20.0) }, OrderStatus.Pending, 0.0, null, null));

        _paymentService.DidNotReceiveWithAnyArgs();
    }

    [Fact]
    public void P2_ShipmentIsNotCreated_WhenStockIsUnavailable()
    {
        _stockService.ReserveStock(Arg.Any<IReadOnlyList<OrderItem>>())
            .Returns(new List<StockReservation> { new("PROD-001", 1, false) });

        _orderService.PlaceOrder(new Order("ORDER-001", "CUST-001", new List<OrderItem> { new("PROD-001", 1, 20.0) }, OrderStatus.Pending, 0.0, null, null));

        _shippingService.DidNotReceiveWithAnyArgs();
    }

    // ── Payment declined ──────────────────────────────────────────────────────

    [Fact]
    public void P2_OrderFails_WhenPaymentIsDeclined()
    {
        _stockService.ReserveStock(Arg.Any<IReadOnlyList<OrderItem>>())
            .Returns(new List<StockReservation> { new("PROD-001", 1, true) });
        _pricingService.CalculateTotal(Arg.Any<IReadOnlyList<OrderItem>>()).Returns(45.0);
        _paymentService.ProcessPayment(Arg.Any<string>(), Arg.Any<string>(), Arg.Any<double>())
            .Returns(new PaymentResult("ORDER-001", PaymentStatus.Failed, null));

        OrderResult result = _orderService.PlaceOrder(new Order("ORDER-001", "CUST-001", new List<OrderItem> { new("PROD-001", 1, 20.0) }, OrderStatus.Pending, 0.0, null, null));

        Assert.IsType<OrderResult.Failure>(result);
        Assert.Equal("Payment failed", ((OrderResult.Failure)result).Reason);
    }

    [Fact]
    public void P2_ShipmentIsNotCreated_WhenPaymentIsDeclined()
    {
        _stockService.ReserveStock(Arg.Any<IReadOnlyList<OrderItem>>())
            .Returns(new List<StockReservation> { new("PROD-001", 1, true) });
        _pricingService.CalculateTotal(Arg.Any<IReadOnlyList<OrderItem>>()).Returns(45.0);
        _paymentService.ProcessPayment(Arg.Any<string>(), Arg.Any<string>(), Arg.Any<double>())
            .Returns(new PaymentResult("ORDER-001", PaymentStatus.Failed, null));

        _orderService.PlaceOrder(new Order("ORDER-001", "CUST-001", new List<OrderItem> { new("PROD-001", 1, 20.0) }, OrderStatus.Pending, 0.0, null, null));

        _shippingService.DidNotReceiveWithAnyArgs();
    }

    [Fact]
    public void P2_StockIsReleased_WhenPaymentIsDeclined()
    {
        _stockService.ReserveStock(Arg.Any<IReadOnlyList<OrderItem>>())
            .Returns(new List<StockReservation> { new("PROD-001", 1, true) });
        _pricingService.CalculateTotal(Arg.Any<IReadOnlyList<OrderItem>>()).Returns(45.0);
        _paymentService.ProcessPayment(Arg.Any<string>(), Arg.Any<string>(), Arg.Any<double>())
            .Returns(new PaymentResult("ORDER-001", PaymentStatus.Failed, null));

        _orderService.PlaceOrder(new Order("ORDER-001", "CUST-001", new List<OrderItem> { new("PROD-001", 1, 20.0) }, OrderStatus.Pending, 0.0, null, null));

        _stockService.Received(1).ReleaseStock(Arg.Any<List<StockReservation>>());
    }
}
