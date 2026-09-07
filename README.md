# 💳 WalletWise — Object-Oriented Digital Wallet & Personal Finance System

**WalletWise** is an end-to-end **object-oriented software system** that models a digital wallet and personal finance management domain.

The primary goal of the project is **not just the desktop interface itself**, but the design and implementation of a maintainable financial application using **object-oriented programming, SOLID principles, design patterns, persistence, transactional data management, and secure authentication**.

The current implementation uses **Java 17 + Swing (FlatLaf)** as the presentation layer and **MySQL** for persistence. The architecture is intentionally layered and decoupled so that the presentation layer can be replaced with a **web application, REST API, or mobile client** without fundamentally redesigning the domain and business logic.

---

## 🎯 Project Objective

WalletWise was built to demonstrate how a real-world financial management domain can be modeled and engineered using **Object-Oriented Programming and clean architectural principles from end to end**:

* **Rich Domain Modeling**: Translating complex financial entities and business constraints into encapsulated, type-safe domain objects rather than procedural CRUD scripts.
* **Separation of Concerns**: Establishing a strict 3-tier boundary so business rules are decoupled from both the presentation client and database persistence.
* **ACID Transactional Consistency**: Guaranteeing data integrity across multi-table financial operations through single-connection JDBC transactions.
* **Event-Driven Decoupling**: Leveraging the Observer Pattern to handle notifications and gamification as decoupled side-effects of financial events.
* **Presentation Independence**: Ensuring the core service and domain layers can seamlessly support alternative clients (such as a REST API, Web UI, or mobile application) without modifying business logic.

---

## 🌟 Key Features

### 🏦 Wallet Management
* **Specialized Wallet Types**: Supports **Personal Wallet** (monthly spending limits) and **Business Wallet** (per-transaction limits).
* **Live Balance & Utilization Metrics**: Real-time balance tracking, dynamic utilization indicators, and status indicators (`SAFE` vs `LIMIT EXCEEDED`).
* **Subtype Polymorphism**: Dynamic method dispatch evaluates spending limit thresholds polymorphically without type inspection (`instanceof`).

### 💳 Transactions (Deposits & Withdrawals)
* **Ledger Entries**: Record deposits (income) and withdrawals (expenses/transfers) with custom descriptions.
* **Audit History**: Real-time activity history and automated transaction receipts.
* **Atomic JDBC Transactions**: Balance updates and ledger entries execute in atomic transactions (`commit`/`rollback`) with full data integrity.

### 💸 Expense Tracking & Category Analytics
* **Expense Classification**: Differentiates between **Fixed Expenses** (recurring bills, rent) and **Variable Expenses** (groceries, leisure).
* **Category Breakdown**: Categorizes expenses across `Food`, `Travel`, `Shopping`, `Entertainment`, `Health`, `Education`, and `Other`.
* **Visual Pie Chart**: Custom component rendering interactive expense category distribution in `WalletPanel`.

### 📈 Category Budgeting & Proactive Alerts
* **Budget Ceilings**: Define category spending limits with custom start and end dates.
* **80% Caution Alert**: Warns users when category spending reaches or crosses 80% of budget.
* **100% Budget Breach Alert**: Generates immediate overspending alerts when a category limit is exceeded.

### 🎯 Savings Goals & Gamified Rewards
* **Target-Driven Milestones**: Establish target savings goals with custom target dates.
* **Atomic Goal Contributions**: Transfer funds directly into savings goals from active wallet balances in an atomic transaction.
* **Gamification**: Earn **+100 Reward Points** and system achievement notifications automatically upon completing a savings goal via the Observer Pattern.

### 🔔 Notifications & Activity Center
* **In-App Notification Center**: Tracks transaction receipts, savings milestones, and unread badges.
* **Proactive Alerts**: Immediate warning dialogs and banners for spending limit overruns and budget thresholds.

### 📊 Comprehensive Financial Reports
* **Periodical Reporting**: Generate statements across **Monthly**, **Yearly**, and **Custom** intervals.
* **Itemized Side-Pane Viewer**: Synchronized dual-pane view rendering detailed HTML breakdowns (income, expenses, budgets, savings) directly in the side panel.

---

## 🏗️ Architecture at a Glance

WalletWise follows a **strict, decoupled 3-tier layered architecture**:

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
        BCrypt Cryptographic Security (Cost Factor 12)
        Single-Connection JDBC Transaction Demarcation
```

The **domain and service layers are designed independently of the Swing UI**, allowing the same core business engine to power alternative clients (CLI, REST API, Web) without modifying domain logic.

---

## 🧩 Core Object-Oriented Design

Object-Oriented Programming is the foundational design pillar of WalletWise.

### 1. Abstraction
Abstract classes such as `Wallet` and `Expense` define common behavior while allowing specialized implementations.

```text
Wallet (Abstract Base)
 ├── PersonalWallet (Monthly Spending Limit)
 └── BusinessWallet (Per-Transaction Ceiling)

Expense (Abstract Base)
 ├── FixedExpense (Recurring Frequency: Rent, Bills)
 └── VariableExpense (Expected Maximums: Dining, Leisure)
```

This enables the system to represent different financial behaviors through domain objects rather than procedural conditional logic.

### 2. Inheritance
Specialized wallet and expense types inherit common state (`walletId`, `balance`, `category`, `amount`, `date`) from their respective abstractions while implementing their own domain rules (`monthlySpendingLimit`, `businessTransactionLimit`, `recurringFrequency`, `maximumExpectedAmount`).

### 3. Polymorphism & Dynamic Method Dispatch
Subclass-specific behaviors are resolved through **dynamic method dispatch** at runtime:
* `calculateTransactionLimit()`: Computes the applicable spending boundary.
* `isLimitExceeded(BigDecimal amount)`: Evaluates if a transaction breaches limits.
* `getLimitWarningMessage()`: Produces subtype-specific warning descriptions.
* `getExpenseType()`: Identifies the expense classification (`FIXED` vs `VARIABLE`).

The service layer (e.g. `WalletService.withdrawMoney` or `ExpenseService.addExpense`) operates directly on the `Wallet` abstraction without needing `instanceof` conditional checks.

### 4. Encapsulation & Defensive Copying
Domain state is strictly `private` and accessed through guarded getters and validated setters:
* Monetary values are validated using `java.math.BigDecimal` to ensure positive, non-zero amounts.
* Collections (`transactions`, `expenses`) use **defensive copying** in getters (`new ArrayList<>(transactions)`) to prevent external callers from mutating internal entity state directly.

### 5. Composition (HAS-A Relationships)
The domain models real-world relationships through object composition:

```text
User
 └── Wallet
      ├── List<Transaction>
      └── List<Expense>
```

---

## 🏛️ SOLID Principles

The architecture is built around the **SOLID design principles**:

### Single Responsibility Principle (SRP)
Responsibilities are cleanly separated across dedicated layers:
* **Models**: Encapsulate domain state, invariants, and validation rules.
* **Repositories**: Handle database access, SQL execution, and `ResultSet` mapping.
* **Services**: Coordinate business operations and manage transaction boundaries.
* **Observers**: Handle decoupled side-effects (notifications, reward points).
* **GUI Components**: Manage user interface rendering and event capture.

### Open/Closed Principle (OCP)
Core subsystems are **open for extension, but closed for modification**:
* New wallet subtypes (e.g. `CryptoWallet`) or expense classifications can be added by extending base classes without altering existing service logic.
* New event listeners (e.g. `EmailNotificationObserver`) can subscribe to `WalletEventListener` without changing transaction code.

### Liskov Substitution Principle (LSP)
Concrete wallet implementations (`PersonalWallet`, `BusinessWallet`) can seamlessly substitute the base `Wallet` abstraction across all services, repositories, and reporting routines without breaking system correctness.

### Interface Segregation Principle (ISP)
Data access operations are partitioned into focused, role-specific repository interfaces (`IUserRepository`, `IWalletRepository`, `ITransactionRepository`, `IExpenseRepository`, `IBudgetRepository`, `ISavingsGoalRepository`, `INotificationRepository`, `IRewardRepository`, `IReportRepository`) rather than a single monolithic interface.

### Dependency Inversion Principle (DIP)
High-level service classes depend upon repository interfaces (`IWalletRepository`, `ITransactionRepository`) rather than concrete MySQL implementations. This decouples business logic from persistence technology and makes unit testing straightforward.

---

## 🎨 Applied Design Patterns

### 1. Repository Pattern
The Repository Pattern separates **domain business logic from database access**:
* Services interact exclusively with repository interfaces.
* Concrete repository classes encapsulate all SQL `PreparedStatement` executions and `ResultSet` mapping.

```text
Service Layer  ──►  Repository Interface  ──►  MySQL Repository  ──►  JDBC & Database
```

### 2. Observer Pattern
Financial events trigger independent side-effects without tightly coupling them to the core transaction flow:

```text
Wallet / Expense / Savings Event
               │
               ▼
      WalletEventListener (Event Bus)
               │
               ├── NotificationObserver (Transaction & Budget Alerts)
               └── RewardObserver (+100 Pts Gamification on Goal Completion)
```

Completing a savings goal automatically triggers reward points and in-app notifications without embedding reward logic inside `SavingsGoalService`.

### 3. Dependency Injection (Constructor Injection)
Dependencies are injected into service and observer constructors, ensuring loose coupling, clear component lifecycles, and testability.

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

This prevents partial state updates (e.g. deducting balance without recording the expense) and maintains database consistency.

### Resource Leak Prevention
Every JDBC `Connection`, `PreparedStatement`, and `ResultSet` is wrapped in Java **try-with-resources** blocks, ensuring deterministic connection release and preventing connection pool starvation.

---

## 🔐 Authentication & Security

User authentication is secured using modern cryptographic hashing:

* **Salted BCrypt Hashing**: Passwords are saved with a work factor of 12 via `at.favre.lib:bcrypt`.
* **Constant-Time Verification**: Secure password verification protecting against timing attacks.
* **Zero-Downtime Migration**: `UserService.authenticate()` automatically detects legacy plaintext records, verifies them, and transparently upgrades them to BCrypt hashes in the database upon successful login.
* **SQL Injection Prevention**: 100% of database queries use parameterized `PreparedStatement` placeholders (`?`).

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

The business logic is decoupled from the UI, meaning the same core system can be exposed through a REST controller, web frontend, or mobile application without modifying domain or persistence code.

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
