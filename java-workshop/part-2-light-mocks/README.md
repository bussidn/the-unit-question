# The Unit Question — Part 2: Light mocks

## 🔄 What changed since Part 1

Dependencies are now **injected via constructor** instead of instantiated inline. Tests can use plain `mock()` calls and pass them straight into the constructor — no more `mockConstruction`, `mockStatic`, or try-with-resources wrapping every test.

`isValid()` also runs for real now — no more `whenPrivate` to stub it.

The production logic is the same as Part 1; only the wiring changed. Have a look at the existing `OrderServiceTest` (`src/test/java/service/`) before you start.

---

## 🏋️ Exercise (~25 min)

`OrderService.placeOrder` does not support discount codes yet. Your job is to add this feature step by step, writing tests as you go.

---

### Step 1 — Reject already-used discount codes

**1.1.** Add a nullable `discountCode` field to the `Order` record (the `DiscountCode` enum is already in `domain/`). The `placeOrder(Order)` signature stays the same.

**1.2.** A `DiscountCodeService` is available in the codebase. Add it as a constructor dependency. It provides `checkDiscountCode(customerId, discountCode)` — returns `true` if the code is available for this customer.

**1.3.** In `placeOrder`: if a discount code is present and **not available**, reject the order.

**Test to write:**

```
GIVEN an order with discount code SUMMER20
AND this code has already been used by this customer

WHEN the customer places the order

THEN the order is rejected with reason "Discount code already used"
AND no payment is triggered
```

> 📝 **1.4.** Now implement in `OrderServiceTest` → look for **Step 1**

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

### 🤔 What is the "unit" here?

In this part, the unit is **the class**. `OrderService` runs entirely for real, and every external dependency is mocked. We no longer stub internal methods like `isValid`.

---

## ➡️ Next

Move on to **[Part 3](../part-3-mocks-clean/README.md)**

