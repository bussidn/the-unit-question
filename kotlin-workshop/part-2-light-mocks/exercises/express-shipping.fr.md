# Exercice : Livraison Express

## Contexte

Le client souhaite permettre aux utilisateurs de choisir la livraison express. Le système doit vérifier si une commande est éligible à la livraison express (selon la zone de livraison).

## Votre Mission

Implémenter un `ExpressShippingService` avec une méthode `checkEligibility(order)`.

**Temps : ~15 minutes**

---

## Cas d'usage à implémenter

### 1. Éligible à l'express

| Entrée | Sortie attendue |
|--------|-----------------|
| Commande avec adresse de livraison | `Eligible` |
| Code postal : "75001" (Paris) | |
| Zone couverte pour l'express | |

**Critères d'acceptation :**
- La commande a une adresse de livraison
- Le code postal est dans une zone couverte par l'express
- Retourner `Eligible`

### 2. Zone non couverte

| Entrée | Sortie attendue |
|--------|-----------------|
| Commande avec adresse de livraison | `NotEligible("Zone not covered")` |
| Code postal : "99999" (zone isolée) | |
| Zone NON couverte pour l'express | |

**Critères d'acceptation :**
- La commande a une adresse de livraison
- Le code postal N'EST PAS dans une zone couverte par l'express
- Retourner `NotEligible` avec la raison

---

## Indices

Vous aurez probablement besoin de :

- `ZoneRepository` — pour vérifier si un code postal est en zone express
- Ajouter `ShippingAddress` à la classe de domaine `Order`

Regardez les tests existants (ex: `StockServiceTest`) pour les patterns de mock.

---

## Bonus (si vous avez le temps)

- Commande trop lourde (poids total > 10kg)
- Produit non éligible à l'express (articles fragiles)
- Pas d'adresse de livraison fournie

