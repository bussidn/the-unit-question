# Exercice : Notification Email

## La feature

> *"Quand une commande est créée avec succès, envoyer un email de confirmation au client."*

**⏱️ Temps : ~15 minutes** *(ne vous inquiétez pas si vous ne finissez pas)*

---

## Scénarios

### Scénario 1 : Commande réussie → email envoyé

```
ÉTANT DONNÉ un client avec l'email "alice@example.com"
ET un panier contenant 2 articles pour un total de 49.99€
ET le stock est disponible
ET le paiement est accepté

QUAND le client valide sa commande

ALORS la commande est créée avec succès
ET un email est envoyé à "alice@example.com"
ET l'email contient "Commande ORDER-123 confirmée : 49.99€"
```

### Scénario 2 : Paiement refusé → pas d'email

```
ÉTANT DONNÉ un client avec l'email "bob@example.com"
ET un panier contenant 1 article
ET le stock est disponible
ET le paiement est refusé

QUAND le client valide sa commande

ALORS la commande échoue
ET aucun email n'est envoyé
```

---

## Votre mission

1. Regardez le code existant dans `OrderService.java`
2. Implémentez la feature comme bon vous semble
3. Lancez les tests

---

## 🎯 Le but

> **L'objectif N'EST PAS de finir.**
>
> L'objectif est de **ressentir** ce qui se passe quand on ajoute une feature à cette codebase.

