# 💳 WalletWise - Digital Wallet & Personal Finance Tracker

A full-featured Desktop Application built in **Java (Swing)** and **MySQL** for managing digital wallets, tracking expenses, establishing budgets, setting savings goals, and generating comprehensive financial reports. Features a modern dark UI powered by **FlatLaf** and gamified reward tracking.

---

## 🌟 Key Features

### 🏦 Wallet Management
* **Wallet Types**: Supports **Personal Wallet** (monthly spending limits) and **Business Wallet** (per-transaction limits).
* **Live Balance & Metrics**: Dynamic visual utilization bars, real-time balance updates, and status indicators (`SAFE` vs `LIMIT EXCEEDED`).

### 💳 Transactions (Deposits & Withdrawals)
* Record deposits (income) and withdrawals (expenses/transfers) with custom descriptions.
* Real-time activity history and automated system notification logging.

### 💸 Expense Tracking & Category Analytics
* **Expense Classification**: Differentiates between **Fixed Expenses** (recurring bills, rent) and **Variable Expenses** (groceries, entertainment).
* **Category Breakdown**: Categorize expenses across `Food`, `Rent`, `Utilities`, `Entertainment`, `Shopping`, `Healthcare`, and `Other`.
* **Visual Pie Chart**: Custom Swing component rendering interactive expense category distribution in `WalletPanel`.

### 📈 Category Budgeting
* Define category spending limits with start and end dates.
* Real-time progress monitoring comparing limit vs actual spent (`ON TRACK` vs `EXCEEDED`).

### 🎯 Savings Goals & Gamified Rewards
* Establish target savings goals with target dates.
* **Goal Contributions**: Deposit funds directly into savings goals from your active wallet balance.
* **Gamification**: Earn **+100 Reward Points** and system achievement notifications automatically upon completing a savings goal.

### 📊 Comprehensive Financial Reports
* Generate periodical reports (**Monthly**, **Yearly**, **Custom**).
* **Itemized Side-Pane Viewer**: Synchronized dual-pane view rendering detailed HTML breakdowns (income, expenses, budgets, savings) directly in the side panel.

---

## 📐 Software Design & Architecture Principles

### 🧩 1. Object-Oriented Programming (OOP) Concepts
* **Abstraction**: Abstract base classes `Wallet` and `Expense` define essential domain behaviors while hiding internal implementation details.
* **Inheritance**: Class hierarchies like `PersonalWallet` and `BusinessWallet` (extending `Wallet`), as well as `FixedExpense` and `VariableExpense` (extending `Expense`), promote code reuse.
* **Polymorphism**: Dynamic method dispatch allows uniform processing of specialized wallet instances and expense classifications.
* **Encapsulation**: Strict private member scoping in domain entities protected with validation routines and getters/setters.

### 🏛️ 2. SOLID Design Principles
* **Single Responsibility Principle (SRP)**: Strict separation of concerns between data entities (`model`), data persistence (`repository`), business rules (`service`), and UI components (`gui`).
* **Open/Closed Principle (OCP)**: Modular architecture allows adding new expense types or spending strategies without modifying core service logic.
* **Liskov Substitution Principle (LSP)**: Derived classes (`PersonalWallet`, `BusinessWallet`) can seamlessly substitute base `Wallet` instances across services.
* **Interface Segregation Principle (ISP)**: Fine-grained repository contracts isolated inside `repository.interfaces` (`IUserRepository`, `IWalletRepository`, `IBudgetRepository`, etc.).
* **Dependency Inversion Principle (DIP)**: Service classes depend directly on interface abstractions rather than tight coupling to concrete MySQL repository implementations.

### 🎨 3. Design Patterns
* **Factory Method Pattern**: `WalletFactory` and `ExpenseFactory` encapsulate object creation logic based on runtime types.
* **Strategy Pattern**: `SpendingLimitStrategy` with `PersonalLimitStrategy` and `BusinessLimitStrategy` dynamically evaluate spending limits based on wallet classification.
* **Observer Pattern**: `WalletEventListener` along with `NotificationObserver` and `RewardObserver` react asynchronously to wallet events, savings milestones, and reward triggers.
* **Repository Pattern**: Decouples domain entities and services from low-level JDBC SQL queries.

---

## 🛠️ Technology Stack

* **Language**: Java 17+
* **GUI Framework**: Java Swing with FlatLaf (FlatDarkLaf)
* **Database**: MySQL 8.0+
* **JDBC Driver**: MySQL Connector/J
* **Architecture**: Layered Architecture (Model/Enums - Repository/Interfaces - Service - GUI)

---

## 📁 Project Structure

```
WalletWise/
├── database/
│   └── schema.sql                  # Full MySQL database schema definition
├── lib/
│   ├── flatlaf-3.5.2.jar           # FlatLaf dark look-and-feel library
│   └── mysql-connector-j.jar       # MySQL JDBC Database driver
├── src/
│   ├── factory/                    # Creational Factories (WalletFactory, ExpenseFactory)
│   ├── gui/                        # Swing GUI Components & Main Dashboard
│   │   ├── Main.java               # Application Entrypoint
│   │   ├── MainFrame.java          # Main Dashboard Window
│   │   ├── LoginRegisterFrame.java # Auth Window
│   │   ├── WalletPanel.java        # Wallet Metrics & Pie Chart
│   │   ├── ReportPanel.java        # Financial Reports & Side-Pane Viewer
│   │   ├── SavingsPanel.java       # Savings Goals & Contributions
│   │   └── ...
│   ├── model/                      # Domain Entities (User, Wallet, Expense, etc.)
│   │   └── enums/                  # Enum Definitions (WalletType, ExpenseCategory, etc.)
│   ├── observer/                   # Event Observers (NotificationObserver, RewardObserver)
│   ├── repository/                 # Concrete Repository Implementations (MySQL queries)
│   │   └── interfaces/             # Repository Interfaces (IUserRepository, IWalletRepository, etc.)
│   ├── service/                    # Business Logic Services (WalletService, ReportService, etc.)
│   ├── strategy/                   # Strategy Pattern (Spending Limit Strategies)
│   ├── test/                       # Verification & Integration Tests
│   └── util/                       # Utilities (DBConnection)
├── .env                            # Local environment configuration (Git ignored)
├── .env.example                    # Environment template
├── .gitignore                      # Git ignore rules
└── README.md                       # Project Documentation
```

---

## 🚀 Getting Started

### 1. Prerequisites
* **Java Development Kit (JDK)**: Java 17 or higher.
* **MySQL Database**: MySQL Server 8.0+ running locally or remotely.

### 2. Database Setup
Execute `database/schema.sql` in your MySQL environment (Command Line or MySQL Workbench):

```bash
mysql -u root -p < database/schema.sql
```

### 3. Environment Configuration
Create a `.env` file in the project root directory (or copy from `.env.example`):

```env
# Database Credentials
DB_URL=jdbc:mysql://localhost:3306/finance_manager
DB_USER=root
DB_PASSWORD=your_mysql_password
```

### 4. Running the Application
Open the project in your preferred Java IDE (such as VS Code, IntelliJ IDEA, or Eclipse) and run `src/gui/Main.java`.
