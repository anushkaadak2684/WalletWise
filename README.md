# 💳 WalletWise - Object-Oriented Digital Wallet & Personal Finance System

**WalletWise** is an end-to-end **object-oriented software system** for digital wallet and personal finance management.

The project focuses on the design and implementation of a maintainable financial application using **object-oriented programming, SOLID principles, design patterns, persistence, transactional data management, and secure authentication**.

The application currently uses **Java 17 + Swing (FlatLaf)** as the presentation layer and **MySQL** for persistence. Its layered architecture keeps business logic independent of the user interface, allowing the presentation layer to be replaced with a **web application, REST API, or mobile client** without redesigning domain logic.

---

## 🎯 Project Objective

WalletWise demonstrates how a financial management system can be modeled and implemented using **object-oriented design and layered architecture**:

* **Domain Modeling**: Encapsulating financial entities, state, and business rules into type-safe domain objects.
* **Separation of Concerns**: Maintaining a clear 3-tier boundary between presentation, business logic, and database persistence.
* **Transactional Integrity**: Ensuring data consistency across multi-table operations using single-connection JDBC transactions.
* **Event-Driven Decoupling**: Using the Observer Pattern to handle notifications and rewards without coupling them to core transaction logic.
* **Client Independence**: Designing the domain and service layers so alternative clients (REST API, Web UI) can be connected without rewriting business logic.

---

## 🌟 Key Features

### 🏦 Wallet Management
* **Specialized Wallet Types**: Supports **Personal Wallet** (monthly spending limits) and **Business Wallet** (per-transaction limits).
* **Live Balance & Utilization**: Real-time balance tracking, utilization metrics, and status indicators (`SAFE` vs `LIMIT EXCEEDED`).
* **Subtype Polymorphism**: Dynamic method dispatch evaluates spending limits polymorphically without `instanceof` checks.

### 💳 Transactions (Deposits & Withdrawals)
* **Ledger Entries**: Record deposits (income) and withdrawals (expenses/transfers) with custom descriptions.
* **Audit History**: Activity log and automated transaction receipts.
* **Transactional Safety**: Balance updates and transaction records execute within database transactions (`commit`/`rollback`).

### 💸 Expense Tracking & Category Analytics
* **Expense Classification**: Differentiates between **Fixed Expenses** (recurring bills, rent) and **Variable Expenses** (groceries, leisure).
* **Category Breakdown**: Categorizes expenses across `Food`, `Travel`, `Shopping`, `Entertainment`, `Health`, `Education`, and `Other`.
* **Category Visualizer**: Custom component rendering category expense distribution in `WalletPanel`.

### 📈 Category Budgeting & Alerts
* **Budget Ceilings**: Define category spending limits with custom start and end dates.
* **80% Caution Alert**: Warns users when category spending reaches or crosses 80% of budget.
* **100% Budget Breach Alert**: Generates overspending alerts when a category limit is exceeded.

### 🎯 Savings Goals & Rewards
* **Target-Driven Goals**: Create savings goals with target amounts and completion dates.
* **Goal Contributions**: Transfer funds directly from active wallet balances into savings goals with transactional consistency.
* **Reward System**: Automatically awards **+100 Reward Points** and milestone alerts upon goal completion via the Observer Pattern.

### 🔔 Notifications & Activity Center
* **In-App Notification Center**: Tracks transaction receipts, savings milestones, and unread badges.
* **Threshold Alerts**: Warning dialogs and banners for spending limit overruns and budget thresholds.

### 📊 Financial Reports
* **Periodic Reporting**: Generate statements across **Monthly**, **Yearly**, and **Custom** intervals.
* **Itemized Side-Pane Viewer**: Dual-pane view rendering detailed HTML breakdowns (income, expenses, budgets, savings) directly in the side panel.

---

## 🏗️ Architecture at a Glance

WalletWise follows a **decoupled 3-tier layered architecture**:

```text
┌────────────────────────────────────────────────────────┐
│               Presentation Layer (gui.*)               │
│         Java Swing + FlatLaf Dark Modern Client        │
└───────────────────────────┬────────────────────────────┘
                            │ (Calls Service Methods)
┌───────────────────────────▼────────────────────────────┐
│                 Service Layer (service.*)              │
│       Business Rules, Validation & Transactions        │
└───────────────────────────┬────────────────────────────┘
                            │ (Calls Repository Interfaces)
┌───────────────────────────▼────────────────────────────┐
│               Repository Layer (repository.*)          │
│          Interfaces + MySQL JDBC Implementations       │
└───────────────────────────┬────────────────────────────┘
                            │ (Reads / Writes SQL)
┌───────────────────────────▼────────────────────────────┐
│                 MySQL Relational Database              │
│               Normalized 3NF Schema Storage            │
└────────────────────────────────────────────────────────┘

        Domain Model + Encapsulated OOP Entities
        Observer Event Bus (WalletEventListener)
        BCrypt Password Hashing (Cost Factor 12)
        Single-Connection JDBC Transactions
```

The **domain and service layers are decoupled from the Swing UI**, allowing the core business engine to support alternative clients (CLI, REST API, Web) without modifying domain logic.

---

## 🧩 Core Object-Oriented Design

Object-Oriented Programming is central to the design of WalletWise:

### 1. Abstraction
Abstract classes define common state and contract while delegating specific behavior to subclasses:

```text
Wallet (Abstract Base)
 ├── PersonalWallet (Monthly Spending Limit)
 └── BusinessWallet (Per-Transaction Ceiling)

Expense (Abstract Base)
 ├── FixedExpense (Recurring Frequency: Rent, Bills)
 └── VariableExpense (Expected Maximums: Dining, Leisure)
```

### 2. Inheritance
Specialized wallet and expense classes inherit common attributes (`walletId`, `balance`, `category`, `amount`, `date`) while defining specific behavior and fields (`monthlySpendingLimit`, `businessTransactionLimit`, `recurringFrequency`, `maximumExpectedAmount`).

### 3. Polymorphism & Dynamic Method Dispatch
Subclass-specific behaviors are resolved at runtime via method overriding:
* `calculateTransactionLimit()`: Computes the applicable spending boundary.
* `isLimitExceeded(BigDecimal amount)`: Evaluates if a transaction breaches limits.
* `getLimitWarningMessage()`: Returns subtype-specific warning text.
* `getExpenseType()`: Returns the expense classification (`FIXED` vs `VARIABLE`).

The service layer operates directly on the `Wallet` and `Expense` abstractions without needing `instanceof` conditional checks.

### 4. Encapsulation & Defensive Copying
Domain state is kept `private` and accessed through guarded getters and validated setters:
* Monetary values use `BigDecimal` with positive-amount validation.
* Collections (`transactions`, `expenses`) use **defensive copying** in getters (`new ArrayList<>(transactions)`) to prevent external code from mutating internal entity state directly.

### 5. Composition (HAS-A Relationships)
The domain models relationships through composition:

```text
User
 └── Wallet
      ├── List<Transaction>
      └── List<Expense>
```

---

## 🏛️ SOLID Principles

The codebase is organized around the **SOLID principles**:

### Single Responsibility Principle (SRP)
Each layer and class has a focused responsibility:
* **Models**: Encapsulate domain state, invariants, and validation rules.
* **Repositories**: Handle database access, SQL execution, and `ResultSet` mapping.
* **Services**: Coordinate business operations and manage transaction boundaries.
* **Observers**: Handle decoupled side-effects (notifications, reward points).
* **GUI Components**: Handle user interface rendering and input events.

### Open/Closed Principle (OCP)
Classes are open for extension and closed for modification:
* New wallet subtypes (e.g. `CryptoWallet`) or expense types can be added by extending base classes without altering existing service logic.
* New event listeners (e.g. `EmailNotificationObserver`) can subscribe to `WalletEventListener` without changing transaction code.

### Liskov Substitution Principle (LSP)
Concrete implementations (`PersonalWallet`, `BusinessWallet`) can substitute the base `Wallet` abstraction across services, repositories, and reporting routines without breaking application behavior.

### Interface Segregation Principle (ISP)
Data access operations are partitioned into focused repository interfaces (`IUserRepository`, `IWalletRepository`, `ITransactionRepository`, `IExpenseRepository`, `IBudgetRepository`, `ISavingsGoalRepository`, `INotificationRepository`, `IRewardRepository`, `IReportRepository`) rather than a single monolithic interface.

### Dependency Inversion Principle (DIP)
Service classes depend on repository interfaces rather than concrete MySQL implementations, keeping business logic independent of database specifics.

---

## 🎨 Applied Design Patterns

### 1. Repository Pattern
Separates business logic from data access:
* Services interact only with repository interfaces.
* Concrete repository classes encapsulate SQL `PreparedStatement` executions and `ResultSet` mapping.

```text
Service Layer  ──►  Repository Interface  ──►  MySQL Repository  ──►  JDBC & Database
```

### 2. Observer Pattern
Decouples side-effects from core transaction flows:

```text
Wallet / Expense / Savings Event
               │
               ▼
      WalletEventListener (Event Bus)
               │
               ├── NotificationObserver (Transaction & Budget Alerts)
               └── RewardObserver (+100 Pts on Goal Completion)
```

Completing a savings goal automatically triggers reward points and in-app notifications without embedding reward logic inside `SavingsGoalService`.

### 3. Dependency Injection (Constructor Injection)
Dependencies are passed into service and observer constructors, ensuring loose coupling and clean component lifecycles.

### 4. Single Table Inheritance (Database Pattern)
Inheritance hierarchies (`Wallet` and `Expense`) map to single database tables (`wallets`, `expenses`) using discriminator columns (`wallet_type`, `expense_type`), avoiding unnecessary table joins.

---

## 💾 Persistence & Transaction Management

WalletWise uses **MySQL with JDBC** for relational persistence.

### Atomic JDBC Transactions (ACID)
Operations modifying multiple related tables are executed within explicit transactions on a shared connection:

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

This prevents partial state updates and maintains database consistency.

### Resource Management
All JDBC `Connection`, `PreparedStatement`, and `ResultSet` instances are managed using Java **try-with-resources** blocks for deterministic closing and leak prevention.

---

## 🔐 Authentication & Security

User authentication and security practices include:

* **BCrypt Password Hashing**: Passwords are saved with a work factor of 12 using `at.favre.lib:bcrypt`.
* **Constant-Time Verification**: Protects against timing attacks during authentication.
* **Automatic Password Upgrading**: Detects legacy plaintext passwords on login, verifies them, and upgrades them to BCrypt hashes in the database upon successful authentication.
* **SQL Injection Protection**: All database operations use parameterized `PreparedStatement` queries.

---

## 🖥️ Presentation Layer

The current presentation layer is implemented using:

* **Java Swing**
* **FlatLaf Look and Feel** (Modern Obsidian Dark Theme: `#0D1117` canvas, `#161B22` cards)

Swing was chosen as a practical client interface for visualizing and interacting with the system.

However, **the desktop UI is not the architectural boundary of the application**:

```text
                 ┌── Swing Desktop UI (Current Client)
                 │
Core Application ├── REST API / Web UI (Future Extension)
(Domain/Service) │
                 └── Mobile / CLI Client (Future Extension)
```

The business and domain layers can be exposed through a REST controller, web frontend, or mobile application without modifying core logic.

---

## 🛠️ Technology Stack

| Layer | Technology | Purpose |
| :--- | :--- | :--- |
| **Language** | Java 17 (LTS) | Core object-oriented programming language |
| **Architecture** | Layered Architecture | Separation of Presentation, Service, Repo, Model |
| **UI Framework** | Java Swing | Native desktop user interface client |
| **UI Styling** | FlatLaf (3.5.2) | Modern dark theme and component geometry |
| **Persistence** | MySQL 8.0+ | Relational database storage |
| **Database Access** | JDBC Connector/J (8.3.0) | Direct SQL execution and transaction control |
| **Security** | Favre BCrypt (0.10.2) | Salted cryptographic password hashing |
| **Build Tool** | Apache Maven | Dependency and build lifecycle management |

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
# Database Credentials
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
