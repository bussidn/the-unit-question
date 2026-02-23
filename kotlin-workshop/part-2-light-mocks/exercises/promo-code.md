# Exercise: Promo Code

## Context

The client wants to allow users to apply a promo code to their order. The system must validate the code and calculate the discount.

## Your Mission

Implement a `PromoCodeService` with an `applyPromoCode(order, code)` method.

**Time: ~20 minutes**

---

## Use Cases to Implement

### 1. Valid promo code (percentage discount)

| Input | Expected Output |
|-------|-----------------|
| Order: 100€ total | `Success(discountedTotal = 80.0)` |
| Code: "SUMMER20" (20% off) | |

**Acceptance criteria:**
- The code exists in the database
- The customer has never used this code before
- The discounted total is calculated correctly
- The code is marked as used for this customer

### 2. Code already used by customer

| Input | Expected Output |
|-------|-----------------|
| Order: any amount | `Failure("Code already used")` |
| Code: "ONCE50" | |
| Customer has already used "ONCE50" | |

**Acceptance criteria:**
- The code exists
- But the customer has already used it
- Return failure without applying discount
- Do NOT mark as used again

---

## Hints

You will probably need:

- `PromoCodeRepository` — to find promo code details
- `CustomerPromoUsageRepository` — to check/track usage per customer

Look at existing tests (e.g., `PaymentServiceTest`) for mock patterns.

---

## Bonus (if you have time)

- Code expired (expiration date in the past)
- Code does not exist
- Minimum order amount not reached
- Fixed amount discount (€ instead of %)

