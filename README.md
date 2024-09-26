
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

## Installation

1. Clonez le dépôt :
   ```bash
   git clone https://github.com/willaumez/Projet-CVTheque-Intelligente.git
Installez les dépendances du backend et du frontend :

Backend :
bash
Copier le code
cd backend
./mvnw install
./mvnw spring-boot:run
Frontend :
bash
Copier le code
cd frontend
npm install
ng serve
Accédez à l'application sur http://localhost:4200.

Méthode RAG
La méthode RAG est utilisée pour analyser et qualifier les CVs. Elle combine la recherche documentaire et les modèles génératifs pour fournir des résultats précis en fonction des données disponibles dans la base de données. L'IA évalue automatiquement les CVs selon les critères prédéfinis et attribue un score à chaque candidat.

Contribuer
Les contributions sont les bienvenues. Pour contribuer :

Forkez le projet.
Créez une nouvelle branche (feature/nouvelle-fonctionnalité).
Effectuez les modifications.
Soumettez une Pull Request.
Licence
Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

Remerciements
Je remercie particulièrement mon encadrant professionnel, Yassine TALEB, ainsi que mon encadrant pédagogique, Badr HIRCHOUA, pour leur soutien tout au long de ce projet. Merci également à l'équipe de Renault Digital Maroc pour leur collaboration.
