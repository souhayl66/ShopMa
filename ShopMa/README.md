# ShopMa — Application e-commerce Android

> Projet individuel — Développement Mobile Android  
> ENSA Kénitra, Université Ibn Tofail

---

## 📋 Description

ShopMa est une application Android marketplace e-commerce développée en **Java**. Elle permet de parcourir un catalogue de produits via la **FakeStoreAPI**, gérer un panier persistant, passer des commandes, et consulter l'historique des achats.

---

## 🚀 Installation & Configuration

### 1. Cloner / ouvrir le projet
Ouvrez le dossier `ShopMa` dans **Android Studio** (version Hedgehog ou supérieure recommandée).

### 2. Configurer la clé Google Maps

Ouvrez le fichier `secrets.properties` à la **racine du projet** et remplacez la valeur :

```properties
MAPS_API_KEY=VOTRE_CLÉ_GOOGLE_MAPS_ICI
```

Pour obtenir une clé :
1. Allez sur [Google Cloud Console](https://console.cloud.google.com/)
2. Activez **Maps SDK for Android**
3. Créez une clé API et copiez-la ici

### 3. Synchroniser Gradle
Cliquez sur **"Sync Now"** dans Android Studio.

### 4. Lancer l'application
Connectez un émulateur (API 24+) ou un téléphone physique et cliquez sur **Run**.

---

## 🔑 Identifiants de connexion (hardcodés)

| Champ   | Valeur             |
|---------|--------------------|
| Email   | `admin@shopma.ma`  |
| Mot de passe | `admin123`    |

---

## 📱 Fonctionnalités implémentées

| Fonctionnalité | Status |
|---|---|
| Authentification + validation formulaire | ✅ |
| Se rappeler de moi (SharedPreferences) | ✅ |
| Dashboard 6 cartes catégories | ✅ |
| Toolbar avec badge panier | ✅ |
| Catalogue via Retrofit + FakeStoreAPI | ✅ |
| Filtre par catégorie | ✅ |
| Panier persistant SQLite | ✅ |
| Gestion des quantités panier | ✅ |
| Calcul total panier | ✅ |
| Passage de commande | ✅ |
| Notification locale commande | ✅ |
| Historique commandes SQLite | ✅ |
| Badge statut coloré (En cours / Livrée) | ✅ |
| Recherche par catégorie + chips | ✅ |
| Google Maps + 6 points de retrait Maroc | ✅ |
| Photo de profil caméra (ACTION_IMAGE_CAPTURE) | ✅ |
| Profil sauvegardé SharedPreferences | ✅ |
| HeaderFragment réutilisable (4+ écrans) | ✅ |
| Intent implicite partage produit | ✅ |
| Permissions runtime CAMERA + LOCATION | ✅ |
| Gestion erreurs réseau (no crash) | ✅ |

---

## 🏗️ Architecture

```
com.shopma.app/
├── LoginActivity.java
├── AccueilActivity.java        ← Dashboard
├── CatalogueActivity.java      ← Produits via Retrofit
├── PanierActivity.java         ← Panier SQLite
├── RechercheActivity.java      ← Recherche par catégorie
├── CommandesActivity.java      ← Historique commandes
├── PointsRetraitActivity.java  ← Google Maps
├── ProfilActivity.java         ← Profil + caméra
├── fragments/
│   └── HeaderFragment.java     ← Fragment réutilisable
├── models/
│   ├── Product.java
│   ├── CartItem.java
│   └── Commande.java
├── adapters/
│   ├── ProductAdapter.java     ← BaseAdapter + ViewHolder
│   ├── CartAdapter.java        ← BaseAdapter + contrôles quantité
│   └── CommandeAdapter.java    ← BaseAdapter + badge coloré
├── api/
│   ├── ApiService.java         ← Interface Retrofit
│   └── RetrofitClient.java     ← Singleton Retrofit
└── database/
    └── DatabaseHelper.java     ← SQLiteOpenHelper (2 tables)
```

---

## 🗄️ Base de données SQLite

**Table `panier`** : `id`, `product_id`, `title`, `price`, `quantity`  
**Table `commandes`** : `id`, `date`, `nb_articles`, `montant_total`, `statut`

---

## 🌐 API utilisée

**FakeStoreAPI** — `https://fakestoreapi.com`

| Endpoint | Usage |
|---|---|
| `GET /products` | Tous les produits |
| `GET /products/categories` | Liste des catégories |
| `GET /products/category/{cat}` | Produits par catégorie |
| `GET /products/{id}` | Un produit par ID |

---

## 📦 Dépendances principales

- **Retrofit 2.9.0** + Gson — appels API REST
- **Glide 4.16.0** — chargement d'images
- **Google Maps SDK 18.2.0** — carte interactive
- **Material Components 1.11.0** — UI (Chips, Buttons...)
- **OkHttp Logging Interceptor** — debug réseau
