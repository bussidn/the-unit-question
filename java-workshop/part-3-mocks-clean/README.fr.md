# The Unit Question — Part 3

## 🏋️ Exercice : Annulation de Commande

**⏱️ ~20 minutes** *(ne vous inquiétez pas si vous ne finissez pas)*

On voudrait permettre aux clients d'annuler une commande. Le système doit gérer le remboursement, libérer le stock, et annuler l'expédition si nécessaire.

### Scénario 1 : Annulation avant expédition

```
ÉTANT DONNÉ une commande confirmée
ET un paiement effectué (transaction ID présent)
ET pas encore expédiée (pas de tracking number)

QUAND le client annule sa commande

ALORS le paiement est remboursé
ET le stock est libéré
ET le statut passe à CANCELLED
```

### Scénario 2 : Annulation avec expédition en cours

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

### Votre mission

1. Regardez le code existant dans `src/main/java/service/`
2. Regardez les tests existants — notez les helpers et builders
3. Implémentez la feature avec ses tests
4. Lancez les tests : `./gradlew test`

### 🎯 Le but

> **L'objectif N'EST PAS de finir.**
>
> L'objectif est de **ressentir** ce qui se passe quand on ajoute une feature à cette codebase.

---

## ⏰ Temps écoulé ?

Une fois l'exercice terminé (ou le temps écoulé), ouvrez le fichier **[reveal/bug-report.fr.md](reveal/bug-report.fr.md)**

---

## ➡️ Suite

Passez à la **[Part 4](../part-4-behavior/README.fr.md)**