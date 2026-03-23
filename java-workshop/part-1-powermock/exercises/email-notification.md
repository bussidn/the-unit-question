# Exercise: Implement email sending

**⏱️ Time: ~15 minutes** *(don't worry if you don't finish)*

---

We'd like the customer to receive a confirmation email after placing their order. The email address is provided by the customer when ordering. The email should contain the order number and the total amount. Obviously, if the order fails, no email should be sent.

---

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

