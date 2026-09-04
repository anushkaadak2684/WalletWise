# 💳 WalletWise - Digital Wallet & Personal Finance Tracker

A full-featured Desktop Application built in **Java 17 (Swing)** and **MySQL** for managing digital wallets, tracking expenses, establishing budgets, setting savings goals, and generating comprehensive financial reports. Features a modern dark UI powered by **FlatLaf**, BCrypt password security, and gamified reward tracking.

---

## 🌟 Key Features

### 🏦 Wallet Management
* **Wallet Types**: Supports **Personal Wallet** (monthly spending limits) and **Business Wallet** (per-transaction limits).
* **Live Balance & Metrics**: Dynamic visual utilization bars, real-time balance updates, and status indicators (`SAFE` vs `LIMIT EXCEEDED`).
* **Subtype Polymorphism**: Dynamic method dispatch evaluates spending limit thresholds polymorphically without type inspection (`instanceof`).

### 💳 Transactions (Deposits & Withdrawals)
* Record deposits (income) and withdrawals (expenses/transfers) with custom descriptions.
* Real-time activity history and automated system notification logging.
* **Atomic JDBC Transactions**: Balance updates and ledger entries execute in atomic transactions (`commit`/`rollback`) with full data integrity.

### 💸 Expense Tracking & Category Analytics
* **Expense Classification**: Differentiates between **Fixed Expenses** (recurring bills, rent) and **Variable Expenses** (groceries, entertainment).
* **Category Breakdown**: Categorize expenses across `Food`, `Rent`, `Utilities`, `Entertainment`, `Shopping`, `Healthcare`, and `Other`.
* **Visual Pie Chart**: Custom Swing component rendering interactive expense category distribution in `WalletPanel`.

### 📈 Category Budgeting
* Define category spending limits with start and end dates.
* Real-time progress monitoring comparing limit vs actual spent (`ON TRACK` vs `EXCEEDED`).

### 🎯 Savings Goals & Gamified Rewards
* Establish target savings goals with target dates.
* **Goal Contributions**: Deposit funds directly into savings goals from your active wallet balance in an atomic transaction.
* **Gamification**: Earn **+100 Reward Points** and system achievement notifications automatically upon completing a savings goal via the Observer Pattern.

### 📊 Comprehensive Financial Reports
* Generate periodical reports (**Monthly**, **Yearly**, **Custom**).
* **Itemized Side-Pane Viewer**: Synchronized dual-pane view rendering detailed HTML breakdowns (income, expenses, budgets, savings) directly in the side panel.

---

## 📐 Software Design & Architecture Principles

### 🧩 1. Object-Oriented Programming (OOP) Concepts
* **Abstraction**: Abstract base classes `Wallet` and `Expense` define essential domain behaviors while hiding internal implementation details. Public interfaces define strict subsystem boundaries.
* **Inheritance**: Class hierarchies like `PersonalWallet` and `BusinessWallet` (extending `Wallet`), as well as `FixedExpense` and `VariableExpense` (extending `Expense`), promote clean code reuse and specialization.
* **Polymorphism**: Dynamic method dispatch (`isLimitExceeded()`, `calculateTransactionLimit()`, `getLimitWarningMessage()`) allows uniform processing of specialized wallet instances and expense classifications without `instanceof` conditional branching.
* **Encapsulation**: Strict private member scoping across domain entities (`User`, `Wallet`, `Expense`, etc.) protected with validation routines and defensive copying in collection getters (`getTransactions()`, `getExpenses()`).
* **Composition**: Favors object composition (`User` *has-a* `Wallet`, `Wallet` *has-a* list of `Transaction` and `Expense` records).

### 🏛️ 2. SOLID Design Principles
* **Single Responsibility Principle (SRP)**: Strict separation of concerns between data entities (`model`), data persistence (`repository`), business rules & transactions (`service`), event subscribers (`observer`), and UI components (`gui`).
* **Open/Closed Principle (OCP)**: Modular architecture allows adding new wallet types or expense classifications by extending base classes without modifying core service or repository logic.
* **Liskov Substitution Principle (LSP)**: Derived classes (`PersonalWallet`, `BusinessWallet`) seamlessly substitute base `Wallet` instances across all services and reports without unexpected behavior.
* **Interface Segregation Principle (ISP)**: Fine-grained repository contracts isolated inside `repository.interfaces` (`IUserRepository`, `IWalletRepository`, `ITransactionRepository`, `IExpenseRepository`, `IBudgetRepository`, `ISavingsGoalRepository`, `INotificationRepository`, `IRewardRepository`, `IReportRepository`).
* **Dependency Inversion Principle (DIP)**: Service classes depend directly on interface abstractions rather than tight coupling to concrete MySQL repository implementations.

### 🎨 3. Design Patterns
* **Observer Pattern**: `WalletEventListener` along with `NotificationObserver` and `RewardObserver` reacts synchronously to wallet transactions and completed savings milestones, completely decoupling side-effects from core business logic.
* **Repository Pattern**: Decouples domain entities and services from low-level JDBC SQL queries while managing connection-aware transaction boundaries.

---

## 🔒 Security & Data Integrity

* **BCrypt Password Hashing**: Passwords are encrypted using salted BCrypt hashes (cost factor 12) via `at.favre.lib:bcrypt:0.10.2`.
* **Zero-Downtime Migration**: `UserService.authenticate()` automatically detects legacy plaintext records upon login, verifies them, and transparently upgrades them to BCrypt hashes in the database.
* **Atomic Transactions**: Multi-table operations (`deposit`, `withdraw`, `addExpense`, `contributeToSavingsGoal`) use `conn.setAutoCommit(false)`, `commit()`, and `rollback()` on failure.

---

## 🛠️ Technology Stack

* **Language**: Java 17+
* **Build System**: Apache Maven
* **GUI Framework**: Java Swing with FlatLaf (FlatDarkLaf)
* **Database**: MySQL 8.0+
* **JDBC Driver**: MySQL Connector/J
* **Security**: Favre BCrypt 0.10.2
* **Architecture**: Layered Architecture (Model/Enums - Repository/Interfaces - Service - Observer - GUI)

---

## 📁 Project Structure

```
WalletWise/
├── database/
│   └── schema.sql                  # Full MySQL database schema definition
├── lib/
│   ├── flatlaf-3.5.2.jar           # FlatLaf dark look-and-feel library
│   ├── bcrypt-0.10.2.jar           # BCrypt security library
│   ├── bytes-1.5.0.jar             # Byte array utility for BCrypt
│   └── mysql-connector-j-26.7.0.jar# MySQL JDBC Database driver
├── pom.xml                         # Maven build descriptor & dependencies
├── src/
│   └── main/java/
│       ├── gui/                    # Swing GUI Components & Main Dashboard
│       │   ├── Main.java           # Application Entrypoint
│       │   ├── MainFrame.java      # Main Dashboard Window
│       │   ├── LoginRegisterFrame.java # Auth Window
│       │   ├── WalletPanel.java    # Wallet Metrics & Pie Chart
│       │   ├── ExpensePanel.java   # Expense Logging & History
│       │   ├── BudgetPanel.java    # Budget Tracking
│       │   ├── SavingsPanel.java   # Savings Goals & Contributions
│       │   ├── NotificationPanel.java # Notifications Center
│       │   ├── RewardPanel.java    # Points & Achievements
│       │   ├── ReportPanel.java    # Financial Reports & Side-Pane Viewer
│       │   ├── Theme.java          # Look and Feel Styling
│       │   └── UIHelper.java       # Reusable UI Helpers & Dialogs
│       ├── model/                  # Domain Entities (User, Wallet, Expense, etc.)
│       │   └── enums/              # Enum Definitions (WalletType, ExpenseCategory, etc.)
│       ├── observer/               # Event Observers (NotificationObserver, RewardObserver)
│       ├── repository/             # Concrete Repository Implementations (MySQL queries)
│       │   └── interfaces/         # Repository Interfaces (IUserRepository, IWalletRepository, etc.)
│       ├── service/                # Business Logic & JDBC Transaction Management
│       └── util/                   # Utilities (DBConnection)
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
Launch via Maven:

```bash
mvn compile exec:java -Dexec.mainClass="gui.Main"
```

Or open the project in your preferred Java IDE (such as VS Code, IntelliJ IDEA, or Eclipse) and run `src/main/java/gui/Main.java`.
