# 🍽️ FoodCircle

**A food discovery platform that uses repeat-order behavior to help validate restaurant and dish ratings.**

FoodCircle explores the idea that a rating becomes more meaningful when a user returns to the same restaurant or orders the same dish again. The project uses order history and repeat-order patterns to understand genuine food preferences.

## 🚀 Features

- **User Management** — Store and retrieve user information.
- **Restaurant Management** — Maintain restaurant records.
- **Dish Management** — Manage dishes independently of restaurants.
- **Order Tracking** — Record users, dishes, restaurants, timestamps, and ratings.
- **Restaurant-Based Matching (RBM)** — Analyze order history to identify repeat-order patterns.
- **Search APIs** — Support search-related operations for users, restaurants, and dishes.
- **Frontend Interface** — React-based interface with login, registration, order creation, and search screens.

## 🧠 Core Concept: Restaurant-Based Matching (RBM)

Traditional food ratings mainly rely on what users say about a restaurant or dish. FoodCircle also considers what users do afterward.

By analyzing repeat orders and restaurant visits recorded in the system, FoodCircle aims to identify patterns that indicate genuine interest in particular restaurants and dishes.

The goal is to make food ratings more meaningful by considering behavior alongside stated preferences.

## 🛠️ Tech Stack

**Backend**
- Java 21
- Spring Boot
- Spring Data JPA
- REST APIs
- Maven

**Database**
- PostgreSQL
- SQL

**Frontend**
- React
- Vite
- JavaScript
- CSS

**Tools**
- Git and GitHub
- Postman
- pgAdmin

## 📁 Project Structure

```text
FoodCircle/
├── src/
│   ├── main/
│   │   ├── java/com/foodcircle/foodcircle/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── entity/
│   │   │   └── repository/
│   │   └── resources/
│   └── test/
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   └── assets/
│   ├── package.json
│   └── ...
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## ⚙️ Getting Started

### Prerequisites

- Java JDK 21
- PostgreSQL
- Node.js and npm
- Git

### 1. Clone the repository

```bash
git clone https://github.com/jainishaan11/FoodCircle.git
cd FoodCircle
```

### 2. Set up the database

Create a PostgreSQL database named `foodcircle`.

Configure the database connection in `src/main/resources/application.properties`. Set the `DB_PASSWORD` environment variable to your local PostgreSQL password. Do not commit real passwords or other credentials to GitHub.

For Git Bash:

```bash
read -s -p "Enter DB password: " DB_PASSWORD
echo
export DB_PASSWORD
```

### 3. Run the backend

From the project root:

```bash
./mvnw spring-boot:run
```

On Windows Command Prompt:

```bat
mvnw.cmd spring-boot:run
```

The backend is expected to run on `http://localhost:8080`, provided the database configuration is correct and the port is available.

### 4. Run the frontend

Open a separate terminal:

```bash
cd frontend
npm install
npm run dev
```

Open the local URL displayed by Vite.

> Note: Frontend-to-backend integration may require additional configuration. Some frontend interactions are still under development.

## 🔮 Future Improvements

- Connect frontend components to backend REST APIs.
- Improve rating validation using repeat-order history.
- Add friends, followers, and taste-matching features.
- Expand restaurant and dish recommendations.
- Strengthen validation, error handling, and testing.

## 🎯 Project Objective

FoodCircle is a project focused on backend development, relational data modelling, REST API design, SQL, and analyzing user behavior through order history.

It is being developed incrementally, starting with core functionality and leaving advanced social and recommendation features for later.

## 🔗 Repository

[GitHub — FoodCircle](https://github.com/jainishaan11/FoodCircle)
