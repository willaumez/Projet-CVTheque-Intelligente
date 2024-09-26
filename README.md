# CVthèque Intelligente

## Présentation du Projet

La CVthèque intelligente est une plateforme web conçue pour gérer et qualifier des CV à l'aide de l'intelligence artificielle. Ce projet a été développé dans le cadre de mon stage chez Renault Digital Maroc et constitue une solution innovante pour le recrutement, facilitant ainsi la mise en relation entre les candidats et les recruteurs.

## Objectif

L'objectif principal de ce projet est de créer un système efficace pour la gestion des candidatures, permettant aux utilisateurs de :

- Soumettre leur CV en ligne.
- Être évalués automatiquement grâce à des algorithmes d'intelligence artificielle.
- Recevoir des recommandations basées sur les compétences et les expériences professionnelles.

## Technologies Utilisées

- **Backend :**
  - Java avec Spring Boot pour la création de l'API RESTful.
  - Base de données PostgreSQL pour la gestion des données.
  
- **Frontend :**
  - Angular pour le développement de l'interface utilisateur.
  
- **Authentification :**
  - Okta pour sécuriser l'accès à la plateforme.

- **Intelligence Artificielle :**
  - Algorithmes de Machine Learning pour la qualification des CV.

## Fonctionnalités

### Utilisateurs

- **Candidats :**
  - Inscription et connexion sécurisée.
  - Téléchargement et gestion de leur CV.
  - Accès à des recommandations d'emploi personnalisées.

- **Recruteurs :**
  - Création de profils d'entreprise.
  - Consultation des CVs des candidats.
  - Filtrage des CVs selon des critères spécifiques.

### Administration

- Gestion des utilisateurs (candidats et recruteurs).
- Suivi des candidatures et des statistiques d'utilisation de la plateforme.

## Installation

Pour installer et exécuter le projet sur votre machine locale, veuillez suivre les étapes ci-dessous :

1. Clonez le dépôt :
   ```bash
   git clone https://github.com/votre-utilisateur/cvtheque-intelligente.git
   ```

2. Accédez au répertoire du projet :
   ```bash
   cd cvtheque-intelligente
   ```

3. Configurez votre environnement (Java, PostgreSQL, etc.).

4. Exécutez le backend :
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

5. Exécutez le frontend :
   ```bash
   cd frontend
   npm install
   ng serve
   ```

6. Ouvrez votre navigateur à l'adresse `http://localhost:4200`.

## Contribuer

Les contributions sont les bienvenues ! Si vous souhaitez contribuer à ce projet, veuillez suivre les étapes suivantes :

1. Forkez le projet.
2. Créez une nouvelle branche :
   ```bash
   git checkout -b feature/nom-de-la-fonctionnalité
   ```
3. Effectuez vos modifications et commit :
   ```bash
   git commit -m 'Ajout d'une nouvelle fonctionnalité'
   ```
4. Poussez vos changements :
   ```bash
   git push origin feature/nom-de-la-fonctionnalité
   ```
5. Ouvrez une Pull Request.

## Auteurs

- **Votre Nom** - [Votre Profil GitHub](https://github.com/votre-utilisateur)

## Licence

Ce projet est sous licence MIT - voir le fichier [LICENSE](LICENSE) pour plus de détails.

## Remerciements

Merci à tous ceux qui ont contribué à ce projet, ainsi qu'à mes encadrants chez Renault Digital Maroc pour leur soutien et leurs conseils.
