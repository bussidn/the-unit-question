# Rapport de bug : l'annulation échoue quand aucun remboursement n'est nécessaire

## Résumé

Certains clients ne peuvent pas annuler leur commande quand la couche paiement indique qu'il n'y a rien à rembourser.

## Cas concernés

- Commandes payées avec un code promo de 100 % ou un montant effectif à zéro
- Commandes déjà remboursées auparavant

## Comportement observé

Le flux d'annulation renvoie `Refund failed` et s'arrête.

## Comportement attendu

L'annulation devrait continuer quand `PaymentService.refundPayment()` renvoie `false`, car `false` signifie **rien à rembourser**, et non **échec du remboursement**.

## Cause racine

`PaymentService` utilise ce contrat :

- `true` = argent remboursé
- `false` = rien à rembourser

`OrderCancellationService` interprète cette même valeur `false` comme une erreur et annule tout le processus.

## Impact

Les tests unitaires passent isolément, mais l'intégration entre les deux services est cassée.