# The Unit Question — Part 4: Behaviour tests

## 🔄 What changed since Part 3

Services now implement **interfaces** (ports). `OrderService` depends on abstractions, not concrete classes.

**Mockito is gone entirely.** Every dependency is a real implementation backed by in-memory data:

```java
orderService = new OrderService(
    orderRepository,                          // InMemoryOrderRepository
    new RepositoryStockService(stockRepo),    // real logic, in-memory data
    new PricingService(),                     // real — same as Part 3
    new GatewayPaymentService(paymentGw),     // real logic, in-memory gateway
    new GatewayShippingService(shippingGw)    // real logic, in-memory gateway
);
```

No `@Mock`. No `when().thenReturn()`. No `verify()`. Tests describe business scenarios with real inputs and check real outcomes.

Have a look at the existing `OrderServiceTest` (`src/test/java/service/`) before you start.

---

## What is provided

Look at the existing `OrderServiceTest` — it has been transformed into a behaviour test. It shows you how to:
- Wire real implementations by hand in `@BeforeEach`
- Use `InMemoryStockRepository` and in-memory gateways
- Assert on real behaviour instead of mock interactions

`InMemoryOrderRepository`, `InMemoryStockRepository` and `InMemoryDiscountCodeRepository` are available in `helper/`.

`DiscountCodeService` is provided with its tests. It exposes two methods:

- **`checkDiscountCode(customerId, discountCode)`** — returns `true` if the code is available for this customer
- **`markAsUsed(customerId, discountCode)`** — marks the code as used for this customer

`PricingService` has also been updated with a new overload: `calculateTotal(items, discountCode)` that applies the discount to the price.

---

## 🎯 Your mission

`OrderService.placeOrder` does not support discount codes yet. Your job is to add this feature step by step, writing tests as you go.

---

### Step 1 — Reject already-used discount codes

**1.1.** Add a nullable `discountCode` field to the `Order` record (the `DiscountCode` enum is already in `domain/`). The `placeOrder(Order)` signature stays the same.

**1.2.** Add `DiscountCodeService` as a dependency. In `placeOrder`: if a discount code is present and **not available**, reject the order.

**Test to write:**

```
GIVEN an order with discount code SUMMER20
AND this code has already been used by this customer

WHEN the customer places the order

THEN the order is rejected with reason "Discount code already used"
AND no payment is triggered
```

> 📝 **1.3.** Now implement in `OrderServiceTest` → look for **Step 1**

---

### Step 2 — Apply the discount to the price

**2.1.** If the discount code is present and available, use `PricingService.calculateTotal(items, discountCode)` instead of the existing call.

**Test to write:**

```
GIVEN an order with 2 items at €55 each (subtotal: €110)
AND discount code SUMMER20 (-20%)
AND the code has not yet been used by this customer

WHEN the customer places the order

THEN payment is processed for €105.60
AND the order is confirmed
```

> 📝 **2.2.** Now implement in `OrderServiceTest` → look for **Step 2**

---

### Step 3 — Mark the code as used after payment

**3.1.** After successful payment, call `DiscountCodeService.markAsUsed(customerId, discountCode)`.

**3.2.** **Update your previous test** to assert the code is marked as used.

> 📝 **3.3.** Now implement in `OrderServiceTest` → look for **Step 3**

---

### 💡 Tips

- Look at the existing `OrderServiceTest` to see how real implementations are wired
- `PricingService` has no dependencies — instantiate it directly: `new PricingService()`

---

### 🤔 What is the "unit" here?

No mocks at all. `placeOrder` runs the entire chain — validation, pricing, stock, payment, shipping, persistence — with real implementations. The unit is **the use case**. Are these still "unit tests"?

---

## ➡️ End of workshop 🎉

