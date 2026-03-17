# Bug report: cancellations fail when no refund is needed

## Summary

Customers cannot cancel some paid orders when the payment layer says there is nothing left to refund.

## Affected cases

- Orders paid with a 100% promo code or zero effective amount
- Orders that were already refunded earlier

## Actual behavior

The cancellation flow returns `Refund failed` and stops.

## Expected behavior

Cancellation should continue when `PaymentService.refundPayment()` returns `false` because `false` means **nothing to refund**, not **refund failed**.

## Root cause

`PaymentService` uses this contract:

- `true` = money refunded
- `false` = nothing to refund

`OrderCancellationService` interprets the same `false` value as an error and aborts the cancellation.

## Impact

The unit tests pass in isolation, but the integration between the two services is broken.