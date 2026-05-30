# 🎤 Notes orateur — Intro + Partie 1

> Compagnon des slides `slides.fr.md`.
> À garder ouvert sur un second écran pendant la présentation.

---

## Slide 1 — The Unit Question (intro)

### 🎬 Ouverture (~1 min)

- « Test unitaire » : tout le monde le dit, **personne ne le définit pareil**.
- Un « unit », c'est quoi ?
  - Une méthode ? Une classe ? Un module ? Un cas d'usage ?
- Tout mocker ? Rien mocker ? « Juste ce qu'il faut » ?
- Pourquoi certaines suites de tests cassent à chaque refacto et d'autres tiennent des années ?
- Aujourd'hui, **on ne répond pas en théorie**. On va le **ressentir** avec du code.

### 🛒 L'application support — prendre le temps (~2 min)

**Pourquoi une seule app pour tout le workshop**
- On reste volontairement dans le **même domaine métier** sur les 4 parties.
- Pas la peine de gaspiller de l'énergie à ré-apprendre un domaine à chaque fois — gardez-la pour observer comment **les tests, eux, changent de feeling**.
- Le domaine : **une petite boutique en ligne**. Commandes, stock, prix, paiement, expédition.

**Structure — 5 packages**
- `domain` — les records : `Order`, `OrderItem`, `PaymentResult`, `StockReservation`, …
- `service` — les orchestrateurs : `OrderService`, `StockService`, `PricingService`, `PaymentService`, `ShippingService`, `DiscountCodeService`
- `repository`, `infrastructure`, `gateway` — les bords du système

**Le point d'entrée qu'on va toucher pendant tout le workshop : `OrderService.placeOrder(Order)`**

👉 *Ouvrir le fichier et le dérouler à voix haute — il raconte le cycle de vie d'une commande :*

1. **Valider** la commande (statut PENDING, items présents).
2. **Réserver le stock** — si quelque chose est indisponible → rejet.
3. **Calculer le prix** via `PricingService`.
4. **Traiter le paiement** — en cas d'échec, libérer le stock et rejeter.
5. **Créer l'expédition**, **confirmer** la commande, la **sauver**, retourner le succès.

> Une méthode, un happy path, quelques branches.
> Assez simple pour tenir dans la tête — assez riche pour être testée de manières très différentes.

### 🔁 Le message clé — insister, répéter (~30 s)

À dire **explicitement**, deux fois si besoin :

> « Dans chaque partie de ce workshop, vous allez implémenter **la même feature**, faire **les mêmes changements de prod** dans `OrderService` et `Order`. La seule chose qui change — d'une partie à l'autre — c'est le **design des tests**. »

- Même scénario métier : codes de réduction.
- Mêmes 3 étapes : rejeter les codes déjà utilisés → appliquer la remise → marquer comme utilisé après paiement.
- Ce qui change : ce que vous mockez, ce que vous vérifiez, ce qui compte comme « unit ».

C'est ce qui rend la comparaison **honnête**. En Partie 4, vous ne comparerez pas des pommes et des oranges — vous comparerez **4 façons de tester exactement le même diff**.

---

### 🚁 Analogie hélicoptère — cadrer la Partie 1 (~1 min)

- En ingénierie mécanique — pensez aux hélicoptères — **on ne fait pas crasher l'appareil pour tester un boulon**.
- Chaque pièce est testée en isolation, sur son propre banc d'essai : le boulon, la pale, la boîte de vitesses.
- Pourquoi ? Parce que tester le pire cas sur l'hélicoptère assemblé est **trop cher, trop lent, trop risqué**.
- Les ingénieurs poussent donc l'isolation à l'extrême : chaque pièce, sur un banc dédié, tout l'environnement simulé.
- **La Partie 1 prend cette philosophie au pied de la lettre dans le code** :
  - isolation extrême,
  - **une méthode** sous test,
  - tout le reste mocké — y compris les autres méthodes de la même classe, les constructeurs, les statiques.
- L'objectif n'est **pas** de dire « c'est comme ça qu'il faut tester ».
  L'objectif est de **pousser l'idée d'isolation à sa limite et voir ce que ça coûte** — en écriture, en lecture, en maintenance.


## Slide 2 — Exercice : Codes de réduction (lancement Partie 1)

### ▶️ Lancement (~1 min)

- Ouvrir `part-1-powermock/README.fr.md`.
- `./gradlew test` → `BUILD SUCCESSFUL` (sinon on débug ensemble avant de lancer le timer).
- Ouvrir `OrderServiceTest` : **regarder les tests existants d'abord** pour s'imprégner du style :
  - `mockConstruction` pour mocker les constructeurs,
  - mocks de méthodes statiques,
  - `whenPrivate` sur `isValid` (méthode de la classe sous test).

### 📝 3 squelettes à compléter

| Étape | Méthode de test |
|---|---|
| 1 | `p1_orderIsRejected_whenDiscountCodeIsAlreadyUsed` |
| 2 | `p1_orderIsConfirmed_whenDiscountCodeIsValid` |
| 3 | `p1_discountCodeIsMarkedAsUsed_afterSuccessfulPayment` |

### 💡 À rappeler

- Le code de prod sera **identique** en Partie 2 → ils pourront le copier-coller, et se concentrer sur les tests.
- **L'objectif n'est pas de finir.** L'objectif est de **ressentir la friction** d'écrire des tests dans ce style.
- ⏱ Timer : **~15 min**.

### 🚦 Go.
