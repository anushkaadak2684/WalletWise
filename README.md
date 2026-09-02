# 💳 WalletWise - Digital Wallet & Personal Finance Tracker

A robust, enterprise-grade Desktop Application built in **Java (Swing)** and **MySQL** for managing digital wallets, tracking expenses, establishing budgets, setting savings goals, and generating comprehensive financial reports. Features a modern dark UI powered by **FlatLaf**, BCrypt security, atomic transactional data integrity, and gamified reward tracking.

---

## 🌟 Key Features

### 🏦 Wallet Management
* **Wallet Types**: Supports **Personal Wallet** (monthly aggregate spending limit) and **Business Wallet** (per-transaction spending cap).
* **Live Balance & Metrics**: Dynamic visual utilization bars, real-time balance updates, and status indicators.
* **Strategy Pattern Spending Limits**: Dynamic polymorphic evaluation of spending thresholds without conditional switching.

### 💳 Transactions (Deposits & Withdrawals)
* Record deposits (income) and withdrawals (expenses/transfers) with custom descriptions.
* Real-time activity history and automated system notification logging.
* **Atomic JDBC Transactions**: Balance updates and ledger entries commit atomically with automatic rollback on error.

### 💸 Expense Tracking & Category Analytics
* **Expense Classification**: Differentiates between **Fixed Expenses** (recurring bills, rent) and **Variable Expenses** (groceries, entertainment).
* **Category Breakdown**: Categorize expenses across `FOOD`, `RENT`, `UTILITIES`, `ENTERTAINMENT`, `SHOPPING`, `HEALTHCARE`, and `OTHER`.
* **Visual Pie Chart**: Custom Swing component rendering interactive expense category distribution in `WalletPanel`.

### 📈 Category Budgeting
* Define category spending limits with start and end dates.
* Real-time progress monitoring comparing limit vs actual spent (`ON TRACK` vs `EXCEEDED`).

### 🎯 Savings Goals & Gamified Rewards
* Establish target savings goals with target dates.
* **Atomic Goal Contributions**: Deposit funds directly into savings goals from active wallet balance with full rollback protection.
* **Gamification**: Earn **+100 Reward Points** and system achievement notifications automatically upon completing a savings goal via the Observer Pattern.

### 📊 Comprehensive Financial Reports
* Generate periodical reports (**Monthly**, **Yearly**, **Custom**).
* **Itemized Side-Pane Viewer**: Synchronized dual-pane view rendering detailed HTML breakdowns (income, expenses, budgets, savings) directly in the side panel.

---

## 🔒 Security Architecture

* **BCrypt Password Hashing**: Passwords are never stored or compared in plaintext. All user credentials are protected using salted BCrypt hashes (cost factor 12) via `at.favre.lib:bcrypt:0.10.2`.
* **Zero-Downtime Migration**: The authentication service seamlessly detects legacy plaintext passwords upon login, authenticates them, and transparently upgrades them to BCrypt hashes in the database.
* **Input Sanitization & Secure Comparison**: Defense against injection and brute-force vulnerabilities.

---

## 🏛️ Architecture & Design Patterns

The system enforces a strict 4-tier Layered Architecture:

```
GUI / Presentation (Swing / FlatLaf)
        │
        ▼
Service Layer (Business Logic & Atomic JDBC Transactions)
        │
        ▼
Repository Layer (CRUD Interface & Implementation)
        │
        ▼
Database (MySQL 8.0+ / H2 In-Memory for Tests)
```

### 🎨 Design Patterns in Detail

1. **Strategy Pattern (`strategy`)**:
   * `SpendingLimitStrategy` defines the spending limit contract.
   * `PersonalLimitStrategy`: Evaluates aggregate monthly limits for personal wallets.
   * `BusinessLimitStrategy`: Evaluates per-transaction caps for business wallets.
   * `Wallet` holds a reference to `SpendingLimitStrategy`, eliminating `instanceof` branches in the presentation and service layers.

2. **Observer Pattern (`observer`)**:
   * `WalletEventListener` defines callbacks for domain events: `onTransactionCreated` and `onSavingsGoalAchieved`.
   * `NotificationObserver`: Synchronously creates persistent system notifications for transaction alerts and savings milestones.
   * `RewardObserver`: Automatically awards bonus points upon achieving savings goals.
   * Eliminates duplicated notification/reward code from UI event handlers.

3. **Simple / Static Factory (`factory`)**:
   * `WalletFactory` and `ExpenseFactory` centralize polymorphic object instantiation for wallet and expense types.

4. **Repository Pattern (`repository.interfaces` & `repository`)**:
   * Data access abstraction providing connection-aware overloads (`save(Connection, ...)`, `update(Connection, ...)`) enabling atomic multi-table transactions across service boundaries.

---

## 🛠️ Technology Stack

* **Language**: Java 17
* **Build System**: Apache Maven
* **GUI Framework**: Java Swing with FlatLaf (FlatDarkLaf 3.5.2)
* **Database**: MySQL 8.0+ (Production) & H2 Database (Integration Testing)
* **Security**: Favre BCrypt 0.10.2
* **Testing**: JUnit 5 (Jupiter 5.10.2) + Mockito 5.11.0

---

## 📁 Project Structure

```
WalletWise/
├── database/
│   └── schema.sql                          # MySQL database schema definition
├── pom.xml                                 # Maven project descriptor & dependencies
├── src/
│   ├── main/java/
│   │   ├── factory/                        # Creational Factories (WalletFactory, ExpenseFactory)
│   │   ├── gui/                            # Presentation Layer (Swing GUI Panels & MainFrame)
│   │   │   ├── Main.java                   # Application Entrypoint
│   │   │   ├── MainFrame.java              # Main Dashboard Window
│   │   │   ├── LoginRegisterFrame.java     # Authentication Frame
│   │   │   ├── WalletPanel.java            # Wallet Metrics & Visual Pie Chart
│   │   │   ├── ExpensePanel.java           # Expense Logging & History
│   │   │   ├── BudgetPanel.java            # Budget Limits & Status
│   │   │   ├── SavingsPanel.java           # Savings Goals & Contributions
│   │   │   ├── NotificationPanel.java      # Notification Center
│   │   │   ├── RewardPanel.java            # Points & Achievements
│   │   │   └── ReportPanel.java            # Financial Reports & Itemized Viewer
│   │   ├── model/                          # Domain Entities (User, Wallet, Expense, etc.)
│   │   │   └── enums/                      # Domain Enumerations (WalletType, ExpenseCategory, etc.)
│   │   ├── observer/                       # Event Observers (NotificationObserver, RewardObserver)
│   │   ├── repository/                     # Repository Implementations (JDBC SQL)
│   │   │   └── interfaces/                 # Repository Interfaces (IUserRepository, IWalletRepository, etc.)
│   │   ├── service/                        # Business Logic & JDBC Transaction Orchestration
│   │   ├── strategy/                       # Spending Limit Strategies (PersonalLimitStrategy, BusinessLimitStrategy)
│   │   └── util/                           # Utilities (DBConnection)
│   └── test/java/
│       ├── integration/                    # Database Transaction Integration Tests (H2)
│       ├── observer/                       # Observer Pattern Unit Tests
│       ├── service/                        # Service Unit Tests (Mockito)
│       └── strategy/                       # Strategy Pattern Unit Tests
├── .env                                    # Local environment configuration (Git ignored)
├── .env.example                            # Environment template
└── README.md                               # Project Documentation
```

---

## 🚀 Getting Started

### 1. Prerequisites
* **Java Development Kit (JDK)**: Java 17 or higher.
* **MySQL Database**: MySQL Server 8.0+ running locally or remotely.

### 2. Database Setup
Execute `database/schema.sql` in your MySQL environment:

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

### 4. Build and Test
Run tests using standard Maven:

```bash
mvn clean test
```

### 5. Running the Application
Launch via Maven or your IDE:

```bash
mvn compile exec:java -Dexec.mainClass="gui.Main"
```
Or directly execute `gui.Main.main()` in VS Code / IntelliJ / Eclipse.
