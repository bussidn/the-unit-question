# The Unit Question — Part 2

## 🏋️ Exercise: Discount Code

**⏱️ ~20-25 minutes**

We'd like to allow customers to apply a discount code to their order.

---

## 💡 What already exists

- `PricingService` already handles tax (20%) and shipping (5€ if subtotal < 100€)
- The [`DiscountCode`](src/main/java/domain/DiscountCode.java) enum lists available codes with their discount percentage
- [`PromoCodeService`](src/main/java/service/PromoCodeService.java) handles promo code validation — `checkPromoCode(customerId, discountCode)` returns `true` if the code is available for use, and `markAsUsed` records usage

---

## 🔧 What to implement

### In `PricingService`

Add an overload of `calculateTotal` that accepts a `DiscountCode` in addition to the list of items.

The discount applies **on the subtotal**, before tax and shipping are calculated.

### In `OrderService`

Update `createOrder` to accept an optional `DiscountCode` and:
1. **Check** via `PromoCodeService.checkPromoCode` whether the code is available — if `checkPromoCode` returns `true`, the code can be applied; otherwise, reject the order
2. **Register** the code usage (via `markAsUsed`) after the order is confirmed

`PromoCodeService` should be injected into `OrderService`.

---

## ✅ Tests to update

### `PricingServiceTest`

#### Scenario A — Discount applied, shipping added

```
GIVEN items worth 100€
AND a SUMMER20 code (20% off)

THEN the discounted subtotal is 80€
AND tax (20%) applies on 80€ → +16€
AND shipping applies (80€ < 100€ threshold) → +5€
AND the total is 101€
```

#### Scenario B — Discount applied, free shipping

```
GIVEN items worth 150€
AND a SUMMER20 code (20% off)

THEN the discounted subtotal is 120€
AND tax (20%) applies on 120€ → +24€
AND shipping is free (120€ ≥ 100€ threshold)
AND the total is 144€
```

---

### `OrderServiceTest`

#### Scenario C — Valid code, order accepted

```
GIVEN an order with a discount code
AND the customer has not used this code yet

WHEN the order is created

THEN the order is confirmed
AND the code is marked as used for this customer
```

#### Scenario D — Already used code, order rejected

```
GIVEN an order with a discount code
AND the customer has already used this code

WHEN the order is created

THEN the order is rejected with "Code already used"
AND no further step is executed (stock, payment, shipping)
```

---

## Your mission

1. Look at the existing code in `PricingService` and `OrderService`
2. Implement the feature
3. Update the tests in `PricingServiceTest` and `OrderServiceTest`
4. Run the tests

---

## ➡️ Next

Once done (or time's up), move on to **[Part 3](../part-3-mocks-clean/README.md)**

