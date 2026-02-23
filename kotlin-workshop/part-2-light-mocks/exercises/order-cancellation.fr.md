# Exercice : Annulation de Commande

## Contexte

Le client souhaite permettre aux utilisateurs d'annuler une commande. Le système doit gérer le remboursement, libérer le stock réservé, et annuler l'expédition si elle a été créée.

## Votre Mission

Implémenter un `OrderCancellationService` avec une méthode `cancelOrder(orderId)`.

**Temps : ~25-30 minutes**

---

## Cas d'usage à implémenter

### 1. Annulation réussie (avant expédition)

| Entrée | Sortie attendue |
|--------|-----------------|
| Statut commande : CONFIRMED | `Success` |
| A un transaction ID (paiement effectué) | |
| Pas de tracking number (pas encore expédiée) | |

**Critères d'acceptation :**
- Trouver la commande par son ID
- Rembourser le paiement
- Libérer le stock réservé
- Mettre à jour le statut en CANCELLED
- NE PAS appeler l'annulation d'expédition (pas de tracking number)

**Séquence d'appels attendue :**
1. `orderRepository.findById()`
2. `paymentService.refundPayment()`
3. `stockService.releaseStock()`
4. `orderRepository.updateStatus(CANCELLED)`

### 2. Annulation avec expédition en cours

| Entrée | Sortie attendue |
|--------|-----------------|
| Statut commande : CONFIRMED | `Success` |
| A un transaction ID (paiement effectué) | |
| A un tracking number (expédition créée) | |

**Critères d'acceptation :**
- Trouver la commande par son ID
- Rembourser le paiement
- Libérer le stock réservé
- Annuler l'expédition
- Mettre à jour le statut en CANCELLED

**Séquence d'appels attendue :**
1. `orderRepository.findById()`
2. `paymentService.refundPayment()`
3. `stockService.releaseStock()`
4. `shippingService.cancelShipment()`
5. `orderRepository.updateStatus(CANCELLED)`

---

## Indices

Vous aurez besoin de :

- `OrderRepository` — pour trouver les commandes et mettre à jour le statut
- Utiliser les services existants : `PaymentService`, `StockService`, `ShippingService`
- Ajouter `transactionId` et `trackingNumber` à la classe `Order`

Regardez `OrderServiceTest` pour les patterns avec plusieurs services mockés.

---

## Bonus (si vous avez le temps)

- Commande déjà expédiée → retourner `Failure("Order already shipped")`
- Commande déjà annulée → retourner `Failure("Order already cancelled")`
- Remboursement échoue → retourner `Failure("Refund failed")` et NE PAS libérer le stock

