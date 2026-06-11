# The Unit Question — Part 2: Light mocks

## 🔄 What changed since Part 1

Dependencies are now **injected via constructor** instead of instantiated inline. Tests can use NSubstitute directly and pass substitutes straight into the constructor — no more commercial tools required.

`IsValid()` also runs for real now — no more internal/virtual workarounds.

The production logic is the same as Part 1; only the wiring changed. Have a look at the existing `OrderServiceTest` (`Part2.LightMocks.Tests/Service/`) before you start.

---

## 🛠️ Setup

```bash
cd the-unit-question/csharp-workshop
dotnet test Part2.LightMocks.Tests/
```

💡 Open `Part2.LightMocks/` as a **separate IDE project** to avoid editing the wrong `OrderService`.

---

## 🪂 Fast track — production code

You'll redo the Part 1 production changes here (it's quick). **Want to focus on the tests?** Merge the reference branch to fast-track the production code:

```bash
git merge --no-edit origin/part-2-prod
```

---

## 🏋️ Exercise (~15 min)

`OrderService.PlaceOrder` does not support discount codes yet. Your job is to add this feature step by step, writing tests as you go.

---

### Step 1 — Reject already-used discount codes

Objective: reject orders when the discount code has already been used.

```
GIVEN an order with discount code SUMMER20
AND this code has already been used by this customer

WHEN the customer places the order

THEN the order is rejected with reason "Discount code already used"
AND no payment is triggered
```

**1.1.** Add a `discountCode` field to the `Order` record (the `DiscountCode` enum is already in `Domain/`). The `PlaceOrder(Order)` signature stays the same.

**1.2.** A `DiscountCodeService` is available in the codebase. Add `IDiscountCodeService` as a constructor dependency. It provides `CheckDiscountCode(customerId, discountCode)` — returns `true` if the code has **already** been used by this customer.

**1.3.** In `PlaceOrder`: if a discount code is present and **already used**, reject the order.

📝 Open `OrderServiceTest` → method `P2_OrderIsRejected_WhenDiscountCodeIsAlreadyUsed`.
The instructions and scenario are waiting for you in the comments.

---

### Step 2 — Apply the discount to the price

Objective: apply the discount to the price when the code is valid.

```
GIVEN an order with 2 items at €55 each (subtotal: €110)
AND discount code SUMMER20 (-20%)
AND the code has not yet been used by this customer

WHEN the customer places the order

THEN payment is processed for €105.60
AND the order is confirmed
```

**2.1.** If the discount code is present and available, use `PricingService.CalculateTotal(items, discountCode)` instead of the existing call.

📝 Open `OrderServiceTest` → method `P2_OrderIsConfirmed_WhenDiscountCodeIsValid`.
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

📝 Open `OrderServiceTest` → method `P2_DiscountCodeIsMarkedAsUsed_AfterSuccessfulPayment`.
The instructions are waiting for you in the comments.

---

### 🤔 What is the "unit" here?

In this part, the unit is **the class**. `OrderService` runs entirely for real, and every external dependency is substituted. We no longer need to stub internal methods like `IsValid`.

---

## ➡️ Next

Move on to **[Part 3](../Part3.MocksClean/README.md)**
