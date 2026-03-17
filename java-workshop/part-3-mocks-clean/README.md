# Java Workshop – Part 3: Mocks Clean with THE TRAP

This exercise demonstrates a subtle mock-based testing problem: both unit tests pass, but the real integration is broken because the contract between two services does not match.

## The trap

`PaymentService.refundPayment()` means:

- `true` → money was refunded
- `false` → nothing to refund

`OrderCancellationService` assumes:

- `true` → success
- `false` → refund failed

Both sides are tested. Both tests pass. The system is still wrong.

## Goal

Read the tests and implementation, then explain:

1. Why the tests pass
2. Why the production behavior is wrong
3. How you would redesign the contract to avoid this trap

## Files to inspect

- `src/main/java/service/GatewayPaymentService.java`
- `src/main/java/service/OrderCancellationService.java`
- `src/test/java/service/PaymentServiceTest.java`
- `src/test/java/service/OrderCancellationServiceTest.java`

## Run the tests

```bash
./gradlew test
```

## Discussion prompts

- Should `boolean` be replaced with a richer result type?
- Should “nothing to refund” be considered success?
- Should failures be represented by an exception or an explicit domain result?

If you want the answer, see `reveal/bug-report.md`.