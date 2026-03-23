# Exercise: Email Notification

## The feature

> *"When an order is successfully created, send a confirmation email to the customer."*

**⏱️ Time: ~15 minutes** *(don't worry if you don't finish)*

---

## Scenarios

### Scenario 1: Successful order → email sent

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

### Scenario 2: Payment declined → no email

```
GIVEN a customer with email "bob@example.com"
AND a cart with 1 item
AND stock is available
AND payment is declined

WHEN the customer places the order

THEN the order fails
AND no email is sent
```

---

## Your mission

1. Look at the existing code in `OrderService.java`
2. Implement the feature however you see fit
3. Run the tests

---

## 🎯 The point

> **The goal is NOT to finish.**
>
> The goal is to **feel** what happens when you add a feature to this codebase.

