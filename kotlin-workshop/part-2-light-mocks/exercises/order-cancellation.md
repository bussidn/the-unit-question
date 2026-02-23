# Exercise: Order Cancellation

## Context

The client wants to allow users to cancel an order. The system must handle refund, release reserved stock, and cancel shipment if one was created.

## Your Mission

Implement an `OrderCancellationService` with a `cancelOrder(orderId)` method.

**Time: ~25-30 minutes**

---

## Use Cases to Implement

### 1. Successful cancellation (before shipping)

| Input | Expected Output |
|-------|-----------------|
| Order status: CONFIRMED | `Success` |
| Has transaction ID (payment done) | |
| No tracking number (not shipped yet) | |

**Acceptance criteria:**
- Find the order by ID
- Refund the payment
- Release reserved stock
- Update order status to CANCELLED
- Do NOT call shipping cancellation (no tracking number)

**Expected call sequence:**
1. `orderRepository.findById()`
2. `paymentService.refundPayment()`
3. `stockService.releaseStock()`
4. `orderRepository.updateStatus(CANCELLED)`

### 2. Cancellation with shipment in progress

| Input | Expected Output |
|-------|-----------------|
| Order status: CONFIRMED | `Success` |
| Has transaction ID (payment done) | |
| Has tracking number (shipment created) | |

**Acceptance criteria:**
- Find the order by ID
- Refund the payment
- Release reserved stock
- Cancel the shipment
- Update order status to CANCELLED

**Expected call sequence:**
1. `orderRepository.findById()`
2. `paymentService.refundPayment()`
3. `stockService.releaseStock()`
4. `shippingService.cancelShipment()`
5. `orderRepository.updateStatus(CANCELLED)`

---

## Hints

You will need:

- `OrderRepository` — to find orders and update status
- Use existing services: `PaymentService`, `StockService`, `ShippingService`
- Add `transactionId` and `trackingNumber` to `Order` domain class

Look at `OrderServiceTest` for patterns with multiple mocked services.

---

## Bonus (if you have time)

- Order already shipped → return `Failure("Order already shipped")`
- Order already cancelled → return `Failure("Order already cancelled")`
- Refund fails → return `Failure("Refund failed")` and do NOT release stock

