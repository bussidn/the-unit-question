# The Unit Question — Part 3: Clean mocks

## 🔄 What changed since Part 2

Same production code, same dependencies. The change is in **test design**:

- An `OrderBuilder` (in `Helper/`) makes test data more readable.
- Recurring setup is factored into **given helpers** (`GivenPaymentSucceedsFor`, `GivenReservationSucceedsFor`, etc.).
- `PricingService` is no longer substituted — it's pure computation, so tests assert on real calculated values.

Have a look at the existing `OrderServiceTest` (`Part3.MocksClean.Tests/Service/`) before you start.

---

## 🛠️ Setup

```bash
cd the-unit-question/csharp-workshop
dotnet test Part3.MocksClean.Tests/
```

💡 Open `Part3.MocksClean/` as a **separate IDE project** to avoid editing the wrong `OrderService`.

---

## 🪂 Fast track — production code

The production code is identical to Part 2. **Want to focus on the tests?** Merge the reference branch to fast-track the production code:

```bash
git merge --no-edit origin/part-3-prod
```

---

## 🏋️ Exercise (~13 min)

`OrderService.PlaceOrder` does not support discount codes yet. Your job is to add this feature step by step, writing tests as you go.

---

### Step 1 — Reject already-used discount codes

Objective: reject an order if the discount code has already been used.

```
GIVEN an order with discount code SUMMER20
AND this code has already been used by this customer

WHEN the customer places the order

THEN the order is rejected with reason "Discount code already used"
AND no payment is triggered
```

**1.1.** Add a `discountCode` field to the `Order` record (the `DiscountCode` enum is already in `Domain/`). The `PlaceOrder(Order)` signature stays the same.

**1.2.** A `DiscountCodeService` is available in the codebase. Add `IDiscountCodeService` as a dependency. It provides `CheckDiscountCode(customerId, discountCode)` — returns `true` if the code has **already** been used by this customer.

**1.3.** In `PlaceOrder`: if a discount code is present and **already used**, reject the order.

📝 Open `OrderServiceTest` → method `P3_OrderIsRejected_WhenDiscountCodeIsAlreadyUsed`.
The instructions and scenario are waiting for you in the comments.

---

### Step 2 — Apply the discount to the price

Objective: apply the discount to the price calculation.

```
GIVEN an order with 2 items at €55 each (subtotal: €110)
AND discount code SUMMER20 (-20%)
AND the code has not yet been used by this customer

WHEN the customer places the order

THEN payment is processed for €105.60
AND the order is confirmed
```

**2.1.** If the discount code is present and available, use `PricingService.CalculateTotal(items, discountCode)` instead of the existing call.

📝 Open `OrderServiceTest` → method `P3_OrderIsConfirmed_WhenDiscountCodeIsValid`.
The instructions and scenario are waiting for you in the comments.

---

### Step 3 — Mark the code as used after payment

Objective: after successful payment, mark the code as used. Update the Step 2 test with a new assertion:

```
GIVEN an order with 2 items at €55 each (subtotal: €110)
AND discount code SUMMER20 (-20%)
AND the code has not yet been used by this customer

WHEN the customer places the order

THEN payment is processed for €105.60
AND the order is confirmed
AND the discount code is marked as used   ← NEW
```

**3.1.** After successful payment, call `DiscountCodeService.MarkAsUsed(customerId, discountCode)`.

📝 Open `OrderServiceTest` → method `P3_DiscountCodeIsMarkedAsUsed_AfterSuccessfulPayment`.
The instructions are waiting for you in the comments.

---

### 💡 Tips

- Use `OrderBuilder` (in `Helper/`) to build test orders — it keeps the GIVEN block focused on what matters
- Use the existing given helper methods as inspiration for adding new ones

---

### 🤔 What is the "unit" here?

Same production code as Part 2, but different test design. Given helpers absorb API changes, and `PricingService` runs for real. The unit is still the class — but the boundary of what we substitute has shifted.

---

## ➡️ Next

Move on to **[Part 4](../Part4.Behavior/README.md)**
