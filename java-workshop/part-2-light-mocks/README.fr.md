# The Unit Question — Part 2

## 🏋️ Exercice : Code Promo

**⏱️ ~20-25 minutes**

On voudrait permettre aux clients d'appliquer un code promo à leur commande. Le système doit valider le code et calculer la réduction.
Celle-ci doit s'appliquer au prix avant calcul des taxes et ajout des frais de livraison.
Si le code a déjà été utilisé, la commande doit être rejetée avec un message clair.

---

## 💡 Informations utiles

- Les codes promos existants sont listés dans l'enum [DiscountCode](src/main/java/domain/DiscountCode.java)
- Le repository [DiscountCodeRepository](/src/main/java/repository/DiscountCodeRepository.java) met déjà a disposition de quoi gérer les associations entre codes promos et clients.  

#### Scénario 1 : Code promo valide

```
ÉTANT DONNÉ une commande avec des articles pour 100€
ET un code promo "SUMMER20" offrant 20% de réduction
ET le client n'a jamais utilisé ce code

QUAND le client applique le code promo

ALORS la réduction s'applique au sous-total avant taxes et livraison : 80€ (100€ - 20%)
ET les taxes (20%) sont calculées sur ce sous-total : +16€
ET les frais de livraison sont ajoutés (80€ < seuil de 100€) : +5€
ET le total passe à 101€
ET le code est marqué comme utilisé pour ce client
```

#### Scénario 2 : Code déjà utilisé

```
ÉTANT DONNÉ une commande avec des articles pour 100€
ET un code promo "ONCE50"
ET le client a déjà utilisé ce code

QUAND le client applique le code promo

ALORS le système refuse avec "Code already used"
ET le total reste inchangé à 120€ (100€ + 20% de taxes, livraison offerte)
```


---

## Votre mission

1. Regardez le code existant dans `src/main/java/service/`
2. Implémentez la feature avec ses tests
3. Lancez les tests

---

## ➡️ Suite

Une fois l'exercice terminé (ou le temps écoulé), passez à la **[Part 3](../part-3-mocks-clean/README.fr.md)**

