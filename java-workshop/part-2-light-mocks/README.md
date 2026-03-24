# The Unit Question — Part 2

## 🏋️ Exercise: Choose your feature

**⏱️ ~20-25 minutes** *(don't worry if you don't finish)*

Choose **one** of the following features to implement:

---

### Option A: Promo Code

We'd like to allow customers to apply a promo code to their order. The system should validate the code and calculate the discount.

#### Scenario 1: Valid promo code

```
GIVEN an order of 100€
AND a promo code "SUMMER20" offering 20% off
AND the customer has never used this code

WHEN the customer applies the promo code

THEN the total becomes 80€
AND the code is marked as used for this customer
```

#### Scenario 2: Code already used

```
GIVEN an order of 100€
AND a promo code "ONCE50"
AND the customer has already used this code

WHEN the customer applies the promo code

THEN the system rejects with "Code already used"
AND the total remains unchanged
```

---

### Option B: Express Shipping

We'd like to allow customers to choose express shipping. The system should check if the order is eligible based on the delivery zone.

#### Scenario 1: Covered zone

```
GIVEN an order with a delivery address
AND postal code "75001" (Paris)
AND this zone is covered for express delivery

WHEN checking express eligibility

THEN the order is eligible
```

#### Scenario 2: Zone not covered

```
GIVEN an order with a delivery address
AND postal code "97500" (remote zone)
AND this zone is not covered for express delivery

WHEN checking express eligibility

THEN the order is not eligible
AND the reason is "Zone not covered"
```

---

### Option C: Order Cancellation

We'd like to allow customers to cancel an order. The system should handle the refund, release stock, and cancel shipping if needed.

#### Scenario 1: Cancellation before shipping

```
GIVEN a confirmed order
AND a payment was made (transaction ID present)
AND not yet shipped (no tracking number)

WHEN the customer cancels the order

THEN the payment is refunded
AND the stock is released
AND the status becomes CANCELLED
```

#### Scenario 2: Cancellation with shipping in progress

```
GIVEN a confirmed order
AND a payment was made
AND a shipment was created (tracking number present)

WHEN the customer cancels the order

THEN the payment is refunded
AND the stock is released
AND the shipment is cancelled
AND the status becomes CANCELLED
```

---

## Your mission

1. Choose a feature (A, B, or C)
2. Look at the existing code in `src/main/java/service/`
3. Implement the feature with its tests
4. Run the tests

## 🎯 The point

> **The goal is NOT to finish.**
>
> The goal is to **feel** what happens when you add a feature to this codebase.

---

## ➡️ Next

Once done (or time's up), move on to **[Part 3](../part-3-mocks-clean/README.md)**

