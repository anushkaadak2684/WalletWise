# 💳 WalletWise — Object-Oriented Finance & Digital Wallet Tracker

**WalletWise** is an enterprise-grade personal finance and digital wallet system crafted to demonstrate **Object-Oriented Programming (OOP)**, **SOLID Design Principles**, and pragmatic software design in Java.

While featuring a modern dark Swing GUI (FlatLaf), BCrypt password hashing, and MySQL/H2 persistence, the core philosophy of this project is **clean domain modeling, pure OOP polymorphism, and maintainable software architecture without unnecessary over-engineering**.

---

## 🎯 Core Engineering Philosophy

* **Pure OOP Polymorphism & Inheritance**: Distinct business behaviors (e.g. personal monthly limits vs. business per-transaction limits, fixed recurring bills vs. variable expenses) are modeled directly through class hierarchies and polymorphic method dispatch.
* **Pragmatic Simplicity**: Avoids over-engineered pattern proliferation (such as redundant Strategy wrappers or trivial factory classes) when clean OOP polymorphism solves the domain problem directly.
* **Adherence to SOLID Principles**: Single Responsibility (SRP), Open/Closed (OCP), Liskov Substitution (LSP), Interface Segregation (ISP), and Dependency Inversion (DIP).
* **Decoupled Event Handling**: Synchronous **Observer Pattern** cleanly separates financial transactions from system notifications and gamified rewards.
* **Atomic JDBC Transactions**: Full data integrity and rollback protection on multi-table database operations.

---

## 🧩 1. Deep Dive: Object-Oriented Programming (OOP) Concepts

### 🔒 A. Encapsulation & Invariant Protection
* **Private Attribute Scoping**: All class fields across domain models (`User`, `Wallet`, `Expense`, `Budget`, `SavingsGoal`, `Transaction`, `Reward`, `Notification`) are strictly `private`.
* **Business Rule Validation**: Modifiers and business methods validate invariants before state mutations (e.g., non-negative balances, non-zero deposits/withdrawals, valid date bounds).
* **Defensive Copying**: Collections within domain objects return defensive copies (`new ArrayList<>(transactions)`, `new ArrayList<>(expenses)`) to protect internal state from external tampering.

```java
public void deposit(BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("Deposit amount must be greater than zero");
    }
    this.balance = this.balance.add(amount);
}
```

---

### 🏛️ B. Inheritance & Abstract Base Classes
* **Abstract Base Classes**: `Wallet` and `Expense` provide shared attributes and core functionality while defining contracts for specialized behavior:
  * `PersonalWallet` & `BusinessWallet` inherit from `Wallet`.
  * `FixedExpense` & `VariableExpense` inherit from `Expense`.

```
                  ┌─────────────────┐
                  │  Wallet (Abstr) │
                  └────────┬────────┘
                           │
             ┌─────────────┴─────────────┐
             │                           │
    ┌────────────────┐          ┌─────────────────┐
    │ PersonalWallet │          │ BusinessWallet  │
    └────────────────┘          └─────────────────┘
```

---

### 🎭 C. Pure Polymorphism & Dynamic Method Dispatch
* **Polymorphic Method Resolution**: Base references uniformly invoke polymorphic domain methods without runtime type inspection (`instanceof`):
  * `wallet.calculateTransactionLimit()`
  * `wallet.isLimitExceeded(amount)`
  * `wallet.getLimitWarningMessage()`
* **How Subclasses Specialize Behavior**:
  * `PersonalWallet`: Evaluates spending against the aggregate **monthly spending limit**.
  * `BusinessWallet`: Evaluates transactions against a **single per-transaction threshold**.

```java
// Example: Uniform polymorphic invocation
Wallet wallet = walletService.getWalletByUserId(userId);
if (wallet.isLimitExceeded(expenseAmount)) {
    UIHelper.showWarning(this, wallet.getLimitWarningMessage());
}
```

---

### 🧱 D. Abstraction & Interface-Driven Contracts
* System boundaries and dependencies are modeled using focused interfaces:
  * [`repository.interfaces.*`](file:///c:/Users/anush/Downloads/WalletWise/src/main/java/repository/interfaces): Abstract data access contracts (`IUserRepository`, `IWalletRepository`, `ITransactionRepository`, `IExpenseRepository`, `IBudgetRepository`, `ISavingsGoalRepository`, `INotificationRepository`, `IRewardRepository`, `IReportRepository`).
  * [`observer.WalletEventListener`](file:///c:/Users/anush/Downloads/WalletWise/src/main/java/observer/WalletEventListener.java): Contract for domain event subscribers.

---

### 🔗 E. Composition Over Inheritance (HAS-A Relationships)
* `User` **HAS-A** `Wallet`.
* `Wallet` **HAS-A** `List<Transaction>` and `List<Expense>`.
* `WalletService` **HAS-A** `IWalletRepository` and `ITransactionRepository`.
* `SavingsGoalService` **HAS-A** `ISavingsGoalRepository`, `IWalletRepository`, and `ITransactionRepository`.

---

## 🏛️ 2. Deep Dive: SOLID Design Principles

| Principle | Meaning & Purpose | Concrete Implementation in WalletWise |
| :--- | :--- | :--- |
| **S — Single Responsibility Principle (SRP)** | A class should have one, and only one, reason to change. | • `Wallet`: Encapsulates balance state and limit rules.<br>• `WalletRepository`: Handles JDBC persistence.<br>• `WalletService`: Coordinates transactions and domain logic.<br>• `NotificationObserver`: Handles notification creation.<br>• `WalletPanel`: Renders Swing UI and charts. |
| **O — Open/Closed Principle (OCP)** | Open for extension, closed for modification. | • Adding a new wallet type (e.g. `StudentWallet`) or expense classification (e.g. `TaxDeductibleExpense`) only requires adding a new subclass implementing `isLimitExceeded()`—**zero modifications to existing services or repositories**. |
| **L — Liskov Substitution Principle (LSP)** | Subtypes must be substitutable for their base types. | • `PersonalWallet` and `BusinessWallet` seamlessly substitute `Wallet` across all services, repositories, and UI controllers without unexpected side-effects. |
| **I — Interface Segregation Principle (ISP)** | Clients should not depend on interfaces they do not use. | • Repositories are cleanly segregated into domain-focused interfaces (`IUserRepository`, `IWalletRepository`, `IExpenseRepository`, etc.) rather than one giant data interface. |
| **D — Dependency Inversion Principle (DIP)** | High-level modules should depend on abstractions, not concrete details. | • `WalletService`, `ExpenseService`, and `SavingsGoalService` depend strictly on repository interfaces, enabling fast unit testing and mocking via Mockito without a real database. |

---

## 🎨 3. Design Patterns Applied

### Observer Pattern (`observer`)
* **Problem**: Financial transactions and completed savings goals need to trigger audit notifications and award gamified reward points without coupling the core transaction engine to notification/reward code.
* **Solution**: `WalletEventListener` defines domain callbacks (`onTransactionCreated`, `onSavingsGoalAchieved`).
* **Benefit**: `WalletService` and `SavingsGoalService` notify listeners synchronously upon successful commits. `NotificationObserver` creates in-app alerts and `RewardObserver` grants points (+100 pts on goal completion) independently.

```
       ┌──────────────────────┐
       │ WalletEventListener  │◄─────────────────┐
       └──────────▲───────────┘                  │ (subscribes to events)
                  │                              │
       ┌──────────┴───────────┐          ┌───────┴────────┐
       │                      │          │ WalletService  │
┌───────────────┐     ┌──────────────┐   └────────────────┘
│NotificationObs│     │RewardObserver│
└───────────────┘     └──────────────┘
```

---

## 📐 High-Level Layered Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                 PRESENTATION LAYER (GUI)                    │
│     Swing Panels, Custom Graphics (Pie Chart, Progress)     │
└──────────────────────────────┬──────────────────────────────┘
                               │ (Calls Services Exclusively)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                   BUSINESS SERVICE LAYER                    │
│     Transaction Orchestration, Domain Rules, Observers      │
└──────────────┬──────────────────────────────┬───────────────┘
               │                              │
               ▼                              ▼
┌─────────────────────────────┐┌──────────────────────────────┐
│   DOMAIN MODEL HIERARCHY    ││        OBSERVER LAYER        │
│ Personal & Business Wallets ││ Notifications & Gamification │
└─────────────────────────────┘└──────────────────────────────┘
               │
               ▼ (Uses Repository Interfaces)
┌─────────────────────────────────────────────────────────────┐
│                  DATA PERSISTENCE LAYER                     │
│    Repository Interfaces (CRUD) & MySQL JDBC Implementations│
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                     DATABASE / STORAGE                      │
│             MySQL 8.0+ / H2 In-Memory (Tests)               │
└─────────────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
WalletWise/
├── pom.xml                                 # Maven build descriptor & dependencies
├── database/
│   └── schema.sql                          # MySQL relational database schema
├── src/
│   ├── main/java/
│   │   ├── gui/                            # Presentation (Swing GUI Panels & MainFrame)
│   │   │   ├── Main.java                   # Application Entrypoint
│   │   │   ├── MainFrame.java              # Main Dashboard Container
│   │   │   ├── LoginRegisterFrame.java     # Authentication Frame
│   │   │   ├── WalletPanel.java            # Wallet Metrics & Visual Pie Chart
│   │   │   ├── ExpensePanel.java           # Expense Logging & History
│   │   │   ├── BudgetPanel.java            # Budget Tracking
│   │   │   ├── SavingsPanel.java           # Savings Goals & Contributions
│   │   │   ├── NotificationPanel.java      # Notifications Center
│   │   │   ├── RewardPanel.java            # Points & Achievements
│   │   │   └── ReportPanel.java            # Financial Reports & Itemized Viewer
│   │   ├── model/                          # Domain Entities (User, Wallet, Expense, etc.)
│   │   │   └── enums/                      # Domain Enumerations (WalletType, ExpenseCategory, etc.)
│   │   ├── observer/                       # Event Observers (NotificationObserver, RewardObserver)
│   │   ├── repository/                     # Repository Implementations (JDBC SQL)
│   │   │   └── interfaces/                 # Repository Interfaces (IUserRepository, IWalletRepository, etc.)
│   │   ├── service/                        # Business Logic & JDBC Transaction Management
│   │   └── util/                           # Utilities (DBConnection)
│   └── test/java/
│       ├── integration/                    # Database Transaction Integration Tests (H2)
│       ├── model/                          # Polymorphism & Model Unit Tests
│       ├── observer/                       # Observer Pattern Unit Tests
│       └── service/                        # Service Unit Tests (Mockito)
└── README.md
```

---

## 🧪 Testing & Verification

The project includes an automated test suite with **100% pass rate**:

* **Unit Tests (Mockito & JUnit 5)**:
  * [`UserServiceTest`](file:///c:/Users/anush/Downloads/WalletWise/src/test/java/service/UserServiceTest.java): Verifies BCrypt password hashing, authentication, and legacy plaintext migration.
  * [`WalletPolymorphismTest`](file:///c:/Users/anush/Downloads/WalletWise/src/test/java/model/WalletPolymorphismTest.java): Verifies pure polymorphic limit evaluation for `PersonalWallet` and `BusinessWallet`.
  * [`WalletObserverTest`](file:///c:/Users/anush/Downloads/WalletWise/src/test/java/observer/WalletObserverTest.java): Verifies decoupled observer notification and reward event handling.
* **Integration Tests (H2 Database)**:
  * [`TransactionIntegrityIntegrationTest`](file:///c:/Users/anush/Downloads/WalletWise/src/test/java/integration/TransactionIntegrityIntegrationTest.java): Verifies atomic JDBC transactions, commit, and rollback behavior without orphaned records.

```bash
# Run tests with Maven
mvn clean test
```

---

## 🚀 Getting Started

### 1. Prerequisites
* **Java**: JDK 17 or higher.
* **MySQL**: MySQL Server 8.0+.

### 2. Database Setup
```bash
mysql -u root -p < database/schema.sql
```

### 3. Environment Configuration
Create a `.env` file in the root directory:
```env
DB_URL=jdbc:mysql://localhost:3306/finance_manager
DB_USER=root
DB_PASSWORD=your_password
```

### 4. Run the Application
```bash
mvn compile exec:java -Dexec.mainClass="gui.Main"
```
Or execute `gui.Main.main()` in your IDE.
