using NSubstitute;
using Part3.MocksClean.Domain;
using Part3.MocksClean.Repository;
using Part3.MocksClean.Service;
using Part3.MocksClean.Tests.Helper;
using Xunit;

namespace Part3.MocksClean.Tests.Service;

public class OrderServiceTest
{
    private readonly IStockService _stockService = Substitute.For<IStockService>();
    private readonly IPaymentService _paymentService = Substitute.For<IPaymentService>();
    private readonly IShippingService _shippingService = Substitute.For<IShippingService>();
    private readonly IOrderRepository _orderRepository = Substitute.For<IOrderRepository>();
    private readonly OrderService _orderService;

    public OrderServiceTest()
    {
        _orderService = new OrderService(_orderRepository, _stockService, new PricingService(), _paymentService, _shippingService);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // YOUR EXERCISE — See README for full instructions
    // ══════════════════════════════════════════════════════════════════════════

    // --- Step 1: Reject already-used discount codes ---
    // 1.4. Add a _discountCodeService = Substitute.For<IDiscountCodeService>(); field.
    //      Inject it via the OrderService constructor. Create a given helper (e.g. GivenDiscountCodeIsAlreadyUsed).
    // 1.5. Fill in the GIVEN/WHEN/THEN below
    [Fact(Skip = "TODO")]
    public void P3_OrderIsRejected_WhenDiscountCodeIsAlreadyUsed()
    {
        // GIVEN an order with discount code SUMMER20
        // AND this code has already been used by this customer

        // WHEN the customer places the order

        // THEN the order is rejected with reason "Discount code already used"
        // AND no payment is triggered
        //     Hint: _paymentService.DidNotReceiveWithAnyArgs()
    }
    // ✅ 1.6. Run: dotnet test Part3.MocksClean.Tests/ — then back to README for Step 2
    // ─── END OF STEP 1 ──────────────────────────────────────────────────────



    // --- Step 2: Apply discount to the price ---
    // 2.2. Create a given helper for a valid discount code (e.g. GivenDiscountCodeIsValid).
    //      Wire all given helpers needed for a successful order (stock, payment, shipping).
    // 2.3. Fill in the GIVEN/WHEN/THEN below
    [Fact(Skip = "TODO")]
    public void P3_OrderIsConfirmed_WhenDiscountCodeIsValid()
    {
        // GIVEN an order with 2 items at €55 each (subtotal: €110)
        // AND discount code SUMMER20 (-20%)
        // AND the code has not yet been used by this customer

        // WHEN the customer places the order

        // THEN payment is processed for €105.60
        // AND the order is confirmed
    }

    // ✅ 2.4. Run: dotnet test Part3.MocksClean.Tests/ — then back to README for Step 3
    // ─── END OF STEP 2 ──────────────────────────────────────────────────────



    // --- Step 3: Mark as used after payment ---
    // 3.2. Update the test above to also verify that MarkAsUsed is called on DiscountCodeService.
    //      Hint: _discountCodeService.Received(1).MarkAsUsed(customerId, discountCode);
    // 3.3. Fill in the GIVEN/WHEN/THEN below
    [Fact(Skip = "TODO")]
    public void P3_DiscountCodeIsMarkedAsUsed_AfterSuccessfulPayment()
    {
        // GIVEN an order with a valid discount code
        // AND payment succeeds

        // WHEN the customer places the order

        // THEN the discount code is marked as used
    }
    // ✅ 3.4. Run: dotnet test Part3.MocksClean.Tests/ ✅
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

    private string GivenPaymentSucceedsFor(Order order)
    {
        var txnId = "TXN-" + order.Id;
        _paymentService.ProcessPayment(Arg.Any<string>(), Arg.Any<string>(), Arg.Any<double>())
            .Returns(new PaymentResult(order.Id, PaymentStatus.Success, txnId));
        return txnId;
    }

    private void GivenPaymentIsDeclinedFor(Order order)
    {
        _paymentService.ProcessPayment(Arg.Any<string>(), Arg.Any<string>(), Arg.Any<double>())
            .Returns(new PaymentResult(order.Id, PaymentStatus.Failed, null));
    }

    private List<StockReservation> GivenReservationSucceedsFor(Order order)
    {
        var reservations = order.Items
            .Select(item => new StockReservation(item.ProductId, item.Quantity, true))
            .ToList();
        _stockService.ReserveStock(Arg.Any<IReadOnlyList<OrderItem>>()).Returns(reservations);
        return reservations;
    }

    private List<StockReservation> GivenReservationFailsFor(Order order)
    {
        var reservations = order.Items
            .Select(item => new StockReservation(item.ProductId, item.Quantity, false))
            .ToList();
        _stockService.ReserveStock(Arg.Any<IReadOnlyList<OrderItem>>()).Returns(reservations);
        return reservations;
    }

    private ShippingConfirmation GivenShipmentCreatedFor(Order order)
    {
        var shipment = new ShippingConfirmation(order.Id, "TRACK-456", "2024-12-25");
        _shippingService.CreateShipment(Arg.Any<Order>()).Returns(shipment);
        return shipment;
    }

    // ── Validation ────────────────────────────────────────────────────────────

    [Fact]
    public void P3_OrderIsRejected_WhenOrderIsAlreadyConfirmed()
    {
        var order = AnOrder().WithStatus(OrderStatus.Confirmed).Build();

        OrderResult result = _orderService.PlaceOrder(order);

        Assert.IsType<OrderResult.Failure>(result);
        Assert.Equal("Invalid order", ((OrderResult.Failure)result).Reason);
    }

    [Fact]
    public void P3_OrderIsRejected_WhenOrderHasNoItems()
    {
        var order = AnOrder().WithItems(new List<OrderItem>()).Build();

        OrderResult result = _orderService.PlaceOrder(order);

        Assert.IsType<OrderResult.Failure>(result);
        Assert.Equal("Invalid order", ((OrderResult.Failure)result).Reason);
    }

    [Fact]
    public void P3_NoStockIsReserved_WhenOrderIsInvalid()
    {
        var order = AnOrder().WithItems(new List<OrderItem>()).Build();

        _orderService.PlaceOrder(order);

        _stockService.DidNotReceiveWithAnyArgs();
    }

    // ── Happy path ────────────────────────────────────────────────────────────

    [Fact]
    public void P3_OrderIsConfirmed_WhenAllStepsSucceed()
    {
        var order = AnOrder().Build();
        GivenPaymentSucceedsFor(order);
        GivenReservationSucceedsFor(order);
        GivenShipmentCreatedFor(order);

        OrderResult result = _orderService.PlaceOrder(order);

        Assert.IsType<OrderResult.Success>(result);
        Assert.Equal(OrderStatus.Confirmed, ((OrderResult.Success)result).Order.Status);
    }

    [Fact]
    public void P3_CalculatedPriceIsStoredInOrder_WhenAllStepsSucceed()
    {
        // 1 x €20.00 → subtotal €20 + tax 20% (€4) + shipping €5 = €29.00
        var order = AnOrder().Build();
        GivenPaymentSucceedsFor(order);
        GivenReservationSucceedsFor(order);
        GivenShipmentCreatedFor(order);

        OrderResult result = _orderService.PlaceOrder(order);

        Assert.Equal(29.0, ((OrderResult.Success)result).Order.TotalPrice);
    }

    [Fact]
    public void P3_ShippingConfirmationIsReturned_WhenAllStepsSucceed()
    {
        var order = AnOrder().Build();
        GivenPaymentSucceedsFor(order);
        GivenReservationSucceedsFor(order);
        var shipment = GivenShipmentCreatedFor(order);

        OrderResult result = _orderService.PlaceOrder(order);

        Assert.Equal(shipment, ((OrderResult.Success)result).ShippingConfirmation);
    }

    // ── Stock unavailable ─────────────────────────────────────────────────────

    [Fact]
    public void P3_OrderFails_WhenStockIsUnavailable()
    {
        var order = AnOrder().Build();
        GivenReservationFailsFor(order);

        OrderResult result = _orderService.PlaceOrder(order);

        Assert.IsType<OrderResult.Failure>(result);
        Assert.Equal("Insufficient stock", ((OrderResult.Failure)result).Reason);
    }

    [Fact]
    public void P3_PaymentIsNotCharged_WhenStockIsUnavailable()
    {
        var order = AnOrder().Build();
        GivenReservationFailsFor(order);

        _orderService.PlaceOrder(order);

        _paymentService.DidNotReceiveWithAnyArgs();
    }

    [Fact]
    public void P3_ShipmentIsNotCreated_WhenStockIsUnavailable()
    {
        var order = AnOrder().Build();
        GivenReservationFailsFor(order);

        _orderService.PlaceOrder(order);

        _shippingService.DidNotReceiveWithAnyArgs();
    }

    // ── Payment declined ──────────────────────────────────────────────────────

    [Fact]
    public void P3_OrderFails_WhenPaymentIsDeclined()
    {
        var order = AnOrder().Build();
        GivenReservationSucceedsFor(order);
        GivenPaymentIsDeclinedFor(order);

        OrderResult result = _orderService.PlaceOrder(order);

        Assert.IsType<OrderResult.Failure>(result);
        Assert.Equal("Payment failed", ((OrderResult.Failure)result).Reason);
    }

    [Fact]
    public void P3_ShipmentIsNotCreated_WhenPaymentIsDeclined()
    {
        var order = AnOrder().Build();
        GivenReservationSucceedsFor(order);
        GivenPaymentIsDeclinedFor(order);

        _orderService.PlaceOrder(order);

        _shippingService.DidNotReceiveWithAnyArgs();
    }

    [Fact]
    public void P3_StockIsReleased_WhenPaymentIsDeclined()
    {
        var order = AnOrder().Build();
        var reservations = GivenReservationSucceedsFor(order);
        GivenPaymentIsDeclinedFor(order);

        _orderService.PlaceOrder(order);

        _stockService.Received(1).ReleaseStock(reservations);
    }
}
