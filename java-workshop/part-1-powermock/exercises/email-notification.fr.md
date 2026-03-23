# Exercice : Notification Email

## Contexte

Le métier souhaite envoyer un email de confirmation quand une commande est créée avec succès.

## Votre mission

Ajouter un `NotificationService` qui envoie un email après une commande réussie.

**⏱️ Temps : ~15 minutes** *(ne vous inquiétez pas si vous ne finissez pas)*

---

## Spécifications

### 1. Créer `NotificationService`

```java
public class NotificationService {
    public void sendOrderConfirmation(String customerId, String orderId, double totalPrice) {
        // Envoie un email (l'implémentation n'a pas d'importance)
        EmailGateway.send(customerId, "Order " + orderId + " confirmed: " + totalPrice + "€");
    }
}
```

### 2. Créer `EmailGateway` (statique)

```java
public class EmailGateway {
    public static void send(String to, String message) {
        // Service email externe
        System.out.println("EMAIL to " + to + ": " + message);
    }
}
```

### 3. Modifier `OrderService`

Appeler le service de notification **après** une commande réussie :

```java
public OrderResult createOrder(Order order) {
    NotificationService notificationService = new NotificationService();
    // ... code existant ...
    
    // Après Database.saveOrder(), avant le return :
    notificationService.sendOrderConfirmation(
        order.customerId(),
        confirmedOrder.id(),
        totalPrice
    );
    
    return new OrderResult.Success(confirmedOrder, shippingConfirmation);
}
```

---

## Ce que vous allez vivre

1. **Lancez les tests** → Ils échouent tous 💥
2. **Essayez de les corriger** → Chaque test doit mocker le nouveau constructeur
3. **Ressentez la douleur** → C'est ça "l'isolation extrême"

---

## 🎯 Le but

> **L'objectif N'EST PAS de finir.**
> 
> L'objectif est de **ressentir** à quel point cette stratégie de test est pénible quand le code évolue.

Chaque test est couplé à l'implémentation. Ajoutez un `new`, tout casse.

---

## Indices (si vous êtes bloqués)

Pour mocker le nouveau constructeur :

```java
MockedConstruction<NotificationService> mockedNotification = mockConstruction(NotificationService.class);
```

Pour mocker le `EmailGateway` statique :

```java
MockedStatic<EmailGateway> mockedEmail = mockStatic(EmailGateway.class);
```

N'oubliez pas de les ajouter au bloc try-with-resources !

