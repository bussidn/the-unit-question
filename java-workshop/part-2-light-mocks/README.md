# The Unit Question — Part 2

## 🏋️ Exercise: Discount Code

**⏱️ ~20-25 minutes**

We'd like to allow customers to apply a discount code to their order. The system should validate the code and calculate the discount.
The discount applies to the price **before** tax and shipping are calculated.

#### Scenario 1: Valid discount code

```
GIVEN an order with items worth 100€
AND a discount code "SUMMER20" offering 20% off
AND the customer has never used this code

WHEN the customer applies the discount code

THEN the discount is applied to the subtotal before tax and shipping: 80€ (100€ - 20%)
AND taxes (20%) are computed on that subtotal: +16€
AND shipping is added (80€ < 100€ free-shipping threshold): +5€
AND the total becomes 101€
AND the code is marked as used for this customer
```

#### Scenario 2: Code already used

```
GIVEN an order with items worth 100€
AND a discount code "ONCE50"
AND the customer has already used this code

WHEN the customer applies the discount code

THEN the system rejects with "Code already used"
AND the total remains unchanged at 120€ (100€ + 20% tax, free shipping)
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

---

## ➡️ Next

Once done (or time's up), move on to **[Part 3](../part-3-mocks-clean/README.md)**

