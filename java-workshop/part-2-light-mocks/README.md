# The Unit Question — Part 2

## 🏋️ Exercise: Discount Code

**⏱️ ~20-25 minutes** *(don't worry if you don't finish)*

We'd like to allow customers to apply a discount code to their order. The system should validate the code and calculate the discount.

#### Scenario 1: Valid discount code

```
GIVEN an order of 100€
AND a discount code "SUMMER20" offering 20% off
AND the customer has never used this code

WHEN the customer applies the discount code

THEN the total becomes 80€
AND the code is marked as used for this customer
```

#### Scenario 2: Code already used

```
GIVEN an order of 100€
AND a discount code "ONCE50"
AND the customer has already used this code

WHEN the customer applies the discount code

THEN the system rejects with "Code already used"
AND the total remains unchanged
```

---

## 💡 Design hints

- The discount code that is being tried can be stored directly in the order (`Order`)
- The discount computation can be delegated to the `PricingService`

---

## Your mission

1. Look at the existing code in `src/main/java/service/`
2. Implement the feature with its tests
3. Run the tests

## 🎯 The point

> **The goal is NOT to finish.**
>
> The goal is to **feel** what happens when you add a feature to this codebase.

---

## ➡️ Next

Once done (or time's up), move on to **[Part 3](../part-3-mocks-clean/README.md)**

