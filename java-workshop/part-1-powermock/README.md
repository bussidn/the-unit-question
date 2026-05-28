# The Unit Question — Part 1

> **What is a "unit" in unit test?**

## 🎯 About this workshop

Everyone talks about "unit tests", but nobody agrees on what a "unit" is.

- Is it a method? A class? A module?
- Should we mock everything? Nothing? Something in between?
- Why do some test suites break with every refactoring while others survive major changes?

**This workshj'aop makes you experience different testing approaches** through hands-on exercises. You'll feel the friction of each strategy and discover which one works best for you.

4 parts, each with a different approach. You'll implement features and experience firsthand the pros and cons of each strategy.

---

## 🛠️ Before you start

### Prerequisites

| Tool | Version | Check |
|------|---------|-------|
| **Java (JDK)** | 21+ | `java -version` |
| **Git** | any | `git --version` |

Gradle, JUnit and Mockito are included (downloaded automatically).

### Setup

```bash
git clone https://github.com/bussidn/the-unit-question.git
cd the-unit-question/java-workshop/part-1-powermock
./gradlew test
```

You should see `BUILD SUCCESSFUL`. If so, you're ready!

> ⚠️ **Windows**: use `gradlew.bat test`

---

## 🏋️ Exercise (~15 min)

`OrderService.placeOrder` does not support discount codes yet. Your job is to add this feature step by step, writing tests as you go.

Look at the existing `OrderServiceTest` for style and follow the same patterns.

Open `OrderServiceTest.java` — the exercise starts right after the class Javadoc.

> 💡 To mock a constructor in your tests, use:
> ```java
> MockedConstruction<SomeService> mocked = mockConstruction(SomeService.class, (mock, ctx) -> {
>     when(mock.someMethod(any())).thenReturn(...);
> });
> ```

---

### Step 1 — Reject already-used discount codes

**1.1.** Add a nullable `discountCode` field to the `Order` record (the `DiscountCode` enum is already in `domain/`). The `placeOrder(Order)` signature stays the same.

**1.2.** A `DiscountCodeService` is available in the codebase. Wire it as a new dependency. It provides `checkDiscountCode(customerId, discountCode)` — returns `true` if the code is available for this customer.

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

### 🎯 The point

> **The goal is NOT to finish.**
>
> The goal is to **feel** what happens when you add a feature to this codebase.

---

### 🤔 What is the "unit" here?

In this part, the unit is **a single method**. We mock other classes, static methods, and even other methods of the same class (`whenPrivate` on `isValid`). Each method is tested in complete isolation.

---

## ➡️ Next

Once done (or time's up), move on to **[Part 2](../part-2-light-mocks/README.md)**

