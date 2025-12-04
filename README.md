# 🏨 Système de Gestion d'Hôtel

Application de gestion hôtelière développée en **JavaFX** avec base de données **MySQL**.

## 📋 Description

Ce projet permet de gérer les opérations quotidiennes d'un hôtel :
- Gestion des chambres (ajout, modification, suppression)
- Gestion des clients (CRUD + recherche)
- Gestion des réservations (création, annulation, check-out)
- Dashboard avec statistiques en temps réel

## 🛠️ Technologies Utilisées

| Technologie | Version | Description |
|-------------|---------|-------------|
| Java | 17+ | Langage principal |
| JavaFX | 17 | Interface graphique |
| Maven | 3.x | Gestion des dépendances |
| MySQL | 8.x | Base de données |
| JDBC | - | Connexion Java ↔ MySQL |

## 📁 Structure du Projet

```
JavaGestionHotel/
├── src/
│   ├── dao/                          # Data Access Object
│   │   ├── ChambreDAO.java
│   │   ├── ClientDAO.java
│   │   ├── ReservationDAO.java
│   │   └── ServiceDAO.java
│   ├── models/                       # Entités métier
│   │   ├── Chambre.java
│   │   ├── Client.java
│   │   ├── Reservation.java
│   │   └── Service.java
│   ├── ui/                           # Interface utilisateur
│   │   ├── MainApp.java              # Point d'entrée + Dashboard
│   │   ├── ChambresView.java         # Vue gestion chambres
│   │   ├── ClientsView.java          # Vue gestion clients
│   │   └── ReservationsView.java     # Vue gestion réservations
        └──StatistiquesView.java     # vue sur les statistiques 
│   ├── utils/
│   │   └── DatabaseConnection.java   # Connexion MySQL
│   └── module-info.java
├── pom.xml                           # Configuration Maven
└── README.md
```

## 🗄️ Base de Données

### Configuration
- **Host:** localhost
- **Port:** 3306
- **Database:** gestion_hotel
- **User:** root
- **Password:** (vide)

### Script SQL
```sql
CREATE DATABASE IF NOT EXISTS gestion_hotel;
USE gestion_hotel;

-- Table des chambres
CREATE TABLE chambres (
    id INT PRIMARY KEY AUTO_INCREMENT,
    numero VARCHAR(10) NOT NULL,
    type VARCHAR(50),
    prix DOUBLE,
    occupee BOOLEAN DEFAULT FALSE
);

-- Table des clients
CREATE TABLE clients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100),
    email VARCHAR(100),
    telephone VARCHAR(20)
);

-- Table des réservations
CREATE TABLE reservations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    client_id INT,
    chambre_id INT,
    date_arrivee DATE,
    date_depart DATE,
    statut VARCHAR(50) DEFAULT 'En cours',
    montant_total DOUBLE,
    FOREIGN KEY (client_id) REFERENCES clients(id),
    FOREIGN KEY (chambre_id) REFERENCES chambres(id)
);

-- Table des services (optionnel)
CREATE TABLE services (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100),
    prix DOUBLE
);
```

## 🚀 Installation et Exécution

### Prérequis
- Java JDK 17 ou supérieur
- Maven 3.x
- MySQL Server 8.x
- MySQL Workbench (optionnel)

### Étapes

1. **Cloner le projet**
   ```bash
   git clone https://github.com/SonnaDiallo/Projet_java.git
   cd Projet_java
   ```

2. **Créer la base de données**
   - Ouvrir MySQL Workbench ou terminal MySQL
   - Exécuter le script SQL ci-dessus

3. **Configurer la connexion**
   - Modifier `src/utils/DatabaseConnection.java` si nécessaire :
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/gestion_hotel";
   private static final String USER = "root";
   private static final String PASSWORD = "";
   ```

4. **Compiler et lancer**
   ```bash
   mvn clean javafx:run
   ```

## 📸 Captures d'écran

### Page d'Accueil (Dashboard)
- Statistiques en temps réel (chambres, clients, réservations)
- Cartes d'accès rapide vers chaque section
- Statut des chambres (disponibles/occupées)

### Gestion des Chambres
- Tableau avec numéro, type, prix, statut
- Boutons : Ajouter, Supprimer, Voir Détails

### Gestion des Clients
- Tableau avec recherche par nom/email
- Formulaire ajout/modification

### Gestion des Réservations
- Création avec sélection client + chambre + dates
- Annulation et Check-out

## ✨ Fonctionnalités

| Fonctionnalité | Description |
|----------------|-------------|
| ✅ Dashboard | Stats dynamiques depuis la BDD |
| ✅ Gestion Chambres | CRUD complet |
| ✅ Gestion Clients | CRUD + recherche |
| ✅ Réservations | Création, annulation, check-out |
| ✅ Navigation | Par cartes colorées + bouton retour |
| ⏳ Statistiques | Graphiques et rapports (à venir) |

## 🏛️ Architecture

### Pattern DAO (Data Access Object)
Séparation de la logique d'accès aux données :
```
UI (JavaFX) → DAO → Base de données MySQL
```

**Avantages :**
- Code modulaire et maintenable
- Facilite les tests unitaires
- Changement de BDD sans modifier l'UI

### Navigation
- `TabPane` caché pour gérer les vues
- Navigation via cartes "Accès Rapide"
- Bouton "← Accueil" sur chaque page

## 👥 Équipe

- **Équipe B3** - Développement

## 📄 Licence

Projet académique - Tous droits réservés.

---

*Développé avec ❤️ en JavaFX*
