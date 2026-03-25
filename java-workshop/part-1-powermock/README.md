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

## 🏋️ Exercise: Implement email sending

**⏱️ ~15 minutes** *(don't worry if you don't finish)*

We'd like the customer to receive a confirmation email after placing their order.

**Currently, the `Order` object does not contain the customer's email address.** You'll need to add it to send the confirmation.

The email should contain the order identifier (`id`) and the total amount. If the order fails, no email should be sent.

### Scenario 1: Successful order

```
GIVEN a customer with email "alice@example.com"
AND a cart with 2 items totaling 49.99€
AND stock is available
AND payment is accepted

WHEN the customer places the order

THEN the order is created successfully
AND an email is sent to "alice@example.com"
AND the email contains "Order ORDER-123 confirmed: 49.99€"
```

### Scenario 2: Payment declined

```
GIVEN a customer with email "bob@example.com"
AND a cart with 1 item
AND stock is available
AND payment is declined

WHEN the customer places the order

THEN the order fails
AND no email is sent
```

### Your mission

1. Look at the existing code in `src/main/java/service/OrderService.java`
2. Implement the feature however you see fit
3. Run the tests

### 🎯 The point

> **The goal is NOT to finish.**
>
> The goal is to **feel** what happens when you add a feature to this codebase.

---

## ➡️ Next

Once done (or time's up), move on to **[Part 2](../part-2-light-mocks/README.md)**

