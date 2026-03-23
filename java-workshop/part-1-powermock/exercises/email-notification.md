# Exercise: Email Notification

## Context

The business wants to send an email confirmation when an order is successfully created.

## Your mission

Add a `NotificationService` that sends an email after a successful order.

**⏱️ Time: ~15 minutes** *(don't worry if you don't finish)*

---

## Requirements

### 1. Create `NotificationService`

```java
public class NotificationService {
    public void sendOrderConfirmation(String customerId, String orderId, double totalPrice) {
        // Sends an email (implementation doesn't matter)
        EmailGateway.send(customerId, "Order " + orderId + " confirmed: " + totalPrice + "€");
    }
}
```

### 2. Create `EmailGateway` (static)

```java
public class EmailGateway {
    public static void send(String to, String message) {
        // External email service
        System.out.println("EMAIL to " + to + ": " + message);
    }
}
```

### 3. Modify `OrderService`

Call the notification service **after** a successful order:

```java
public OrderResult createOrder(Order order) {
    NotificationService notificationService = new NotificationService();
    // ... existing code ...
    
    // After Database.saveOrder(), before return:
    notificationService.sendOrderConfirmation(
        order.customerId(),
        confirmedOrder.id(),
        totalPrice
    );
    
    return new OrderResult.Success(confirmedOrder, shippingConfirmation);
}
```

---

## What you'll experience

1. **Run the tests** → They all fail 💥
2. **Try to fix them** → Each test needs to mock the new constructor
3. **Feel the pain** → This is what "extreme isolation" means

---

## 🎯 The point

> **The goal is NOT to finish.**
> 
> The goal is to **feel** how painful this testing strategy is when the code evolves.

Every test is coupled to the implementation. Add one `new`, break everything.

---

## Hints (if you're stuck)

To mock the new constructor:

```java
MockedConstruction<NotificationService> mockedNotification = mockConstruction(NotificationService.class);
```

To mock the static `EmailGateway`:

```java
MockedStatic<EmailGateway> mockedEmail = mockStatic(EmailGateway.class);
```

Don't forget to add them to the try-with-resources block!

