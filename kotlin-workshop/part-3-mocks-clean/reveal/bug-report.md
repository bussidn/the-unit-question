# 🐛 Bug Report #4242

**Severity:** Critical  
**Status:** Open  
**Reported by:** QA Team  
**Date:** 2024-03-15

---

## Summary

A customer was charged but never refunded, and their product was sold to another customer.

## Steps to Reproduce

1. Customer A orders 1x "Limited Edition Watch" (only 1 in stock)
2. Customer A's payment succeeds → `transactionId = TXN-001`
3. Customer A requests order cancellation
4. **Payment gateway is temporarily down** → refund fails
5. Customer A receives error: "Refund failed"

## Expected Result

- Stock should remain reserved for Customer A
- Customer A should be able to retry cancellation later
- The product should NOT be available for other customers

## Actual Result

- Stock was released back to inventory **BEFORE** the refund was attempted
- Customer B purchased the same watch (it was available!)
- Customer B received the product
- Customer A was never refunded
- **Customer A paid €500 and has nothing**

## Impact

- Customer A lost €500
- Customer B received a product that was technically sold twice
- Legal/compliance team involved

---

## Investigation Notes

> "All unit tests pass. How did this bug get through?"

Look at the order of operations in `OrderCancellationService.cancelOrder()`.

**Question:** What happens when `refundPayment()` fails AFTER `releaseStock()` was called?

---

## Root Cause

*To be filled after investigation*

