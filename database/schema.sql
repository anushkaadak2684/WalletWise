CREATE DATABASE IF NOT EXISTS finance_manager;

USE finance_manager;


-- =========================
-- USERS
-- =========================

CREATE TABLE users(
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15)
);


-- =========================
-- WALLETS
-- Single Table Inheritance:
-- PersonalWallet / BusinessWallet
-- =========================

CREATE TABLE wallets(
    wallet_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNIQUE,
    balance DECIMAL(10,2) DEFAULT 0,
    wallet_type VARCHAR(20) NOT NULL,
    monthly_spending_limit DECIMAL(10,2),
    business_transaction_limit DECIMAL(10,2);

    FOREIGN KEY(user_id)
    REFERENCES users(user_id)
    ON DELETE CASCADE
);


-- =========================
-- TRANSACTIONS
-- =========================

CREATE TABLE transactions(
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    wallet_id INT,
    transaction_type VARCHAR(30) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    transaction_date DATETIME NOT NULL,
    description VARCHAR(255),

    FOREIGN KEY(wallet_id)
    REFERENCES wallets(wallet_id)
    ON DELETE CASCADE
);


-- =========================
-- EXPENSES
-- Single Table Inheritance:
-- FixedExpense / VariableExpense
-- =========================

CREATE TABLE expenses(
    expense_id INT PRIMARY KEY AUTO_INCREMENT,
    wallet_id INT,

    category VARCHAR(50) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    date DATE NOT NULL,
    description VARCHAR(255),

    expense_type VARCHAR(30) NOT NULL,

    recurring_frequency VARCHAR(30),
    maximum_expected_amount DECIMAL(10,2),

    FOREIGN KEY(wallet_id)
    REFERENCES wallets(wallet_id)
    ON DELETE CASCADE
);


-- =========================
-- BUDGETS
-- =========================

CREATE TABLE budgets(
    budget_id INT PRIMARY KEY AUTO_INCREMENT,
    wallet_id INT,

    category VARCHAR(50) NOT NULL,

    limit_amount DECIMAL(10,2) NOT NULL,
    spent_amount DECIMAL(10,2) DEFAULT 0,

    start_date DATE NOT NULL,
    end_date DATE NOT NULL,

    FOREIGN KEY(wallet_id)
    REFERENCES wallets(wallet_id)
    ON DELETE CASCADE
);


-- =========================
-- SAVINGS GOALS
-- =========================

CREATE TABLE savings_goals(
    goal_id INT PRIMARY KEY AUTO_INCREMENT,
    wallet_id INT,

    goal_name VARCHAR(100) NOT NULL,

    target_amount DECIMAL(10,2) NOT NULL,
    saved_amount DECIMAL(10,2) DEFAULT 0,

    target_date DATE,

    FOREIGN KEY(wallet_id)
    REFERENCES wallets(wallet_id)
    ON DELETE CASCADE
);


-- =========================
-- NOTIFICATIONS
-- =========================

CREATE TABLE notifications(
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,

    message VARCHAR(255) NOT NULL,
    notification_type VARCHAR(50) NOT NULL,

    read_status BOOLEAN DEFAULT FALSE,

    created_at DATETIME NOT NULL,

    FOREIGN KEY(user_id)
    REFERENCES users(user_id)
    ON DELETE CASCADE
);


-- =========================
-- REWARDS
-- =========================

CREATE TABLE rewards(
    reward_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,

    reward_name VARCHAR(100) NOT NULL,

    points INT DEFAULT 0,

    description VARCHAR(255),

    earned_date DATE NOT NULL,

    FOREIGN KEY(user_id)
    REFERENCES users(user_id)
    ON DELETE CASCADE
);


-- =========================
-- REPORTS
-- =========================

CREATE TABLE reports(
    report_id INT PRIMARY KEY AUTO_INCREMENT,
    wallet_id INT,

    report_type VARCHAR(50) NOT NULL,

    generated_date DATE NOT NULL,

    total_income DECIMAL(10,2) DEFAULT 0,
    total_expense DECIMAL(10,2) DEFAULT 0,
    total_savings DECIMAL(10,2) DEFAULT 0,
    summary_details LONGTEXT,

    FOREIGN KEY(wallet_id)
    REFERENCES wallets(wallet_id)
    ON DELETE CASCADE
);