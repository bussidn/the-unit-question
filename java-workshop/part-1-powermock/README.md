# The Unit Question — Part 1

> **What is a "unit" in unit test?**

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

## 🏋️ Exercise: Discount codes in order creation

**⏱️ ~25 minutes**

A `DiscountCodeService` has been added to the codebase. It exposes two methods:

- **`checkDiscountCode(customerId, discountCode)`** — returns `true` if the code is available for this customer
- **`markAsUsed(customerId, discountCode)`** — marks the code as used for this customer

`PricingService` has also been updated with a new overload: `calculateTotal(items, discountCode)` that applies the discount to the price.

`OrderService` has not been migrated yet. That's your mission.

---

### 🎯 Your mission

`OrderService.placeOrder` needs to support discount codes.

- Add `DiscountCodeService` as a dependency (instantiate it inside `placeOrder`, like the other services)
- Add a `discountCode` field to the `Order` record — it should be nullable (the `DiscountCode` enum is already provided in `domain/`). The `placeOrder(Order)` signature stays the same
- If a discount code is provided: use `DiscountCodeService.checkDiscountCode` — if it returns `true`, the code is available and can be applied; otherwise reject. Calculate the price with the discount, mark as used after payment
- Add your new test scenarios to the existing `OrderServiceTest` — follow the style already in place

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
- To mock a constructor in your tests, use:
  ```java
  MockedConstruction<DiscountCodeService> mocked = mockConstruction(DiscountCodeService.class, (mock, ctx) -> {
      when(mock.checkDiscountCode(any(), any())).thenReturn(false);
  });
  ```
- To mock `Database` static methods:
  ```java
  MockedStatic<Database> mockedDb = mockStatic(Database.class);
  ```

### 🎯 The point

> **The goal is NOT to finish.**
>
> The goal is to **feel** what happens when you add a feature to this codebase.

---

## ➡️ Next

Once done (or time's up), move on to **[Part 2](../part-2-light-mocks/README.md)**

