
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

#### Configuration de la base de données et de l'API OpenAI
Les configurations de la base de données et de l'API OpenAI sont déjà incluses dans le code du projet. Vous pouvez les trouver dans le fichier `application.properties` ou `application.yml`. Voici un rappel de ces configurations :

```properties
# Configuration de la base de données
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/FREEPDB1
spring.datasource.username=admin
spring.datasource.password=admin
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# Configuration de OpenAI
spring.ai.openai.api-key=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
spring.ai.openai.chat.enabled=true
spring.ai.openai.chat.options.model=gpt-4
```

**Note importante**: L'API key fournie a un solde limité. Elle est suffisante pour quelques tests, mais pour une utilisation prolongée, vous devrez la remplacer par votre propre API key.

##### Changer l'API key
Pour changer l'API key :
1. Allez sur [https://platform.openai.com/api-keys](https://platform.openai.com/api-keys)
2. Créez une nouvelle clé ou copiez une clé existante
3. Remplacez la valeur de `spring.ai.openai.api-key` dans le fichier de configuration avec votre nouvelle clé

##### Créer une nouvelle API key OpenAI
Si vous souhaitez créer votre propre API key pour une utilisation à long terme :

1. Rendez-vous sur [OpenAI API](https://platform.openai.com/signup)
2. Créez un compte ou connectez-vous
3. Allez dans la section "API Keys"
4. Cliquez sur "Create new secret key"
5. Copiez la clé générée et remplacez-la dans votre fichier de configuration

##### Vérifier le solde de votre API key
Pour vérifier le solde restant sur votre API key :
1. Allez sur [https://platform.openai.com/settings/organization/billing/overview](https://platform.openai.com/settings/organization/billing/overview)
2. Vous y trouverez les informations sur votre utilisation actuelle et votre solde restant

**Attention** : L'utilisation de l'API OpenAI est payante. Assurez-vous de comprendre la tarification avant de l'utiliser.

#### Lancer le backend
Une fois que vous avez vérifié les configurations, lancez le backend :

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


#### Passer d'OpenAI à Ollama (LLM open source)

Si vous préférez utiliser un modèle de langage open source au lieu d'OpenAI, vous pouvez passer à Ollama. Voici comment procéder :

1. Modifier le fichier `pom.xml` :
   Remplacez la dépendance OpenAI :
   ```xml
   <dependency>
       <groupId>org.springframework.ai</groupId>
       <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
   </dependency>
   ```
   par la dépendance Ollama (déjà commentée au-dessus) :
   ```xml
   <dependency>
       <groupId>org.springframework.ai</groupId>
       <artifactId>spring-ai-ollama-spring-boot-starter</artifactId>
   </dependency>
   ```

2. Modifier le fichier `src/main/resources/application.properties` :
   Remplacez la configuration OpenAI :
   ```properties
   # Configuration de OpenAI
   spring.ai.openai.api-key=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
   spring.ai.openai.chat.enabled=true
   spring.ai.openai.chat.options.model=gpt-4
   ```
   par la configuration Ollama (déjà commentée au-dessus) :
   ```properties
   spring.ai.ollama.base-url=http://localhost:11434
   spring.ai.ollama.chat.enabled=true
   spring.ai.ollama.chat.options.model=llama3
   #spring.ai.ollama.chat.options.format=JSON
   spring.ai.ollama.embedding.enabled=true
   spring.ai.ollama.embedding.model=llama3
   ```

#### Installation et configuration d'Ollama

1. Installer Ollama :
   - Rendez-vous sur [le site officiel d'Ollama](https://ollama.ai/download) pour télécharger et installer Ollama pour votre système d'exploitation.

2. Installer les modèles recommandés :
   
   a. Llama3 70B :
   ```bash
   ollama run llama3:70b
   ```
   Pour plus d'informations sur ce modèle : [Llama3 70B](https://ollama.com/library/llama3:70b)

   b. Command-R :
   ```bash
   ollama run command-r
   ```
   Pour plus d'informations sur ce modèle : [Command-R](https://ollama.com/library/command-r)

   c. Command-R-Plus :
   ```bash
   ollama run command-r-plus
   ```
   Pour plus d'informations sur ce modèle : [Command-R-Plus](https://ollama.com/library/command-r-plus)

3. Changer le modèle dans la configuration :
   - Dans le fichier `application.properties`, modifiez la ligne :
     ```properties
     spring.ai.ollama.chat.options.model=llama3
     ```
   - Remplacez `llama3` par le nom du modèle que vous souhaitez utiliser (par exemple, `llama3:70b`, `command-r`, ou `command-r-plus`).

**Note** : Assurez-vous que le serveur Ollama est en cours d'exécution lorsque vous lancez l'application. Le téléchargement et l'installation de ces modèles peuvent prendre un certain temps en fonction de votre connexion internet et des ressources de votre machine.

--------------------------------------------------------------------------

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
