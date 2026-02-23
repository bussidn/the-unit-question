# Exercise: Express Shipping

## Context

The client wants to allow users to choose express shipping. The system must check if an order is eligible for express delivery (based on delivery zone).

## Your Mission

Implement an `ExpressShippingService` with a `checkEligibility(order)` method.

**Time: ~15 minutes**

---

## Use Cases to Implement

### 1. Eligible for express

| Input | Expected Output |
|-------|-----------------|
| Order with shipping address | `Eligible` |
| Postal code: "75001" (Paris) | |
| Zone is covered for express | |

**Acceptance criteria:**
- The order has a shipping address
- The postal code is in an express-covered zone
- Return `Eligible`

### 2. Zone not covered

| Input | Expected Output |
|-------|-----------------|
| Order with shipping address | `NotEligible("Zone not covered")` |
| Postal code: "99999" (remote area) | |
| Zone is NOT covered for express | |

**Acceptance criteria:**
- The order has a shipping address
- The postal code is NOT in an express-covered zone
- Return `NotEligible` with reason

---

## Hints

You will probably need:

- `ZoneRepository` — to check if a postal code is in an express zone
- Add `ShippingAddress` to the `Order` domain class

Look at existing tests (e.g., `StockServiceTest`) for mock patterns.

---

## Bonus (if you have time)

- Order too heavy (total weight > 10kg)
- Product not eligible for express (fragile items)
- No shipping address provided

