# The Unit Question — Part 1: Method isolation

> **What is a "unit" in a unit test?**

## 🎯 About this workshop

Everyone talks about "unit tests", but nobody agrees on what a "unit" is.

- Is it a method? A class? A module?
- Should we mock everything? Nothing? Something in between?
- Why do some test suites break with every refactoring while others survive major changes?

**This workshop makes you experience different testing approaches** through hands-on exercises. You'll feel the friction of each strategy and discover which one works best for you.

4 parts, each with a different approach. You'll implement features and experience firsthand the pros and cons of each strategy.

---

## 🛠️ Before you start

### Prerequisites

| Tool | Version | Check |
|------|---------|-------|
| **.NET SDK** | 8+ | `dotnet --version` |
| **Git** | any | `git --version` |

NSubstitute and xUnit are included (restored automatically).

### Setup

```bash
cd the-unit-question/csharp-workshop
dotnet test Part1.StaticDeps.Tests/
```

You should see all tests passing (or skipped). If so, you're ready!

💡 Open `Part1.StaticDeps/` as a **separate IDE project** to avoid editing the wrong `OrderService`.

---

## 🏋️ Exercise (~14 min)

`OrderService.PlaceOrder` does not support discount codes yet. Your job is to understand what tests you *would* write for this feature — and why they're hard to write in this architecture.

Open `OrderServiceTest.cs` — the exercise methods are waiting for you with instructions.

> ⚠️ **C# note**: In this part, `OrderService` instantiates all its dependencies inline (`new StockService()`, `new PaymentService()`…). In Java, Mockito's `mockConstruction()` can intercept these. In C#, NSubstitute has no equivalent — that requires a commercial tool (JustMock, Typemock). The exercise tests are provided as stubs to illustrate what you *would* implement.

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

**1.2.** A `DiscountCodeService` is available in the codebase. Wire it as a new dependency (instantiated inline, like the others). It provides `CheckDiscountCode(customerId, discountCode)` — returns `true` if the code has **already** been used by this customer.

**1.3.** In `PlaceOrder`: if a discount code is present and **already used**, reject the order.

📝 Open `OrderServiceTest` → method `P1_OrderIsRejected_WhenDiscountCodeIsAlreadyUsed`.

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

📝 Open `OrderServiceTest` → method `P1_OrderIsConfirmed_WhenDiscountCodeIsValid`.

---

### Step 3 — Mark the code as used after payment

Objective: after successful payment, mark the code as used.

**3.1.** After successful payment, call `DiscountCodeService.MarkAsUsed(customerId, discountCode)`.

📝 Open `OrderServiceTest` → method `P1_DiscountCodeIsMarkedAsUsed_AfterSuccessfulPayment`.

---

### 🎯 The point

> **The goal is NOT to finish.**
>
> The goal is to **feel** what happens when you try to add a feature to this codebase.

---

### 🤔 What is the "unit" here?

In this part, the unit is **a single method**. Every dependency is instantiated inline — no injection, no interfaces. Isolating `PlaceOrder` from its collaborators requires intercepting constructors, which is trivial in Java (Mockito `mockConstruction`) but needs a commercial tool in C# (JustMock, Typemock).

The existing tests for `IsValid` work because that method is `internal virtual` — NSubstitute can create a partial substitute to test it in isolation.

---

## ➡️ Next

Once done (or time's up), move on to **[Part 2](../Part2.LightMocks/README.md)**
