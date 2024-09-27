
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



Installation
Prérequis
Avant de commencer, assurez-vous d'avoir installé les éléments suivants :

Git
Node.js et npm (pour le frontend Angular)
Java JDK 17 (pour le backend Spring Boot)
Docker (pour la base de données)
Étapes d'Installation
Configurer et Lancer la Base de Données avec Docker :

La base de données utilisée dans ce projet est stockée dans un container Docker. Commencez par récupérer l'image de la base de données à partir de Docker Hub avec la commande suivante :

bash
Copier le code
docker pull willaumez/databasecvtheque
Ensuite, lancez le container Docker qui contient la base de données Oracle, en exposant le port 1521 :

bash
Copier le code
docker run -d -p 1521:1521 --name databasecvtheque willaumez/databasecvtheque
Cela démarre la base de données Oracle contenue dans le container et la rend accessible sur le port 1521. Assurez-vous que le container est bien en cours d'exécution avant de continuer.

Cloner le dépôt :

Clonez le projet sur votre machine locale à partir de GitHub.

bash
Copier le code
git clone https://github.com/willaumez/Projet-CVTheque-Intelligente.git
Installer les dépendances du Backend :

Allez dans le répertoire backend et installez les dépendances Java nécessaires à l'exécution du backend Spring Boot.

bash
Copier le code
cd backend
./mvnw install
Configurer la Connexion à la Base de Données dans le Backend :

Assurez-vous que le fichier application.properties dans le répertoire backend/src/main/resources est bien configuré pour se connecter à la base de données. Modifiez les paramètres de connexion si nécessaire (URL, nom d'utilisateur, mot de passe). L'URL devrait ressembler à ceci :

properties
Copier le code
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=nom_utilisateur
spring.datasource.password=mot_de_passe
Démarrer le Backend :

Une fois la configuration correcte, démarrez le backend Spring Boot.

bash
Copier le code
./mvnw spring-boot:run
Installer les dépendances du Frontend :

Allez dans le répertoire frontend, installez les dépendances nécessaires, puis démarrez l'application Angular.

bash
Copier le code
cd ../frontend
npm install
ng serve
L'application sera accessible sur http://localhost:4200.

Méthode RAG
La méthode RAG (Retrieval-Augmented Generation) est utilisée pour analyser et qualifier les CVs. Elle permet d'effectuer une recherche documentaire tout en utilisant des modèles génératifs pour fournir des résultats précis basés sur les données contenues dans la base de données. L'intelligence artificielle attribue automatiquement un score aux CVs en fonction des critères prédéfinis, ce qui permet de recommander les candidats les plus adaptés.


=====================================================================================================================================================================================
Remerciements
Je remercie particulièrement mon encadrant professionnel, Yassine TALEB, ainsi que mon encadrant pédagogique, Badr HIRCHOUA, pour leur soutien tout au long de ce projet. Merci également à l'équipe de Renault Digital Maroc pour leur collaboration.
