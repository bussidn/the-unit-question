# 🐛 Bug Report #4242

**Sévérité :** Critique  
**Statut :** Ouvert  
**Rapporté par :** Équipe QA  
**Date :** 2024-03-15

---

## Résumé

Un client a été débité mais jamais remboursé, et son produit a été vendu à un autre client.

## Étapes pour reproduire

1. Client A commande 1x "Montre Édition Limitée" (1 seule en stock)
2. Paiement de Client A réussi → `transactionId = TXN-001`
3. Client A demande l'annulation de sa commande
4. **Passerelle de paiement temporairement indisponible** → le remboursement échoue
5. Client A reçoit l'erreur : "Refund failed"

## Résultat attendu

- Le stock devrait rester réservé pour Client A
- Client A devrait pouvoir réessayer l'annulation plus tard
- Le produit ne devrait PAS être disponible pour d'autres clients

## Résultat constaté

- Le stock a été libéré **AVANT** que le remboursement ne soit tenté
- Client B a acheté la même montre (elle était disponible !)
- Client B a reçu le produit
- Client A n'a jamais été remboursé
- **Client A a payé 500€ et n'a rien**

## Impact

- Client A a perdu 500€
- Client B a reçu un produit techniquement vendu deux fois
- L'équipe juridique est impliquée

---

## Notes d'investigation

> "Tous les tests unitaires passent. Comment ce bug a-t-il pu passer ?"

Regardez l'ordre des opérations dans `OrderCancellationService.cancelOrder()`.

**Question :** Que se passe-t-il quand `refundPayment()` échoue APRÈS que `releaseStock()` a été appelé ?

---

## Cause racine

*À remplir après investigation*

