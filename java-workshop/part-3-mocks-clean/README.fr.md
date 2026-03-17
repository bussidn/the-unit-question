# Atelier Java – Partie 3 : Mocks propres avec LE PIÈGE

Cet exercice montre un problème subtil lié aux mocks : les deux tests unitaires passent, mais l'intégration réelle est cassée parce que le contrat entre deux services ne correspond pas.

## Le piège

`PaymentService.refundPayment()` signifie :

- `true` → l'argent a été remboursé
- `false` → rien à rembourser

`OrderCancellationService` suppose :

- `true` → succès
- `false` → échec du remboursement

Les deux côtés sont testés. Les deux tests passent. Pourtant le système reste incorrect.

## Objectif

Lire les tests et l'implémentation, puis expliquer :

1. Pourquoi les tests passent
2. Pourquoi le comportement en production est faux
3. Comment redessiner le contrat pour éviter ce piège

## Fichiers à inspecter

- `src/main/java/service/GatewayPaymentService.java`
- `src/main/java/service/OrderCancellationService.java`
- `src/test/java/service/PaymentServiceTest.java`
- `src/test/java/service/OrderCancellationServiceTest.java`

## Lancer les tests

```bash
./gradlew test
```

## Pistes de discussion

- Faut-il remplacer le `boolean` par un type de résultat plus riche ?
- Faut-il considérer « rien à rembourser » comme un succès ?
- Les échecs doivent-ils être représentés par une exception ou par un résultat explicite du domaine ?

Si vous voulez la réponse, consultez `reveal/bug-report.fr.md`.