# Exercice : Code Promo

## Contexte

Le client souhaite permettre aux utilisateurs d'appliquer un code promo à leur commande. Le système doit valider le code et calculer la réduction.

## Votre Mission

Implémenter un `PromoCodeService` avec une méthode `applyPromoCode(order, code)`.

**Temps : ~20 minutes**

---

## Cas d'usage à implémenter

### 1. Code promo valide (réduction en pourcentage)

| Entrée | Sortie attendue |
|--------|-----------------|
| Commande : 100€ total | `Success(discountedTotal = 80.0)` |
| Code : "SUMMER20" (20% de réduction) | |

**Critères d'acceptation :**
- Le code existe en base de données
- Le client n'a jamais utilisé ce code
- Le total réduit est calculé correctement
- Le code est marqué comme utilisé pour ce client

### 2. Code déjà utilisé par le client

| Entrée | Sortie attendue |
|--------|-----------------|
| Commande : n'importe quel montant | `Failure("Code already used")` |
| Code : "ONCE50" | |
| Le client a déjà utilisé "ONCE50" | |

**Critères d'acceptation :**
- Le code existe
- Mais le client l'a déjà utilisé
- Retourner une erreur sans appliquer la réduction
- NE PAS marquer comme utilisé à nouveau

---

## Indices

Vous aurez probablement besoin de :

- `PromoCodeRepository` — pour récupérer les détails du code promo
- `CustomerPromoUsageRepository` — pour vérifier/suivre l'utilisation par client

Regardez les tests existants (ex: `PaymentServiceTest`) pour les patterns de mock.

---

## Bonus (si vous avez le temps)

- Code expiré (date d'expiration dépassée)
- Code inexistant
- Montant minimum de commande non atteint
- Réduction en montant fixe (€ au lieu de %)

