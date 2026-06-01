---
marp: true
theme: default
paginate: true
header: "The Unit Question"
---


<!-- _class: lead -->

# 🎯 The Unit Question

## Qu'est-ce qu'un *« unit »* dans **unit test** ?

<!--
NOTES — Intro 

OUVERTURE (~1 min)
- « Test unitaire » : tout le monde le dit, personne ne le définit pareil.
- Un « unit » = une méthode ? une classe ? un module ? un cas d'usage ?
- Tout mocker ? rien ? « juste ce qu'il faut » ?
- Pourquoi certaines suites cassent à chaque refacto et d'autres tiennent des années ?
- Aujourd'hui : pas de théorie. On va le **ressentir** dans le code.

HÉLICOPTÈRE — cadrer la Partie 1 (~1 min)
- En méca (hélico), on ne fait pas crasher l'appareil pour tester un boulon.
- Chaque pièce est testée en isolation, sur son banc dédié.
- Tester le pire cas sur l'assemblage complet : trop cher, trop lent, trop risqué.
- Les ingénieurs poussent donc l'isolation à l'extrême.
- **Partie 1 = cette philosophie prise au pied de la lettre dans le code.**
  Une méthode sous test, tout le reste mocké : autres méthodes de la même classe, constructeurs, statiques.
- Le but n'est PAS de dire « il faut faire comme ça ».
  Le but est de **pousser l'idée d'isolation à sa limite et voir ce que ça coûte** — en écriture, en lecture, en maintenance.

L'APP — prendre le temps (~2 min)
- Une seule app pour les 4 parties → on garde l'énergie pour ressentir les tests, pas pour ré-apprendre un domaine.
- Domaine : petite boutique en ligne (commandes, stock, prix, paiement, expédition).
- 5 packages : `domain` / `service` / `infrastructure` / `repository` / `gateway`.
- Le point d'entrée qu'on touchera toute la journée : `OrderService.placeOrder(Order)`.
- Ouvrir le fichier et raconter le **cycle de vie d'une commande** :
  1. valider (PENDING + items présents)
  2. réserver le stock → si indispo, rejet
  3. calculer le prix via PricingService
  4. payer → si échec, libérer le stock et rejeter
  5. créer l'expédition, confirmer, sauver, succès
- Assez simple pour tenir dans la tête, assez riche pour être testé de plein de façons.

LA RÈGLE — insister, répéter (~30 s)
- « Dans chaque partie, **même feature, mêmes changements de prod** dans `OrderService` et `Order`. »
- Même scénario : codes de réduction, 3 étapes (rejeter / appliquer la remise / marquer utilisé).
- Seul le **design des tests** change : ce qu'on mocke, ce qu'on asserte, ce qui compte comme « unit ».
- C'est ce qui rend la comparaison honnête : en Partie 4, on comparera 4 façons de tester **exactement le même diff**.
-->

---

<!--
_class: compact
-->
<style scoped>
  section { font-size: 26px; }
  h1 { margin-bottom: 0.6em; }
  ul { line-height: 1.8; }
</style>

# 💡 Conseils pratiques

- 🎯 **Travaillez par analogie** — copiez les patterns des tests existants
- 🗂️ **Ouvrez chaque partie comme un projet séparé** dans votre IDE
- ✏️ **Code de prod** : vous le refaites à chaque partie (c'est court)
- 🪂 Décroché ? `git cherry-pick <SHA>` documenté dans chaque README

<!--
NOTES — Conseils pratiques (~30 s)

- Insister, ne pas survoler. C'est ce qui fait gagner du temps dans les 4 exos.
- "Par analogie" : la clé pour boucler en ~12 min sans tout réinventer.
- "Projet séparé" : évite les « j'ai édité le mauvais `OrderService` ».
- "Refaire le prod" : court et formateur — mais le filet `cherry-pick` existe.
-->

---

<!-- _class: lead -->

# Partie 1
## Method isolation

> Une méthode sous test. **Tout le reste mocké.**

`mockConstruction` · `mockStatic` · `whenPrivate`

<!--
NOTES — Cover Partie 1 (~3 min)

- Analogie banc d'essai : on ne fait pas crasher l'hélico pour tester un boulon.
- Pousser l'idée d'isolation à l'extrême → on remplace TOUT (constructeurs, statiques, méthode privée `isValid`).
- Les 3 keywords PowerMock = les 3 outils nécessaires pour tenir cette philosophie.
- Ce n'est PAS la « bonne » méthode — c'est le bout de la logique d'isolation.
- Puis : enchaîner sur l'exo. Les participants écriront prod ET tests, vous coderez la prod en parallèle, lentement, comme repère.
-->

---

<!--
_class: compact
-->
<style scoped>
  section { font-size: 22px; }
  table { font-size: 20px; }
  h1 { margin-bottom: 0.2em; }
  h2 { margin-top: 0.4em; margin-bottom: 0.2em; }
</style>

# 🏋️ Exercice Partie 1 — Codes de réduction

## Code de production

**`Order`** — ajouter un champ nullable `DiscountCode` *(l'enum existe dans `domain/`)*
**`OrderService`** — câbler `DiscountCodeService` *(déjà fourni)* et modifier `placeOrder` :

| | Modification |
|---|---|
| **Étape 1** | Si `discountCode` présent et **non disponible** → rejeter `"Discount code already used"` |
| **Étape 2** | Si disponible → `calculateTotal(items, discountCode)` au lieu de `calculateTotal(items)` |
| **Étape 3** | Après paiement réussi → `markAsUsed(customerId, discountCode)` |

> `checkDiscountCode(customerId, code)` retourne `true` si le code est disponible

## Tests

Complétez les squelettes existants dans `OrderServiceTest`.

💡 **Travaillez par analogie** avec les tests existants — suivez les mêmes patterns !

<!--
NOTES — Lancement Partie 1

- Ouvrir `part-1-powermock/README.fr.md`.
- `./gradlew test` → `BUILD SUCCESSFUL` (sinon on débug ensemble avant de lancer le timer).
- Ouvrir `OrderServiceTest` : **regarder les tests existants d'abord** pour s'imprégner du style
  (`mockConstruction`, mocks de statiques, `whenPrivate` sur `isValid`).
- 3 squelettes attendent dans le fichier de test :
  · `p1_orderIsRejected_whenDiscountCodeIsAlreadyUsed`
  · `p1_orderIsConfirmed_whenDiscountCodeIsValid`
  · `p1_discountCodeIsMarkedAsUsed_afterSuccessfulPayment`
- 👨‍💻 **Geste clé Partie 1** : pendant que les participants codent, vous écrivez la prod en parallèle, **lentement**, sur écran visible. Pacer, pas devancer.
- 💡 Astuce à rappeler : le code de prod sera identique en Partie 2 → ils pourront le recopier ou `cherry-pick`.
- **Le but n'est PAS de finir.** Le but est de ressentir la friction d'écrire des tests dans ce style.
- ⏱ Timer : ~14 min. Go.
-->

---

<!-- _class: lead -->

# Partie 2
## Light mocks

> Une petite injection de dépendance et **Mockito redevient lisible.**

<!--
NOTES — Cover Partie 2 (~2 min)

- Petit changement de DESIGN côté prod : injection par constructeur, PricingService devient une instance.
- Conséquence : `mock()` standard suffit. Adieu `mockConstruction`, `mockStatic`, `whenPrivate`.
- À DIRE EXPLICITEMENT : cette approche, c'est ce qu'on voit dans des bases de gens compétents.
  Pour beaucoup, c'est suffisant et ça donne la confiance d'envoyer en prod. Ce n'est PAS « mauvais ».
- Ce qu'on va observer : ça marche bien, mais la duplication et certains mocks vont commencer à interroger.
-->

---

<!--
_class: compact
-->
<style scoped>
  section { font-size: 22px; }
  h1 { margin-bottom: 0.2em; }
  h2 { margin-top: 0.4em; margin-bottom: 0.2em; }
  blockquote { border-left: 4px solid #888; padding-left: 12px; margin: 0.4em 0; font-size: 20px; }
</style>

# 🏋️ Exercice Partie 2

## Code de production
**Identique à la Partie 1** (juste adapté pour l'injection par constructeur)
→ recopiez, ou `git cherry-pick <SHA>` (cf. README)

> ① reject si `discountCode` déjà utilisé · ② `calculateTotal(items, discountCode)` · ③ `markAsUsed` après paiement réussi

## Tests
3 squelettes `p2_*` dans `OrderServiceTest`.

💡 Style cible : `@BeforeEach` configure le `OrderService`, mocks injectés au constructeur.
Plus de `mockConstruction`, `mockStatic`, `whenPrivate`.

<!--
NOTES — Lancement Partie 2

- `./gradlew test` → BUILD SUCCESSFUL avant de lancer le timer.
- Tests existants à regarder en priorité : ils donnent le pattern `when().thenReturn()` + `verify()`.
- Rappeler : `cherry-pick` est documenté dans le README s'ils décrochent sur le prod.
- ⏱ Timer : ~13 min. Go.
-->

---

<!-- _class: lead -->

# Partie 3
## Clean mocks

> Builders, given helpers, et **PricingService pour de vrai.**

<!--
NOTES — Cover Partie 3 (~2 min)

- Même approche que P2, mais la boîte à outils du test propre :
  · `@ExtendWith(MockitoExtension.class)` + `@Mock` (zéro cérémonie)
  · `OrderBuilder` : la donnée métier devient lisible
  · `given*` helpers : la configuration récurrente est extraite
  · PricingService = calcul pur → on l'instancie pour de vrai
- Résultat : un test = 3-5 lignes (GIVEN / WHEN / THEN).
- Message : ce sont des bonnes pratiques **immédiatement applicables**, sans changer le design de la prod.
-->

---

<!--
_class: compact
-->
<style scoped>
  section { font-size: 22px; }
  h1 { margin-bottom: 0.2em; }
  h2 { margin-top: 0.4em; margin-bottom: 0.2em; }
  blockquote { border-left: 4px solid #888; padding-left: 12px; margin: 0.4em 0; font-size: 20px; }
</style>

# 🏋️ Exercice Partie 3

## Code de production
**Identique à la Partie 2** → recopiez, ou `git cherry-pick <SHA>` (cf. README)

> ① reject si `discountCode` déjà utilisé · ② `calculateTotal(items, discountCode)` · ③ `markAsUsed` après paiement réussi

## Tests
3 squelettes `p3_*` dans `OrderServiceTest`.

💡 Boîte à outils : `@Mock`, `OrderBuilder`, **écrivez vos** `givenDiscountCodeIs...()` helpers.
**`PricingService` est réel** (calcul pur) — pas de mock dessus.

<!--
NOTES — Lancement Partie 3

- Tests devraient être beaucoup plus courts qu'en P2 grâce aux helpers.
- Insister : ils peuvent (doivent) écrire leurs propres `givenDiscountCodeIsAvailable(...)` / `givenDiscountCodeIsAlreadyUsed(...)`.
- ⏱ Timer : ~13 min. Go.
-->

---

<!-- _class: lead -->

# Partie 4
## Behavior tests

> Plus aucun mock. On teste **le comportement, pas les appels.**

<!--
NOTES — Cover Partie 4 (~3 min)

- Services deviennent des interfaces (ports) : StockService, PaymentService, ShippingService.
- Implémentations RÉELLES : RepositoryStockService, GatewayPaymentService, GatewayShippingService, new PricingService().
- Aucun @Mock, aucun when().thenReturn(), aucun verify().
- Given helpers manipulent de l'état réel : `givenStockIsAvailableFor(order)`, `givenPaymentIsDeclined()`.
- Assertions sur l'état réel : `paymentGateway.wasCharged()`, `stockRepository.availableStock(...)`.
- L'unité ici, c'est LE USE CASE — pas la méthode, pas la classe.
- Coût : interfaces côté prod + in-memory adapters à maintenir.
- Bénéfice : tests qui survivent au refactoring.
-->

---

<!--
_class: compact
-->
<style scoped>
  section { font-size: 22px; }
  h1 { margin-bottom: 0.2em; }
  h2 { margin-top: 0.4em; margin-bottom: 0.2em; }
  blockquote { border-left: 4px solid #888; padding-left: 12px; margin: 0.4em 0; font-size: 20px; }
</style>

# 🏋️ Exercice Partie 4

## Code de production
**Identique à la Partie 3** → recopiez, ou `git cherry-pick <SHA>` (cf. README)

> ① reject si `discountCode` déjà utilisé · ② `calculateTotal(items, discountCode)` · ③ `markAsUsed` après paiement réussi

## Tests
3 squelettes `p4_*` dans `OrderServiceTest`.

💡 `InMemoryDiscountCodeRepository` **déjà fourni** — wirez juste le `DiscountCodeService`.
Pas de `@Mock`, pas de `when().thenReturn()`, pas de `verify()`.
Assertions sur l'**état réel** des repos / gateways.

<!--
NOTES — Lancement Partie 4

- Wiring : OrderService(stockService, paymentService, shippingService, pricingService, discountCodeService).
- Assertions à pousser : `paymentGateway.wasCharged()`, `stockRepository.availableStock(...)`, `discountCodeRepository.isUsed(...)`.
- C'est l'aboutissement — on lit du métier sans bruit technique.
- ⏱ Timer : ~13 min. Go.
-->

---

<!-- _class: lead -->

# 🎯 Débrief

<br>

## C'est quoi un *unit* pour vous, **maintenant** ?

<!--
NOTES — Débrief final (~8 min)

LAISSER LE SILENCE. Ne pas répondre à la place de la salle.

Si besoin de relancer, piocher dans les questions de synthèse (cf. guide animateur §9) :
- Quelle partie vous a semblé la plus naturelle ?
- Dans laquelle avez-vous le plus confiance pour détecter une vraie régression ?
- Laquelle survivrait le mieux à un refactoring du code de prod ?
- Laquelle reliriez-vous dans 6 mois en comprenant immédiatement ce qui est testé ?

Questions étendues (selon le temps / dynamique) :
- Si vous démarriez un projet demain, quelle approche par défaut ?
- Pour les vrais adapters externes (DB, API, bus), où mettriez-vous le curseur ?
- Quand un test échoue en P4, diagnostic plus facile ou plus dur qu'en P1/2/3 ?

Conclusion à exprimer :
- Notre parti pris : unit = comportement (use case).
- Le vrai message : QUESTIONNEZ ce que vous mockez. Chaque mock est une dette.
- Le réflexe à emporter : « pourquoi ne pas l'utiliser pour de vrai ? »

→ Une fois la conclusion landed, passer à la slide suivante (« À quoi servent les tests unitaires ? ») pour l'image finale à emporter.
-->

---

<!--
_class: compact
-->
<style scoped>
  section { font-size: 24px; }
  h1 { margin-bottom: 0.5em; }
  h2 { margin-top: 0.7em; margin-bottom: 0.2em; color: #444; }
  ul:nth-of-type(1) { font-size: 32px; line-height: 1.8; font-weight: 600; }
  ul:nth-of-type(2) { font-size: 22px; line-height: 1.5; color: #666; }
</style>

# À quoi servent les tests unitaires ?

## Avant tout

- Validation fonctionnelle
- Spécification
- Documentation technique

## Aussi

- Non-régression
- Refactoring
- Aide à la conception via le TDD

<!--
NOTES — Message de clôture (~2 min)

À DIRE APRÈS QUE LA CONCLUSION DU DÉBRIEF AIT ÉTÉ DITE. C'est l'image finale à emporter, pas l'ouverture d'un nouveau débat.

LES 3 RÔLES PRIMAIRES (à dérouler, dans l'ordre)
- VALIDATION FONCTIONNELLE : prouver que le comportement métier est correct. C'est ce qui justifie l'existence du test.
- SPÉCIFICATION : un test bien écrit décrit l'intention métier mieux qu'un commentaire. GIVEN / WHEN / THEN = spec exécutable.
- DOCUMENTATION TECHNIQUE : la seule doc qui ne ment pas, parce qu'elle s'exécute. Documentation DYNAMIQUE : elle dit NON quand on essaie de changer ce qu'elle décrit.

LES 3 RÔLES SECONDAIRES (mentionner brièvement, ne pas s'y attarder)
- NON-RÉGRESSION : un bénéfice automatique des 3 premiers, pas une raison d'écrire un test.
- REFACTORING : les tests supportent le refactoring SI ils testent le comportement et non l'implémentation (cf. P1 → P4). Effet de bord du « bien tester ».
- TDD / DESIGN : le TDD donne du feedback sur la conception. Utile, mais c'est un outil de DESIGN — pas le but premier d'un test.

POURQUOI LA HIÉRARCHIE COMPTE
- Quand on confond les rôles secondaires avec les rôles primaires, on écrit des tests pour « se protéger » plutôt que pour « spécifier » — et on se retrouve avec une suite qui fige des détails (cf. P1) au lieu du comportement (cf. P4).
- C'est l'autre face de « chaque mock est une dette » : chaque assertion est un engagement. Choisissez ce que vous engagez.
- Le test sert d'abord à DIRE ce que le code doit faire. Le reste suit.
-->
