
# CVthèque Intelligente

## Description du Projet
La CVthèque Intelligente est une plateforme web innovante développée pour centraliser et automatiser la gestion des CVs. Grâce à l'intégration de l'intelligence artificielle, ce projet permet d'analyser les compétences des candidats et de recommander les profils les plus pertinents pour les postes proposés. Il s'appuie sur la méthode RAG (Retrieval-Augmented Generation) pour améliorer la précision de l'évaluation des CVs.

Ce projet a été réalisé dans le cadre de mon stage de fin d'études chez Renault Digital Maroc (RDM) en vue de répondre aux besoins croissants en matière de modernisation des processus de recrutement.

## Objectifs
L'objectif principal du projet est de :
- Centraliser la gestion des CVs.
- Automatiser la qualification des candidatures via l'intelligence artificielle.
- Fournir des recommandations de candidats en adéquation avec les postes à pourvoir.

## Fonctionnalités Principales
### Utilisateurs
- **Recruteurs** :
  - Gestion des dossiers et des CVs.
  - Qualification automatique des CVs à partir de critères prédéfinis (ex. compétences, expérience).
  - Recherche avancée par mots-clés.
  - Système de scoring pour les CVs.
### Administrateurs
  - Gestion des utilisateurs.
  - Contrôle des critères de qualification et des scores.
### Sécurité
  - Gestion sécurisée des CVs et des informations personnelles.
  - Authentification via Okta.

## Technologies Utilisées
- **Backend** : Java, Spring Boot, Spring AI.
- **Frontend** : Angular, Angular Material.
- **Authentification** : Okta.
- **Intelligence Artificielle** : Méthode RAG (Retrieval-Augmented Generation), LLM (Large Language Models).
- **Base de Données** : Oracle 23c AI pour le stockage des documents et des données vectorielles.
- **Conteneurisation** : Docker

## Prérequis
- [Java JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) ou supérieur
- [Node.js et npm](https://nodejs.org/en/download/)
- [Docker](https://www.docker.com/products/docker-desktop)
- [Git](https://git-scm.com/downloads)

## Installation et Configuration

### 1. Cloner le dépôt
```bash
git clone https://github.com/willaumez/Projet-CVTheque-Intelligente.git
cd Projet-CVTheque-Intelligente
```

### 2. Configurer la base de données
Tirez l'image Docker de la base de données :
```bash
docker pull willaumez/databasecvtheque
```

Lancez le conteneur de la base de données :
```bash
docker run -d -p 1521:1521 --name cvtheque-db willaumez/databasecvtheque
```

### 3. Configurer et lancer le backend
Avant de lancer le backend, assurez-vous que votre fichier de configuration (généralement `application.properties` ou `application.yml`) contient les informations suivantes pour la connexion à la base de données :

```properties
# Configuration de la base de données
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/FREEPDB1
spring.datasource.username=admin
spring.datasource.password=admin
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
```

Ensuite, lancez le backend :

```bash
cd cvtheque-spring-boot
./mvnw install
./mvnw spring-boot:run
```

### 4. Configurer et lancer le frontend
```bash
cd cvtheque-front-angular
npm install
ng serve
```

L'application sera accessible à l'adresse : http://localhost:4200

--------------------------------------------------------------------------

## Utilisation

1. Connectez-vous à l'application en utilisant vos identifiants Okta.
2. Pour les recruteurs :
   - Téléchargez et gérez les CVs dans l'interface.
   - Utilisez la fonction de recherche avancée pour trouver des candidats spécifiques.
   - Consultez les scores et recommandations générés par l'IA pour chaque CV.
3. Pour les administrateurs :
   - Gérez les comptes utilisateurs et les permissions.
   - Ajustez les critères de qualification et les paramètres de scoring si nécessaire.

## Méthode RAG

La méthode RAG est utilisée pour analyser et qualifier les CVs. Elle combine la recherche documentaire et les modèles génératifs pour fournir des résultats précis en fonction des données disponibles dans la base de données. L'IA évalue automatiquement les CVs selon les critères prédéfinis et attribue un score à chaque candidat.

## Contribuer

Les contributions sont les bienvenues. Pour contribuer :
1. Forkez le projet.
2. Créez une nouvelle branche (`git checkout -b feature/nouvelle-fonctionnalité`).
3. Effectuez les modifications.
4. Commitez vos changements (`git commit -am 'Ajout d'une nouvelle fonctionnalité'`).
5. Poussez vers la branche (`git push origin feature/nouvelle-fonctionnalité`).
6. Soumettez une Pull Request.

## Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

## Remerciements

Je remercie particulièrement mon encadrant professionnel, Yassine TALEB, ainsi que mon encadrant pédagogique, Badr HIRCHOUA, pour leur soutien tout au long de ce projet. Merci également à l'équipe de Renault Digital Maroc pour leur collaboration.
