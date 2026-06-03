# The Unit Question — Part 3: Clean mocks

## 🔄 What changed since Part 2

Same production code, same dependencies. The change is in **test design**:

- `@ExtendWith(MockitoExtension.class)` and `@Mock` annotations replace manual `mock()` calls.
- An `OrderBuilder` (in `helper/`) makes test data more readable.
- Recurring setup is factored into **given helpers** (`givenReservationSucceedsFor`, `givenPaymentSucceedsFor`, etc.).
- `PricingService` is no longer mocked — it's pure computation, so tests assert on real calculated values.

Have a look at the existing `OrderServiceTest` (`src/test/java/service/`) before you start.

---

## 🛠️ Setup

```bash
cd java-workshop/part-3-mocks-clean
./gradlew test
```

💡 Open this part as a **separate IDE project** to avoid editing the wrong `OrderService`.

---

## 🪂 Safety net — production code

The production code is identical to Part 2. **If you fall behind**, grab the reference commit and jump straight to the tests:

```bash
git cherry-pick <SHA>  # ask the facilitator for the SHA
```

---

## 🏋️ Exercise (~13 min)

`OrderService.placeOrder` does not support discount codes yet. Your job is to add this feature step by step, writing tests as you go.

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

**1.1.** Add a `discountCode` field to the `Order` record (the `DiscountCode` enum is already in `domain/`). The `placeOrder(Order)` signature stays the same.

**1.2.** A `DiscountCodeService` is available in the codebase. Add it as a dependency. It provides `checkDiscountCode(customerId, discountCode)` — returns `true` if the code is available for this customer.

**1.3.** In `placeOrder`: if a discount code is present and **not available**, reject the order.

📝 Open `OrderServiceTest` → method `p3_orderIsRejected_whenDiscountCodeIsAlreadyUsed`.
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

**2.1.** If the discount code is present and available, use `PricingService.calculateTotal(items, discountCode)` instead of the existing call.

📝 Open `OrderServiceTest` → method `p3_orderIsConfirmed_whenDiscountCodeIsValid`.
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

**3.1.** After successful payment, call `DiscountCodeService.markAsUsed(customerId, discountCode)`.

📝 Open `OrderServiceTest` → method `p3_discountCodeIsMarkedAsUsed_afterSuccessfulPayment`.
The instructions are waiting for you in the comments.

---

### 💡 Tips

- Use `OrderBuilder` (in `helper/`) to build test orders — it keeps the GIVEN block focused on what matters

---

### 🤔 What is the "unit" here?

Same production code as Part 2, but different test design. Given helpers absorb API changes, and `PricingService` runs for real. The unit is still the class — but the boundary of what we mock has shifted.

---

## ➡️ Next

Move on to **[Part 4](../part-4-behavior/README.md)**

