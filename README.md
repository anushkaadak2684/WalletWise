# 💳 WalletWise — Object-Oriented Finance & Digital Wallet Tracker

**WalletWise** is an enterprise-grade personal finance and digital wallet system crafted to demonstrate advanced **Object-Oriented Programming (OOP)**, **SOLID Design Principles**, and **Software Design Patterns** in Java.

While featuring a fully functional Swing GUI (FlatLaf) and MySQL persistence, the core focus of this project is **clean domain modeling, modular architecture, and extensible object-oriented design**.

---

## 🎯 Core Engineering Objectives

* **Mastery of OOP Pillars**: Practical application of Encapsulation, Inheritance, Polymorphism, Abstraction, and Composition.
* **Adherence to SOLID Principles**: Decoupled, maintainable, and testable design.
* **Practical Design Patterns**: Applied Strategy, Observer, and Factory patterns to solve real domain problems without over-engineering.
* **Separation of Concerns**: Strict layered architecture separating Presentation, Business Logic, and Data Persistence.

---

## 🧩 1. Deep Dive: Object-Oriented Programming (OOP) Concepts

### 🔒 A. Encapsulation & Domain Integrity
* **Private State Scoping**: All class attributes across domain models (`User`, `Wallet`, `Expense`, `Budget`, `SavingsGoal`) are declared `private` to prevent unauthorized external state mutation.
* **Invariant Enforcement**: Setters and business methods enforce strict business validations (e.g., preventing negative deposits/withdrawals, validating date ranges, ensuring positive spending limits).
* **Defensive Copying**: Collections within domain objects (e.g., `wallet.getTransactions()`, `wallet.getExpenses()`) return defensive copies (`new ArrayList<>(transactions)`) to preserve encapsulation.

```java
public void deposit(BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("Deposit amount must be greater than zero");
    }
    this.balance = this.balance.add(amount);
}
```

---

### 🏛️ B. Inheritance & Class Hierarchies
* **Abstract Base Classes**: `Wallet` and `Expense` provide shared attributes (id, balance, date, amount) and template behavior, while leaving specialized logic to concrete subclasses.
* **Specialized Subclasses**:
  * `PersonalWallet` & `BusinessWallet` extend `Wallet`, introducing monthly aggregate limits vs. per-transaction limits.
  * `FixedExpense` & `VariableExpense` extend `Expense`, introducing recurring frequencies (`MONTHLY`, `WEEKLY`) vs. expected maximum amounts.

```
            ┌─────────────────┐
            │  Wallet (Abstr) │
            └────────┬────────┘
                     │
         ┌───────────┴───────────┐
         │                       │
┌────────────────┐      ┌─────────────────┐
│ PersonalWallet │      │ BusinessWallet  │
└────────────────┘      └─────────────────┘
```

---

### 🎭 C. Polymorphism & Dynamic Dispatch
* **Dynamic Method Dispatch**: Base references (`Wallet wallet`, `Expense expense`) uniformly invoke polymorphic methods like `calculateTransactionLimit()` and `displayDetails()` without requiring runtime type casting.
* **Elimination of Type Switching**: Instead of using brittle `if (wallet instanceof PersonalWallet)` condition blocks throughout services and UI panels, behavior is resolved polymorphically through class hierarchies and Strategy objects.

---

### 🧱 D. Abstraction & Interface Segregation
* **Interface-Driven Design**: Critical system boundaries are defined via interfaces:
  * [`repository.interfaces.*`](file:///c:/Users/anush/Downloads/WalletWise/src/main/java/repository/interfaces): Abstract data access contracts (`IUserRepository`, `IWalletRepository`, `IExpenseRepository`, etc.).
  * [`strategy.SpendingLimitStrategy`](file:///c:/Users/anush/Downloads/WalletWise/src/main/java/strategy/SpendingLimitStrategy.java): Abstraction for spending evaluation rules.
  * [`observer.WalletEventListener`](file:///c:/Users/anush/Downloads/WalletWise/src/main/java/observer/WalletEventListener.java): Abstraction for asynchronous domain event listeners.

---

### 🔗 E. Composition Over Inheritance (HAS-A vs. IS-A)
Rather than forcing deep, rigid inheritance trees, WalletWise favors object composition:
* `User` **HAS-A** `Wallet`.
* `Wallet` **HAS-A** `List<Transaction>` and `List<Expense>`.
* `Wallet` **HAS-A** `SpendingLimitStrategy` (behavioral composition).
* `ExpenseService` **HAS-A** `IExpenseRepository` and `IWalletRepository`.

---

## 🏛️ 2. Deep Dive: SOLID Design Principles

| Principle | Meaning & Purpose | Concrete Implementation in WalletWise |
| :--- | :--- | :--- |
| **S — Single Responsibility Principle (SRP)** | A class should have one, and only one, reason to change. | • `Wallet`: Manages balance state and domain rules.<br>• `WalletRepository`: Handles JDBC persistence.<br>• `WalletService`: Coordinates transactions and domain logic.<br>• `NotificationObserver`: Handles notification creation.<br>• `WalletPanel`: Renders Swing UI and charts. |
| **O — Open/Closed Principle (OCP)** | Software entities should be open for extension, but closed for modification. | • Adding a new wallet type (e.g. `StudentWallet`) or expense type (e.g. `TaxDeductibleExpense`) requires creating a new subclass and strategy—**zero changes to existing services or repositories**. |
| **L — Liskov Substitution Principle (LSP)** | Subtypes must be substitutable for their base types without altering correctness. | • `PersonalWallet` and `BusinessWallet` can be substituted wherever a `Wallet` base reference is expected across `WalletService`, `ExpenseService`, and `ReportService`. |
| **I — Interface Segregation Principle (ISP)** | Clients should not be forced to depend on interfaces they do not use. | • Instead of a single mega-repository, repository contracts are split into cohesive, domain-specific interfaces: `IUserRepository`, `IWalletRepository`, `ITransactionRepository`, `ISavingsGoalRepository`, `IBudgetRepository`. |
| **D — Dependency Inversion Principle (DIP)** | High-level modules should depend on abstractions, not concrete details. | • `WalletService`, `ExpenseService`, and `SavingsGoalService` depend exclusively on repository interfaces (`IWalletRepository`), allowing seamless unit testing and mocking via Mockito without a real database. |

---

## 🎨 3. Design Patterns Applied

### 1. Strategy Pattern (`strategy`)
* **Problem**: `PersonalWallet` enforces an aggregate monthly spending limit, while `BusinessWallet` enforces a per-transaction cap.
* **Solution**: Extracted the limit-checking algorithm into a `SpendingLimitStrategy` interface with `PersonalLimitStrategy` and `BusinessLimitStrategy`.
* **Benefit**: Wallets dynamically delegate limit verification to their assigned strategy (`wallet.isLimitExceeded(amount)`), eliminating hardcoded conditional branches.

```
       ┌────────────────────────┐
       │ SpendingLimitStrategy  │◄─────────────────┐
       └───────────▲────────────┘                  │ (delegates to)
                   │                               │
       ┌───────────┴───────────┐           ┌───────┴──────┐
       │                       │           │    Wallet    │
┌──────────────────────┐ ┌──────────────────────┐  └──────────────┘
│ PersonalLimitStrategy│ │BusinessLimitStrategy │
└──────────────────────┘ └──────────────────────┘
```

---

### 2. Observer Pattern (`observer`)
* **Problem**: Transactions and completed savings goals need to trigger notifications and award reward points without coupling `WalletService` or UI handlers to notification/reward code.
* **Solution**: `WalletEventListener` interface with `NotificationObserver` and `RewardObserver` listeners.
* **Benefit**: `WalletService` emits events (`notifyTransactionCreated`, `notifyGoalAchieved`), and listeners react synchronously and independently.

---

### 3. Static / Simple Factory (`factory`)
* **Problem**: UI forms instantiate specialized wallet or expense subclasses based on user dropdown selections.
* **Solution**: `WalletFactory.createWallet(...)` and `ExpenseFactory.createExpense(...)` centralize and encapsulate object creation parameters in one place.

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
│       STRATEGY LAYER        ││        OBSERVER LAYER        │
│ Spending Limit Calculations ││ Notifications & Gamification │
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
├── pom.xml                                 # Maven configuration & dependencies
├── database/
│   └── schema.sql                          # MySQL relational database schema
├── src/
│   ├── main/java/
│   │   ├── factory/                        # Creational Factories (WalletFactory, ExpenseFactory)
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
│   │   ├── strategy/                       # Spending Limit Strategies (PersonalLimitStrategy, BusinessLimitStrategy)
│   │   └── util/                           # Utilities (DBConnection)
│   └── test/java/
│       ├── integration/                    # Database Transaction Integration Tests (H2)
│       ├── observer/                       # Observer Pattern Unit Tests
│       ├── service/                        # Service Unit Tests (Mockito)
│       └── strategy/                       # Strategy Pattern Unit Tests
└── README.md
```

---

## 🧪 Testing & Verification

The project includes an automated test suite with **100% pass rate**:

* **Unit Tests (Mockito)**: [`UserServiceTest`](file:///c:/Users/anush/Downloads/WalletWise/src/test/java/service/UserServiceTest.java), [`WalletObserverTest`](file:///c:/Users/anush/Downloads/WalletWise/src/test/java/observer/WalletObserverTest.java), [`SpendingLimitStrategyTest`](file:///c:/Users/anush/Downloads/WalletWise/src/test/java/strategy/SpendingLimitStrategyTest.java).
* **Integration Tests (H2 Database)**: [`TransactionIntegrityIntegrationTest`](file:///c:/Users/anush/Downloads/WalletWise/src/test/java/integration/TransactionIntegrityIntegrationTest.java) verifying atomic JDBC transactions, commit, and rollback behavior.

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
