# 💰 Personal Budgeting Desktop Application

A modern **desktop financial management system** built using **JavaFX** and **Microsoft SQL Server**, designed to help users efficiently track income, expenses, budgets, and financial goals.

The application follows the **MVC (Model-View-Controller)** architecture, ensuring clean code organization, scalability, and maintainability.

---

## ✨ Features

### 📊 Dashboard

* Real-time overview of **balance, income, and expenses**
* Summary cards for transactions, budgets, and goals
* Recent activity table for quick tracking

### 💸 Budget Management

* Create budgets with custom limits and date ranges
* Set alert thresholds (e.g., 80%) to avoid overspending
* Visual progress tracking using dynamic progress bars

### 🎯 Financial Goals

* Define savings goals with target amounts and deadlines
* Automatic progress evaluation:

  * On Track
  * Behind Schedule
  * Achieved
* Control actions: pause, resume, or cancel goals

### 📈 Reports & Analytics

* Pie charts for expense distribution by category
* Bar charts for income vs. expenses comparison
* Automated insights based on spending behavior

### 🔔 Notifications

* Real-time alerts for:

  * Budget limits
  * Goal milestones
  * Important financial events
* Read/unread notification tracking system

---

## 🛠️ Tech Stack

* **Language:** Java (JDK 21+)
* **UI:** JavaFX (FXML + Scene Builder)
* **Database:** Microsoft SQL Server
* **Architecture:** MVC Pattern
* **Build Tool:** Maven

---

## ⚙️ Installation & Setup

### 1. Database Setup

* Create a database named:

```sql id="db1"
PersonalBudgetDB
```

* Create required tables:

  * Users
  * Transactions
  * Budgets
  * Goals
  * Notifications

* Update Budgets table:

```sql id="db2"
ALTER TABLE Budgets ADD SpentAmount FLOAT NOT NULL DEFAULT 0.0;
```

---

### 2. Configure Application

* Open:

```
DBConnection.java
```

* Update:

  * Server name
  * Username
  * Password

---

### 3. Run the Project

```bash id="run1"
mvn clean install
mvn javafx:run
```

---

## 📁 Project Structure

```id="structure1"
Personal-Budgeting-App/
│
├── src/main/java/
│   ├── controller/     # UI logic & event handling
│   ├── database/       # DAO & database connection
│   ├── model/          # Data models
│   └── view/           # UI controllers
│
├── src/main/resources/
│   ├── view/           # FXML files
│   └── images/         # assets
│
└── pom.xml             # Maven configuration
```

---

## 👨‍💻 Contributors

* **Omar Ahmed** — Lead Developer
* Yousef Ahmed
* Farouk Mohamed
* Kerolos George

---

## 🎓 Academic Context

Developed as part of the **Software Engineering coursework** at:

**Faculty of Computers and Artificial Intelligence
Cairo University (FCAI CU)**

---

## 📌 Future Improvements

* Export reports to PDF/Excel
* Multi-user role system (Admin/User)
* Cloud synchronization
* Mobile companion app

---

## ⭐ Support

If you find this project useful, consider giving it a **star ⭐ on GitHub**.
