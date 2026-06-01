# Guide animateur — The Unit Question

> Notes de présentation. Fil conducteur à l'oral.
> Les slides Marp (`slides.fr.md`) sont le support visuel ; ce fichier contient le narratif.
> 👉 À garder ouvert sur un second écran pendant l'animation.

> 📌 **TODO avant le J** : créer les commits de référence du code de prod pour P2/P3/P4 et reporter les SHAs dans les READMEs de chaque partie (les slides mémos pointent déjà vers « cf. README »). Sans ça, le filet `git cherry-pick` ne marche pas.

---

## ⏱ Budget temps (1h45 disponibles, ~15 min de marge)

| Phase | Durée | Slides |
|---|---|---|
| Intro | 8 min | 1 + 2 |
| Partie 1 — présentation + **live-code prod** + exo + débrief | 3 + 4 + 10 + 3 = **20 min** | 3 (cover) + 4 (mémo exo) |
| Transition P1 → P2 | 1 min | — |
| Partie 2 — présentation + exo (prod + tests) + débrief | 2 + 13 + 2 = **17 min** | 5 (cover) + 6 (mémo exo) |
| Transition P2 → P3 | 1 min | — |
| Partie 3 — présentation + exo + débrief | 2 + 13 + 2 = **17 min** | 7 (cover) + 8 (mémo exo) |
| Transition P3 → P4 | 1 min | — |
| Partie 4 — présentation + exo + débrief | 3 + 13 + 2 = **18 min** | 9 (cover) + 10 (mémo exo) |
| Débrief final | 8 min | 11 |
| **Total** | **~1h31** | |

📌 **Particularités** :
- **Partie 1** : le code de prod est **live-codé** par l'animateur pendant la présentation. Pour l'exo, *« le but n'est pas de finir »*.
- **Parties 2/3/4** : les participants refont eux-mêmes le code de prod (c'est rapide, ils l'ont déjà fait en P1). Un `git cherry-pick <SHA>` est documenté dans chaque README comme **filet de sécurité** s'ils décrochent. **Le but, ici, est de finir.**

⚠️ Si on dérape sur la Partie 1, c'est la Partie 4 qui en pâtit — or c'est elle la destination. Tenir le timing.

---

## 1. Introduction — Présenter l'atelier (~8 min)

🎬 **Slide 1 — "The Unit Question"**

### Message clé
> Tout le monde parle de « tests unitaires », mais personne n'est d'accord sur ce que « unit » veut dire.

### Origines

- Cet atelier est **issu d'une démarche personnelle**
  - Ce n'est pas un cours académique, c'est le fruit d'une réflexion sur nos propres pratiques
  - On considère que les approches vont « de moins bien à mieux » — c'est **opinionated**, assumé
  - Le parti pris : **unit = behavior**. Les 4 parties sont là pour **vivre les compromis** de chaque approche et **confronter votre intuition à la pratique** — vous jugerez par vous-mêmes si vous arrivez à la même conclusion

### Structure de l'atelier

- **4 parties**, toujours le même exercice, toujours la même feature (codes de réduction)
- Le code de production est **identique** d'une partie à l'autre — *sauf Part 1 qui a une légère différence de design (pas d'injection de dépendance)*
- Ce qui change : **l'approche de test**, la boîte à outils
- Les parties sont censées être **de plus en plus faciles** — si c'est le cas, c'est que l'approche est de plus en plus naturelle
- Avantage de la répétition : on ne perd pas de temps à re-découvrir un domaine métier, on se concentre sur le ressenti

### Coup d'œil dans le code (prendre le temps)

👉 *Ouvrir `OrderService.placeOrder` dans l'IDE et le dérouler à voix haute — il raconte le cycle de vie d'une commande :*

1. **Valider** la commande (statut PENDING, items présents)
2. **Réserver le stock** — si quelque chose est indisponible → rejet
3. **Calculer le prix** via `PricingService`
4. **Traiter le paiement** — en cas d'échec, libérer le stock et rejeter
5. **Créer l'expédition**, **confirmer** la commande, la **sauver**, retourner le succès

> Une méthode, un happy path, quelques branches.
> Assez simple pour tenir dans la tête — assez riche pour être testée de manières très différentes.

### Conseils pratiques aux participants

🎬 **Slide 2 — "Conseils pratiques"** — afficher pendant qu'on insiste sur ces points (à ne pas survoler)

- **Travaillez par analogie** avec les tests existants dans chaque partie — copiez le pattern
- **Ouvrez chaque partie comme un projet séparé** dans votre IDE pour éviter les confusions de navigation (mauvais fichier, mauvaise part)
- **Code de prod** : à chaque partie, vous le refaites vous-mêmes (c'est court). Si vous décrochez, un `git cherry-pick` documenté dans le README de chaque partie vous remet à jour

---

## 2. Partie 1 — Method isolation / PowerMock (~20 min)

🎬 **Slide 3 — "Partie 1 / Method isolation"** (cover, affichée pendant les ~3 min de présentation)

### Présentation (~3 min)

#### L'analogie du banc d'essai

- Dans l'industrie aéronautique, on teste les pièces sur des **bancs d'essai**
  - On isole une pièce (un réacteur, un train d'atterrissage) de l'avion complet
  - On la branche sur des simulateurs qui reproduisent les conditions de vol
  - Chaque entrée/sortie est contrôlée, chaque capteur est monitoré
- C'est exactement ce que fait la Part 1 avec le code :
  - On prend une **méthode** (l'unité)
  - On remplace **toutes** ses dépendances par des simulateurs (mocks)
  - On contrôle chaque entrée, on vérifie chaque sortie

#### Le parallèle avec PowerMock / mockConstruction

- `mockConstruction` = le banc d'essai
  - On intercepte la création de chaque dépendance
  - On la remplace par un simulateur contrôlé
- `mockStatic` pour les méthodes statiques (`PricingService.calculateTotal`)
- `whenPrivate` / `spyOn` pour stuber jusqu'à `isValid` (méthode privée de la classe sous test)
- Le test ne touche **que** la méthode `placeOrder` — tout le reste est simulé

### Live-coding du code de prod (~4 min)

> 👨‍💻 **Geste clé** : l'animateur écrit le code de prod en direct pendant que la salle observe.
> Les participants démarrent l'exo avec un code de prod **déjà à jour** et se concentrent uniquement sur les tests.

- Ouvrir `OrderService` côté Part 1
- Ajouter le champ `discountCode` dans `Order` (record)
- Wirer `DiscountCodeService` (instanciation interne, vu qu'on n'a pas la DI en P1)
- Ajouter la branche `if (discountCode != null && !discountCodeService.checkDiscountCode(...))` → reject
- Brancher `PricingService.calculateTotal(items, discountCode)` quand le code est valide
- Appeler `markAsUsed` après paiement réussi
- Narrer en parallèle : « regardez, c'est petit côté prod — c'est sur les tests qu'on va sentir la différence »

### Lancement de l'exercice (~10 min)

🎬 **Slide 4 — "Exo Partie 1"** (mémo, reste affichée pendant les 10 min)

- 3 squelettes dans `OrderServiceTest` :
  - `p1_orderIsRejected_whenDiscountCodeIsAlreadyUsed`
  - `p1_orderIsConfirmed_whenDiscountCodeIsValid`
  - `p1_discountCodeIsMarkedAsUsed_afterSuccessfulPayment`
- **Rappel : le but n'est pas de finir.** Le but est de ressentir.
  *(C'est la seule partie où on dit ça — sur P2/3/4, on veut qu'ils finissent.)*
- Timer : ~10 min

### Débrief (~3 min) — points à faire émerger

#### 🎯 Core — la « unit »

- C'était quoi la « unit » ici ? → **une méthode**
- Si on **renomme `isValid`** ou qu'on **inline le code de validation**, que se passe-t-il ? → tests cassent **sans qu'il y ait de régression métier**
- `verify(constructed.get(0))` : on teste **l'ordre de construction des objets**, pas un comportement métier
- Est-ce que ce test vous protège vraiment contre une **vraie régression** ? Qu'est-ce qui casserait ce test — un changement de comportement métier ou un refactoring interne ?

#### 📎 Annexe — ergonomie et lisibilité

- Combien de lignes de **setup** pour combien de lignes d'**assertion** ? (compter à voix haute — typiquement 20+ lignes de plomberie pour 2-3 lignes utiles)
- `try-with-resources` à 5 niveaux : qu'est-ce qu'on lit vraiment ?
- Combien de temps avant qu'un nouvel arrivant comprenne ce qu'il y a à modifier ?

---

## 3. Transition Partie 1 → Partie 2 (~1 min)

- « On vient de payer le prix de l'isolation extrême : `mockConstruction`, `mockStatic`, `whenPrivate`, du try-with-resources partout, des `verify` sur des indices de construction… »
- « Le coupable, ce n'est PAS Mockito. C'est le **design du code de prod** : pas d'injection de dépendance, des `new` en dur, du statique. »
- « Petit changement de design en Partie 2 : **injection par constructeur**. Et regardez ce qui devient possible. »

---


## 4. Partie 2 — Light mocks (~17 min)

🎬 **Slide 5 — "Partie 2 / Light mocks"** (cover, affichée pendant les ~2 min de présentation)

### Présentation (~2 min)

#### Ce qui change par rapport à la Partie 1

- Les dépendances sont **injectées par le constructeur** (plus de `new` interne dans `placeOrder`)
- `PricingService` devient une instance (plus statique)
- → conséquence directe : on peut utiliser `mock()` standard et passer les mocks au constructeur
- **Plus de `mockConstruction`, plus de `mockStatic`, plus de `whenPrivate`**
- `isValid` tourne pour de vrai
- Un `@BeforeEach` configure le `OrderService` une fois pour tous les tests

#### Le positionnement de cette approche

> ⚠️ **À dire explicitement — important pour ne pas braquer la salle :**
>
> Cette approche entre dans la catégorie **« choses que je vois aujourd'hui dans des bases de code écrites par des gens compétents »** — mais qui n'ont pas vu l'intérêt d'aller plus loin sur le design des tests. Pour beaucoup, c'est **suffisant** et ça leur donne la **confiance d'envoyer en prod**.
>
> Ce n'est pas « mauvais ». C'est juste **pas optimal** quand on regarde de près. Et c'est ce qu'on va regarder.

### Lancement de l'exercice (~13 min)

🎬 **Slide 6 — "Exo Partie 2"** (mémo, reste affichée pendant l'exo)

- 3 squelettes parallèles à la Partie 1 (préfixe `p2_`)
- **Code de prod** : ils le refont eux-mêmes (très proche de la Partie 1, juste adapté pour la DI). Filet de sécurité : `git cherry-pick <SHA>` documenté dans le README s'ils décrochent.
- **Le but, ici, est de finir.**
- Timer : ~13 min

### Débrief (~2 min) — points à faire émerger

#### 🎯 Core — la « unit »

- C'était quoi la « unit » ici ? → **la classe** (via injection constructeur)
- Qu'est-ce qui a changé par rapport à P1 ? → on est passé de la méthode à la classe, mais on teste toujours des **interactions** (`verify`), pas des **résultats métier**
- Si on change la signature d'une méthode (ajouter un paramètre à `processPayment`), **combien de tests il faut toucher** ?

#### 📎 Annexe — ergonomie et observations

- C'était plus rapide à écrire ? Plus lisible ? (oui, clairement — c'est même agréable)
- **Mais regardez les répétitions** : chaque test « happy path » duplique 4 lignes de `when(...)` (stock, pricing, payment, shipping). Ouvrir 2 tests côte à côte.
- On **mocke `PricingService`** — c'est du **calcul pur**. Pourquoi le mocker ?
  → On en fait une boîte noire alors qu'on pourrait juste l'utiliser

---

## 5. Transition Partie 2 → Partie 3 (~1 min)

- « La Partie 2 marche bien — mais avec 5 dépendances et 10+ tests, on commence à voir la duplication. »
- « Et on se rend compte qu'on mocke des choses qui n'auraient peut-être pas besoin de l'être. »
- « La Partie 3 garde la même approche, mais sort la boîte à outils du test propre : »
  - **builders** pour les données métier
  - **given helpers** pour la configuration récurrente
  - **utilisation réelle** quand c'est du calcul pur

---

## 6. Partie 3 — Clean mocks / Mockito annotations (~17 min)

🎬 **Slide 7 — "Partie 3 / Clean mocks"** (cover, affichée pendant les ~2 min de présentation)

### Présentation (~2 min)

#### Ce qui change par rapport à la Partie 2

- **`@ExtendWith(MockitoExtension.class)` + `@Mock`** : zéro cérémonie pour créer les mocks
- **`OrderBuilder`** : `anOrder().withCustomerId(...).withItems(...).build()` → la donnée métier devient lisible
- **Given helpers** : `givenPaymentSucceedsFor(order)`, `givenReservationSucceedsFor(order)`, `givenShipmentCreatedFor(order)` → la configuration récurrente est extraite
- **`PricingService` n'est plus mocké** : c'est du calcul pur → on l'instancie pour de vrai dans le `@BeforeEach`
- → un test devient 3-5 lignes : GIVEN (helpers), WHEN (`placeOrder`), THEN (assertion)

#### Le message

- Avec très peu d'outillage (un builder, quelques helpers, un peu de bon sens sur ce qu'on mocke), les tests deviennent **lisibles comme une spec**.
- Ce sont de bonnes pratiques applicables **immédiatement** dans n'importe quelle base existante, sans aucun changement de design côté prod.

> ⚠️ **Honnêteté intellectuelle** : reconnaître que le saut P2→P3 vient du **design des tests**, pas de la définition de "unit" — ça met du plomb dans l'aile de la thèse de l'atelier, et c'est OK de le dire.

### Lancement de l'exercice (~13 min)

🎬 **Slide 8 — "Exo Partie 3"** (mémo, reste affichée pendant l'exo)

- 3 squelettes (préfixe `p3_`)
- **Code de prod** : identique à la Partie 2 — ils peuvent le retaper ou `git cherry-pick <SHA>` (documenté dans le README)
- **Astuce à rappeler** : ils peuvent (et doivent) écrire leur propre `givenDiscountCodeIsAvailable(...)` / `givenDiscountCodeIsAlreadyUsed(...)` — c'est l'esprit
- Timer : ~13 min

### Débrief (~2 min) — points à faire émerger

#### 🎯 Core — la « unit »

- C'était quoi la « unit » ici ? → on a essayé de passer de **la classe** à **la couche applicative** : `PricingService` n'est plus mocké, on l'utilise pour de vrai. C'est ce qu'on ferait dans un Controller où on mocke la couche suivante (usecase/domain) mais on garde les vrais mappers.
- La « unit » a-t-elle vraiment changé entre P2 et P3 ? → à peine. Le confort a changé, pas le fond. On a toujours du `verify(...)`, `verifyNoInteractions(...)` → on teste encore des **interactions**
- Si demain on ajoute une dépendance à `OrderService`, combien de tests à modifier ? → la fragilité structurelle reste

#### 📎 Annexe — ergonomie et lisibilité

- Comparez la longueur d'un test P2 vs P3 (ouvrir les deux côte à côte). Lisibilité ?
- Sur un test P3, on lit **quoi** plutôt que **comment** → on se rapproche d'une spec
- Les given helpers, c'est de la mécanique qu'on construit nous-mêmes pour cacher Mockito → est-ce qu'on ne réinvente pas, manuellement, une **couche d'infrastructure de test** ?

---

## 7. Transition Partie 3 → Partie 4 (~1 min)

- « On a poli les mocks autant qu'on pouvait. Les tests sont jolis. »
- « Question gênante : **et si on enlevait les mocks pour de vrai** ? »
- « Pour ça, il faut un petit investissement de design côté prod : passer par des **interfaces (ports/adapters)**. »
- « En échange, on a des tests **sans aucun mock**. Plus de `when/thenReturn`, plus de `verify`. »

---

## 8. Partie 4 — Behaviour tests / No mocks (~18 min)

🎬 **Slide 9 — "Partie 4 / Behavior tests"** (cover, affichée pendant les ~3 min de présentation)

### Présentation (~3 min)

#### Ce qui change par rapport à la Partie 3

- Les services implémentent des **interfaces (ports)** : `StockService`, `PaymentService`, `ShippingService` sont désormais des interfaces
- Toutes les implémentations sont **réelles** :
  - `RepositoryStockService` (vraie logique, backed par `InMemoryStockRepository`)
  - `GatewayPaymentService` / `GatewayShippingService` (vraie logique, backed par des gateways in-memory)
  - `new PricingService()` directement
- **Plus aucun mock** : ni `@Mock`, ni `when().thenReturn()`, ni `verify()`
- Les given helpers manipulent maintenant de l'**état réel** : `givenStockIsAvailableFor(order)`, `givenPaymentIsDeclined()`
- Les assertions inspectent de l'**état réel** : `paymentGateway.wasCharged()`, `stockRepository.availableStock(...)`

#### Le message

- L'unité ici, c'est **le use case** (le scénario métier « passer une commande »), pas la méthode, pas la classe.
- Le test ressemble à la spec :
  > GIVEN du stock disponible
  > WHEN je passe la commande
  > THEN la commande est confirmée ET le paiement est encaissé
- **Coût** : il faut accepter d'introduire des interfaces côté prod et maintenir des in-memory adapters.
- **Bénéfice** : les tests **survivent au refactoring** (renommer une méthode interne ne casse rien), et donnent une **confiance réelle** sur le comportement métier de bout en bout.

### Lancement de l'exercice (~13 min)

🎬 **Slide 10 — "Exo Partie 4"** (mémo, reste affichée pendant l'exo)

- 3 squelettes (préfixe `p4_`)
- **Code de prod** : identique à la Partie 3 — ils peuvent le retaper ou `git cherry-pick <SHA>` (documenté dans le README)
- `InMemoryDiscountCodeRepository` est **déjà fourni** dans `helper/` → ils n'ont qu'à wirer le `DiscountCodeService`
- Timer : ~13 min

### Débrief (~2 min) — points à faire émerger

#### 🎯 Core — la « unit »

- C'était quoi la « unit » ici ? → **le use case, le comportement**
- Si je renomme `reserveStock` en `holdInventory`, **combien de tests cassent** ? → zéro. En P1, un renommage cassait tout — qu'est-ce qui a changé ?
- Est-ce que c'est encore un « test unitaire » ? **C'est tout l'enjeu de l'atelier.**
- `placeOrder` exerce **vraiment** la chaîne complète — si une intégration interne casse, le test échoue. C'est très différent d'un test mocké.
- 💣 **Le twist** : regardez `checkDiscountCode`. Il renvoie `true` quand le code est **déjà utilisé**, pas quand il est disponible. La spec était fausse depuis le début. En P1, P2 et P3, personne n'a vu le bug — on mockait le résultat attendu, on ne testait jamais le vrai comportement. Ici, le test exécute le vrai code, et le bug saute aux yeux. **C'est ça, la différence entre tester du câblage et tester du comportement.**

#### 📎 Annexe — trade-offs pratiques

- Lisibilité ? On lit **du métier** sans bruit technique.
- Coût d'entrée : il fallait des interfaces. Mais ce sont **les mêmes interfaces qu'on poserait pour faire de l'archi hexagonale**.
- Question ouverte : quand un test échoue ici, le diagnostic est-il plus facile ou plus difficile ?

---

## 9. Débrief final (~8 min)

🎬 **Slide 11 — "Débrief"**

### Questions de synthèse (issues de la version originale)

- Quelle partie vous a semblé **la plus naturelle** ?
- Dans laquelle avez-vous **le plus confiance** pour détecter une vraie régression ?
- Laquelle **survivrait le mieux** à un refactoring du code de prod ?
- Laquelle **reliriez-vous dans 6 mois** en comprenant immédiatement ce qui est testé ?

### Questions étendues (à choisir selon le temps restant)

> Ces questions sont des **extensions** aux 4 questions de base. À piocher selon la dynamique de la salle.

- Si vous deviez **démarrer un projet demain**, quelle approche choisiriez-vous par défaut ? Pourquoi ?
- Si vous **rejoigniez une base existante** où les tests sont en style Part 2, quel serait le **coût** de la faire évoluer vers Part 3 ou Part 4 ?
- Dans Part 4, **quand un test échoue**, le diagnostic est-il plus facile ou plus difficile qu'en Part 1/2/3 ? Pourquoi ?
- Pour les **adapters externes** (vraie DB, vraie API, vrai bus), où mettriez-vous le curseur ? (les tests d'intégration ne disparaissent pas — ils ciblent ces adapters)
- **La question titre** : c'est quoi un « unit » pour vous, **maintenant** ?

### Conclusion à exprimer

- Notre parti pris : **unit = comportement** (use case), pas méthode, pas classe.
- Mais le vrai message est plus important que la conclusion : **questionnez ce que vous mockez**. Chaque mock est une dette qu'on paie au moment de refactorer.
- Si vous repartez avec **un seul réflexe** : avant de mocker quelque chose, demandez-vous *« pourquoi ne pas l'utiliser pour de vrai ? »*

---

## Annexes

### Mapping slides ↔ sections

| Slide | Section du guide | Quand |
|---|---|---|
| Slide 1 — Intro « The Unit Question » | §1 | Discours d'intro |
| Slide 2 — Conseils pratiques | §1 (fin) | Pendant qu'on insiste sur les conseils |
| Slide 3 — Cover Partie 1 | §2 (présentation) | Pendant les ~3 min de présentation |
| Slide 4 — Mémo Exo Partie 1 | §2 (exo) | Reste affichée pendant les ~10 min d'exo |
| Slide 5 — Cover Partie 2 | §4 (présentation) | Pendant les ~2 min de présentation |
| Slide 6 — Mémo Exo Partie 2 | §4 (exo) | Reste affichée pendant l'exo |
| Slide 7 — Cover Partie 3 | §6 (présentation) | Pendant les ~2 min de présentation |
| Slide 8 — Mémo Exo Partie 3 | §6 (exo) | Reste affichée pendant l'exo |
| Slide 9 — Cover Partie 4 | §8 (présentation) | Pendant les ~3 min de présentation |
| Slide 10 — Mémo Exo Partie 4 | §8 (exo) | Reste affichée pendant l'exo |
| Slide 11 — Débrief | §9 | Questions de synthèse + extensions |

> 💡 **Transitions P1→P2, P2→P3, P3→P4** : pas de slide, purement oral (la cover de la partie suivante prend le relais).

### Anti-patterns à éviter pendant l'animation

- ❌ **Ne pas conclure trop tôt** « voilà la bonne approche » — laisser émerger
- ❌ **Ne pas sauter le code walkthrough** en intro — c'est le repère commun pour tout l'atelier
- ❌ **Ne pas oublier la note positive sur la Part 2** (« ce que beaucoup de gens compétents font ») — ça évite de braquer ceux qui s'y reconnaissent
- ❌ **Ne pas dépasser sur la Part 1** : si on déborde, c'est la Part 4 qui en pâtit — or c'est elle, la destination
- ❌ **Ne pas répondre à la place de la salle** sur les questions de débrief — silence inconfortable = bonne chose
