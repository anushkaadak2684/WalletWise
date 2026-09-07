# 💳 WalletWise — Object-Oriented Digital Wallet & Personal Finance System

**WalletWise** is an end-to-end **object-oriented software system** that models a digital wallet and personal finance management use case.

The primary goal of the project is **not just the desktop interface itself**, but the design and implementation of a maintainable financial application using **object-oriented programming, SOLID principles, design patterns, persistence, transactional data management, and secure authentication**.

The current implementation uses **Java 17 + Swing (FlatLaf)** as the presentation layer and **MySQL** for persistence. The architecture is intentionally layered and decoupled so that the presentation layer can be replaced with a **web application, REST API, or another client** without fundamentally redesigning the domain and business logic.

---

## 🎯 Project Objective

WalletWise was built to demonstrate how a real-world application can be designed and implemented using **OOP principles from end to end**.

The system models common financial operations such as:

* Managing personal and business wallets with customizable spending limits
* Recording deposits, withdrawals, and categorized expenses
* Enforcing category budgets with proactive alerts (80% caution & 100% overrun)
* Managing milestone-driven savings goals with automated rewards (+100 Points)
* Tracking notifications and activity history
* Generating comprehensive financial reports with rich summary views
* Persisting application data in MySQL with zero resource leaks
* Maintaining data consistency through atomic JDBC transactions (`commit` / `rollback`)
* Securing user authentication with BCrypt password hashing and legacy account migration

The project focuses on **how these requirements are translated into objects, responsibilities, abstractions, relationships, and application layers** rather than simply implementing basic CRUD functionality.

---

## 🏗️ Architecture at a Glance

WalletWise follows a **layered, object-oriented architecture**:

```text
┌──────────────────────────────────────┐
│           Presentation Layer         │
│       Java Swing + FlatLaf UI        │
└──────────────────┬───────────────────┘
                   │ (Calls Service Methods)
┌──────────────────▼───────────────────┐
│             Service Layer            │
│       Business Rules & Use Cases     │
└──────────────────┬───────────────────┘
                   │ (Calls Repository Interfaces)
┌──────────────────▼───────────────────┐
│           Repository Layer           │
│       Interfaces + MySQL/JDBC        │
└──────────────────┬───────────────────┘
                   │ (Reads / Writes SQL)
┌──────────────────▼───────────────────┐
│             MySQL Database           │
│              Persistence             │
└──────────────────────────────────────┘

       Domain Model + OOP Abstractions
       Observer-based Event Handling
       BCrypt Authentication (Cost Factor 12)
       Single-Connection Transaction Management
```

The **domain and service layers are designed independently of the Swing UI**, allowing the same business logic to support a different presentation layer in the future.

---

## 🧩 Core Object-Oriented Design

Object-Oriented Programming is the foundational design pillar of WalletWise.

### 1. Abstraction

Abstract classes such as `Wallet` and `Expense` define common contracts and essential behavior while delegating specialized implementation details to concrete subclasses.

```text
Wallet (Abstract Base)
 ├── PersonalWallet (Monthly Spending Limit)
 └── BusinessWallet (Per-Transaction Ceiling)

Expense (Abstract Base)
 ├── FixedExpense (Recurring Frequency: Rent, Bills)
 └── VariableExpense (Expected Maximums: Dining, Leisure)
```

This enables the system to represent diverse financial behaviors through domain models rather than procedural conditional checks.

### 2. Inheritance

Specialized wallet and expense subclasses inherit common state (`walletId`, `balance`, `category`, `amount`, `date`) from their base abstractions while providing specialized attributes (`monthlySpendingLimit`, `businessTransactionLimit`, `recurringFrequency`, `maximumExpectedAmount`).

### 3. Polymorphism & Dynamic Method Dispatch

Subclass-specific behaviors such as:

* `calculateTransactionLimit()`
* `isLimitExceeded(BigDecimal amount)`
* `getLimitWarningMessage()`
* `getExpenseType()`

are resolved through **dynamic method dispatch** at runtime.

The service layer (e.g., `WalletService.withdrawMoney` or `ExpenseService.addExpense`) operates directly on the base `Wallet` abstraction without needing `instanceof` inspections or type-switching conditionals.

### 4. Encapsulation & Defensive Copying

Domain state is strictly `private` and accessed through guarded getters and validated setters.
* Monetary values are validated using `java.math.BigDecimal` to ensure positive, non-zero values.
* Collection fields (`transactions`, `expenses`) use **defensive copying** in getters (`new ArrayList<>(transactions)`) to prevent external callers from mutating internal entity state directly.

### 5. Composition (HAS-A Relationships)

The domain models real-world financial relationships using object composition:

```text
User
 └── Wallet
      ├── Transactions
      └── Expenses

User
 └── Savings Goals

User
 └── Notifications & Rewards
```

---

## 🏛️ SOLID Principles

The architecture is built around the **SOLID design principles**:

### Single Responsibility Principle (SRP)
Responsibilities are cleanly separated across distinct layers:
* **Models** encapsulate domain state and validation.
* **Repositories** handle raw SQL queries and JDBC mapping.
* **Services** orchestrate business rules and transaction boundaries.
* **Observers** react to event-driven side-effects.
* **GUI Components** manage UI layout and user interaction.

### Open/Closed Principle (OCP)
The system is **open for extension, but closed for modification**:
* New wallet types (e.g., `CryptoWallet`) can be added by extending `Wallet` without modifying existing service code.
* New event listeners (e.g., `EmailNotificationObserver`) can be attached to `WalletEventListener` without changing core transaction services.

### Liskov Substitution Principle (LSP)
Concrete wallet implementations (`PersonalWallet`, `BusinessWallet`) can seamlessly substitute the base `Wallet` abstraction across services, repositories, and reporting components without breaking correctness.

### Interface Segregation Principle (ISP)
Data access operations are partitioned into focused, role-specific repository interfaces rather than one monolithic repository:
```text
IUserRepository
IWalletRepository
ITransactionRepository
IExpenseRepository
IBudgetRepository
ISavingsGoalRepository
INotificationRepository
IRewardRepository
IReportRepository
```

### Dependency Inversion Principle (DIP)
High-level service classes depend upon repository interfaces (`IWalletRepository`, `ITransactionRepository`) rather than concrete MySQL implementations. This decouples business logic from persistence technology and makes unit testing straightforward.

---

## 🎨 Applied Design Patterns

### 1. Repository Pattern
The Repository Pattern separates **domain business logic from database access**:
* Services interact solely with repository interfaces.
* Concrete repository classes encapsulate all SQL `PreparedStatement` executions and `ResultSet` mapping.

```text
Service Layer
     ↓
Repository Interface (e.g., IExpenseRepository)
     ↓
MySQL Repository (e.g., ExpenseRepository)
     ↓
JDBC Driver & MySQL Database
```

### 2. Observer Pattern
Financial transactions trigger independent side-effects without tightly coupling them to the core execution path:

```text
Wallet / Expense / Savings Event
               │
               ▼
      WalletEventListener (Event Bus)
               │
               ├── NotificationObserver (In-App Transaction & Budget Alerts)
               └── RewardObserver (+100 Pts Gamification on Goal Completion)
```

Completing a savings goal automatically triggers reward points and in-app notifications without embedding reward logic inside `SavingsGoalService`.

### 3. Dependency Injection (Constructor Injection)
Dependencies are injected into service and observer constructors, ensuring loose coupling and clean component lifecycles.

### 4. Single Table Inheritance (Database Pattern)
Inheritance hierarchies (`Wallet` and `Expense`) are mapped to single database tables (`wallets`, `expenses`) using discriminator columns (`wallet_type`, `expense_type`), avoiding costly multi-table `JOIN` operations.

---

## 💾 Persistence & Transaction Management

WalletWise uses **MySQL with JDBC** for reliable relational persistence.

### Atomic JDBC Transactions (ACID)
Financial operations modifying multiple related tables are grouped into explicit atomic transactions on a single connection:

```text
BEGIN TRANSACTION (setAutoCommit(false))
  │
  ├── 1. Update Wallet Balance (walletRepository.update(conn, wallet))
  ├── 2. Save Expense / Goal (expenseRepository.save(conn, expense, walletId))
  └── 3. Record Audit Transaction (transactionRepository.save(conn, tx, walletId))
  │
COMMIT (connection.commit())
```

If any step fails or an exception occurs:
```text
ROLLBACK (connection.rollback())
```

This prevents partial state updates (e.g., deducting balance without recording the expense) and preserves database consistency.

### Resource Leak Prevention
Every JDBC `Connection`, `PreparedStatement`, and `ResultSet` is wrapped in Java **try-with-resources** blocks, ensuring deterministic connection release and preventing connection pool starvation.

---

## 🔐 Authentication & Security

User authentication is secured using modern cryptographic hashing:

* **Salted BCrypt Hashing**: Passwords are hashed using BCrypt with a work factor of 12 via `at.favre.lib:bcrypt`.
* **Zero-Downtime Migration**: `UserService.authenticate()` automatically checks legacy plaintext records, verifies them, and transparently upgrades them to BCrypt hashes in the database upon successful login.
* **SQL Injection Prevention**: 100% of database queries use parameterized `PreparedStatement` placeholders (`?`).

---

## 💰 Financial Domain Features

While architecture is the primary focus, WalletWise implements a complete, practical financial management suite:

### 🏦 Wallets
* **Personal Wallets**: Monthly spending limit tracking.
* **Business Wallets**: Per-transaction ceiling tracking.
* **Metrics**: Real-time balance monitoring and visual utilization indicators.

### 💳 Transactions
* Deposits (income) and withdrawals (expenses/transfers).
* Real-time transaction ledger and activity history.
* Atomic balance updates and audit logging.

### 💸 Expenses & Category Analytics
* **Expense Classification**: Fixed expenses (recurring bills) and Variable expenses (discretionary spending).
* **Categories**: `Food`, `Rent`, `Utilities`, `Entertainment`, `Shopping`, `Healthcare`, and `Other`.
* **Visual Analytics**: Interactive expense category distribution in the wallet panel.

### 📈 Category Budgets & Proactive Alerts
* Set category-level spending limits with start and end dates.
* **80% Caution Alert**: Warns users when category spending reaches or crosses 80% of budget.
* **100% Budget Breach Alert**: Generates immediate overspending alerts when a category limit is exceeded.

### 🎯 Savings Goals & Gamification
* Set target amounts and completion dates.
* Contribute funds directly from wallet balances in atomic transactions.
* Earn **+100 Reward Points** and milestone badges upon goal achievement.

### 🔔 Notification Center
* Tracks transaction confirmations, spending limit warnings, budget threshold alerts, and savings milestones.
* Mark notifications as read or clear notification history.

### 📊 Financial Reports
* Generate **Monthly**, **Yearly**, and **Custom-period** financial statements.
* Itemized breakdowns of total income, expenses, savings allocations, and net balance.
* Formatted HTML summary preview and exportable CSV records.

---

## 🖥️ Presentation Layer

The current presentation layer is implemented using:

* **Java Swing**
* **FlatLaf Look and Feel** (Modern Obsidian Dark Theme: `#0D1117` canvas, `#161B22` cards)

Swing was chosen as a lightweight client for visualizing and interacting with the system.

However, **the desktop UI is not the architectural boundary of the application**:

```text
                 ┌── Swing Desktop UI (Current Client)
                 │
Core Application ├── REST API / Web UI (Future Extension)
(Domain/Service) │
                 └── Mobile / CLI Client (Future Extension)
```

The business logic is decoupled from the UI, meaning the same core system can be exposed through a REST controller, web frontend, or mobile application without modifying domain or persistence code.

---

## 🛠️ Technology Stack

| Layer | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Language** | Java | 17 LTS | Core object-oriented programming language |
| **Architecture** | Layered Architecture | — | Separation of Presentation, Service, Repo, Model |
| **UI Framework** | Java Swing | Native | Desktop user interface client |
| **UI Styling** | FlatLaf | 3.5.2 | Modern dark theme and component styling |
| **Persistence** | MySQL | 8.0+ | Relational database storage |
| **Database Access** | JDBC Connector/J | 8.3.0 | Low-level SQL execution and transaction control |
| **Security** | Favre BCrypt | 0.10.2 | Salted cryptographic password hashing |
| **Build Tool** | Apache Maven | 3.x | Dependency and build lifecycle management |

---

## 📁 Project Structure

```text
WalletWise/
│
├── database/
│   └── schema.sql                  # MySQL 3NF database schema & constraints
│
├── lib/                            # Bundled libraries (FlatLaf, BCrypt, MySQL Connector)
│   ├── flatlaf-3.5.2.jar
│   ├── bcrypt-0.10.2.jar
│   ├── bytes-1.5.0.jar
│   └── mysql-connector-j-26.7.0.jar
│
├── pom.xml                         # Maven build descriptor
│
├── src/main/java/
│   ├── gui/                        # Presentation Layer (Swing + FlatLaf)
│   │   ├── Main.java               # Application Entry Point
│   │   ├── MainFrame.java          # Main Application Shell
│   │   ├── LoginRegisterFrame.java # Authentication Window
│   │   ├── Theme.java              # Dark Theme Palette & Geometry Tokens
│   │   ├── UIHelper.java           # Reusable UI Card Panels & Helpers
│   │   └── *Panel.java             # Dashboard, Wallet, Expense, Budget, etc.
│   │
│   ├── model/                      # Domain Model (OOP Entities & Value Objects)
│   │   ├── enums/                  # Category, TransactionType, WalletType
│   │   ├── Wallet.java             # Abstract Base Wallet
│   │   ├── PersonalWallet.java     # Subclass with monthly limit
│   │   ├── BusinessWallet.java     # Subclass with per-transaction limit
│   │   ├── Expense.java            # Abstract Base Expense
│   │   ├── FixedExpense.java       # Subclass with recurring frequency
│   │   ├── VariableExpense.java    # Subclass with maximum limit
│   │   └── User.java, Budget.java, SavingsGoal.java, Transaction.java, Report.java, Reward.java
│   │
│   ├── observer/                   # Event-Driven Observer Subsystem
│   │   ├── WalletEventListener.java   # Event Bus Contract
│   │   ├── NotificationObserver.java  # In-app alert listener
│   │   └── RewardObserver.java        # Gamified reward points listener
│   │
│   ├── repository/                 # Data Access Layer (JDBC Implementations)
│   │   ├── interfaces/             # Segregated Repository Contracts (I*Repository)
│   │   └── *Repository.java        # Parameterized SQL PreparedStatement queries
│   │
│   ├── service/                    # Business Logic & ACID Transaction Management
│   │   └── *Service.java           # WalletService, ExpenseService, SavingsGoalService, etc.
│   │
│   └── util/                       # Infrastructure Utilities
│       └── DBConnection.java       # JDBC Connection Factory
│
├── .env.example                    # Database environment configuration template
└── README.md                       # Comprehensive Project Documentation
```

---

## 🚀 Running the Project

### 1. Prerequisites
* **Java Development Kit (JDK)**: Java 17 LTS or higher
* **MySQL Server**: 8.0+ running locally or in Docker
* **Apache Maven**: 3.8+

### 2. Database Setup
Run the SQL schema in MySQL to initialize tables and relationships:
```bash
mysql -u root -p < database/schema.sql
```

### 3. Environment Configuration
Create a `.env` file in the project root directory (or copy from `.env.example`):
```env
DB_URL=jdbc:mysql://localhost:3306/finance_manager
DB_USER=root
DB_PASSWORD=your_mysql_password
```

### 4. Build and Run
Compile and launch using Maven:
```bash
mvn compile exec:java -Dexec.mainClass="gui.Main"
```

Or open the project in any Java IDE (VS Code, IntelliJ IDEA, Eclipse) and run `src/main/java/gui/Main.java`.

---

## 📌 What This Project Demonstrates

WalletWise is primarily a demonstration of **software design and object-oriented engineering applied to a real-world domain**.

The project brings together:

* **Object-Oriented Programming (OOP)**
* **Abstraction, Inheritance, Polymorphism & Encapsulation**
* **Composition (HAS-A Relationships)**
* **SOLID Design Principles (SRP, OCP, LSP, ISP, DIP)**
* **Repository Pattern & Data Access Abstraction**
* **Observer Pattern & Event-Driven Decoupling**
* **Layered Architecture & Separation of Concerns**
* **JDBC & MySQL Persistence with Zero Resource Leaks**
* **Single-Connection Atomic Database Transactions (ACID)**
* **BCrypt Cryptographic Security & Zero-Downtime Migration**
* **Presentation Independence (Pluggable Client Architecture)**
