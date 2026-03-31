# The Unit Question — Part 2

## 🏋️ Exercice : Code Promo

**⏱️ ~20-25 minutes** *(ne vous inquiétez pas si vous ne finissez pas)*

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

## 💡 Pistes de conception

- Le code promo que l'on tente d'appliquer peut être stocké directement dans la commande (`Order`)
- Le calcul de la réduction peut être délégué au `PricingService`

---

## Votre mission

1. Regardez le code existant dans `src/main/java/service/`
2. Implémentez la feature avec ses tests
3. Lancez les tests

## 🎯 Le but

> **L'objectif N'EST PAS de finir.**
>
> L'objectif est de **ressentir** ce qui se passe quand on ajoute une feature à cette codebase.

---

## ➡️ Suite

Une fois l'exercice terminé (ou le temps écoulé), passez à la **[Part 3](../part-3-mocks-clean/README.fr.md)**

