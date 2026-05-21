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

## 🏋️ Exercise (~25 min)

`OrderService.placeOrder` does not support discount codes yet. Your job is to add this feature step by step, writing tests as you go.

Look at the existing `OrderServiceTest` for style and follow the same patterns.

---

### Step 1 — Reject already-used discount codes

Add a nullable `discountCode` field to the `Order` record (the `DiscountCode` enum is already in `domain/`). The `placeOrder(Order)` signature stays the same.

A `DiscountCodeService` is available in the codebase. Add it as a constructor dependency. It provides `checkDiscountCode(customerId, discountCode)` — returns `true` if the code is available for this customer.

In `placeOrder`: if a discount code is present and **not available**, reject the order.

**Test to write:**

```
GIVEN an order with discount code SUMMER20
AND this code has already been used by this customer

WHEN the customer places the order

THEN the order is rejected with reason "Discount code already used"
AND no payment is triggered
```

Run: `./gradlew test` ✅

---

### Step 2 — Apply the discount to the price

If the discount code is present and available, use `PricingService.calculateTotal(items, discountCode)` instead of the existing call.

**Test to write:**

```
GIVEN an order with 2 items at €55 each (subtotal: €110)
AND discount code SUMMER20 (-20%)
AND the code has not yet been used by this customer

WHEN the customer places the order

THEN payment is processed for €105.60
AND the order is confirmed
```

Run: `./gradlew test` ✅

---

### Step 3 — Mark the code as used after payment

After successful payment, call `DiscountCodeService.markAsUsed(customerId, discountCode)`.

**Update your previous test** to assert the code is marked as used.

Run: `./gradlew test` ✅

---

## ➡️ Next

Move on to **[Part 3](../part-3-mocks-clean/README.md)**

