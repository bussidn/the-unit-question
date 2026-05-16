# The Unit Question — Part 2: Constructor injection

## 🔄 What changed since Part 1

In Part 1, `OrderService` instantiated all its collaborators (`StockService`, `PricingService`, etc.) directly inside `placeOrder`. Testing that code required **PowerMock-style hacks**: `mockConstruction()` to intercept `new SomeService()` calls, and `mockStatic()` to stub out static helpers.

In Part 2, those same services are now **injected via constructor**:

```java
public OrderService(
    StockService stockService,
    PricingService pricingService,
    PaymentService paymentService,
    ShippingService shippingService
) { ... }
```

That one structural change is enough to make the hacks disappear entirely. Tests can now use plain **`mock()`** calls and pass fakes straight into the constructor:

```java
@BeforeEach
void setUp() {
    stockService    = mock(StockService.class);
    pricingService  = mock(PricingService.class);
    paymentService  = mock(PaymentService.class);
    shippingService = mock(ShippingService.class);
    orderService    = new OrderService(stockService, pricingService, paymentService, shippingService);
}
```

No `mockConstruction()`. No `mockStatic()`. No try-with-resources wrapping every test.

The production logic inside `placeOrder` does **exactly the same thing** as in Part 1 — the only difference is where the dependencies come from. Have a look at the existing `OrderServiceTest` to see how much simpler the test style has become before you start the exercise.

---

## 🏋️ Exercise: Discount codes in order creation

**⏱️ ~25 minutes**

A `DiscountCodeService` has been added to the codebase. It exposes two methods:

- **`checkDiscountCode(customerId, discountCode)`** — returns `true` if the code is available for this customer
- **`markAsUsed(customerId, discountCode)`** — marks the code as used for this customer

`PricingService` has also been updated with a new overload: `calculateTotal(items, discountCode)` that applies the discount to the price.

`OrderService` has not been migrated yet. That's your mission.

---

### 🎯 Your mission

Migrate `OrderService` to support discount codes:

1. Add `DiscountCodeService` as a constructor dependency
2. Add a `discountCode` field to the `Order` record — it should be nullable (the `DiscountCode` enum is already provided in `domain/`). The `placeOrder(Order)` signature stays the same
3. If a discount code is provided: use `DiscountCodeService.checkDiscountCode` — if it returns `true`, the code is available and can be applied; otherwise reject. Calculate the price with the discount, mark as used after payment
4. Add your new test scenarios to the existing `OrderServiceTest` — follow the style already in place

Run the tests: `./gradlew test`

---

### Scenarios to implement in `OrderServiceTest`

#### Scenario 1: Order with a valid discount code

```
GIVEN an order with 2 items at €55 each (subtotal: €110)
AND discount code SUMMER20 (-20%)
AND the code has not yet been used by this customer

WHEN the customer places the order

THEN payment is processed for €105.60
AND the discount code is marked as used
AND the order is confirmed
```

#### Scenario 2: Discount code already used

```
GIVEN an order with discount code SUMMER20
AND this code has already been used by this customer

WHEN the customer places the order

THEN the order is rejected with reason "Discount code already used"
AND no payment is triggered
```

---

### 💡 Tips

- Look at the existing `OrderServiceTest` for style and follow the same patterns

---

## ➡️ Next

Move on to **[Part 3](../part-3-mocks-clean/README.md)**

