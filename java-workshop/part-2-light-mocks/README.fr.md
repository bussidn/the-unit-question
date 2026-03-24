# The Unit Question — Part 2

## 🏋️ Exercice : Choisissez votre feature

**⏱️ ~20-25 minutes** *(ne vous inquiétez pas si vous ne finissez pas)*

Choisissez **une** des features suivantes à implémenter :

---

### Option A : Code Promo

On voudrait permettre aux clients d'appliquer un code promo à leur commande. Le système doit valider le code et calculer la réduction.

#### Scénario 1 : Code promo valide

```
ÉTANT DONNÉ une commande de 100€
ET un code promo "SUMMER20" offrant 20% de réduction
ET le client n'a jamais utilisé ce code

QUAND le client applique le code promo

ALORS le total passe à 80€
ET le code est marqué comme utilisé pour ce client
```

#### Scénario 2 : Code déjà utilisé

```
ÉTANT DONNÉ une commande de 100€
ET un code promo "ONCE50"
ET le client a déjà utilisé ce code

QUAND le client applique le code promo

ALORS le système refuse avec "Code already used"
ET le total reste inchangé
```

---

### Option B : Livraison Express

On voudrait permettre aux clients de choisir la livraison express. Le système doit vérifier si la commande est éligible selon la zone de livraison.

#### Scénario 1 : Zone couverte

```
ÉTANT DONNÉ une commande avec adresse de livraison
ET le code postal "75001" (Paris)
ET cette zone est couverte par l'express

QUAND on vérifie l'éligibilité express

ALORS la commande est éligible
```

#### Scénario 2 : Zone non couverte

```
ÉTANT DONNÉ une commande avec adresse de livraison
ET le code postal "97500" (zone isolée)
ET cette zone n'est pas couverte par l'express

QUAND on vérifie l'éligibilité express

ALORS la commande n'est pas éligible
ET la raison est "Zone not covered"
```

---

### Option C : Annulation de Commande

On voudrait permettre aux clients d'annuler une commande. Le système doit gérer le remboursement, libérer le stock, et annuler l'expédition si nécessaire.

#### Scénario 1 : Annulation avant expédition

```
ÉTANT DONNÉ une commande confirmée
ET un paiement effectué (transaction ID présent)
ET pas encore expédiée (pas de tracking number)

QUAND le client annule sa commande

ALORS le paiement est remboursé
ET le stock est libéré
ET le statut passe à CANCELLED
```

#### Scénario 2 : Annulation avec expédition en cours

```
ÉTANT DONNÉ une commande confirmée
ET un paiement effectué
ET une expédition créée (tracking number présent)

QUAND le client annule sa commande

ALORS le paiement est remboursé
ET le stock est libéré
ET l'expédition est annulée
ET le statut passe à CANCELLED
```

---

## Votre mission

1. Choisissez une feature (A, B ou C)
2. Regardez le code existant dans `src/main/java/service/`
3. Implémentez la feature avec ses tests
4. Lancez les tests

## 🎯 Le but

> **L'objectif N'EST PAS de finir.**
>
> L'objectif est de **ressentir** ce qui se passe quand on ajoute une feature à cette codebase.

---

## ➡️ Suite

Une fois l'exercice terminé (ou le temps écoulé), passez à la **[Part 3](../part-3-mocks-clean/README.fr.md)**

