package integration;

import model.PersonalWallet;
import model.Transaction;
import model.enums.TransactionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repository.TransactionRepository;
import repository.WalletRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionIntegrityIntegrationTest {

    private Connection conn;
    private WalletRepository walletRepository;
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() throws SQLException {
        // Use H2 in-memory database with MySQL compatibility mode for fast, isolated integration tests
        conn = DriverManager.getConnection("jdbc:h2:mem:walletwise_test;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        walletRepository = new WalletRepository();
        transactionRepository = new TransactionRepository();

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "full_name VARCHAR(100) NOT NULL," +
                    "email VARCHAR(100) NOT NULL UNIQUE," +
                    "password VARCHAR(255) NOT NULL," +
                    "phone_number VARCHAR(15)" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS wallets (" +
                    "wallet_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "balance DECIMAL(10,2) NOT NULL DEFAULT 0.00," +
                    "wallet_type ENUM('PERSONAL','BUSINESS') NOT NULL," +
                    "monthly_spending_limit DECIMAL(10,2)," +
                    "business_transaction_limit DECIMAL(10,2)" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                    "transaction_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "wallet_id INT NOT NULL," +
                    "transaction_type ENUM('DEPOSIT','WITHDRAWAL','EXPENSE') NOT NULL," +
                    "amount DECIMAL(10,2) NOT NULL," +
                    "transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "description VARCHAR(255)" +
                    ");");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DROP ALL OBJECTS");
            }
            conn.close();
        }
    }

    @Test
    @DisplayName("Atomic commit: Wallet balance update and transaction record are persisted together")
    void testAtomicTransactionCommit() throws SQLException {
        // Setup initial wallet with balance ₹1000
        PersonalWallet wallet = new PersonalWallet(0, new BigDecimal("1000.00"), new BigDecimal("50000.00"));
        walletRepository.save(conn, wallet, 1);
        int walletId = wallet.getWalletId();

        // Perform transaction
        conn.setAutoCommit(false);
        try {
            wallet.deposit(new BigDecimal("500.00"));
            walletRepository.update(conn, wallet);

            Transaction tx = new Transaction(0, TransactionType.DEPOSIT, new BigDecimal("500.00"), "Direct Deposit");
            transactionRepository.save(conn, tx, walletId);

            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            fail("Transaction should have committed successfully");
        } finally {
            conn.setAutoCommit(true);
        }

        // Verify state
        try (Statement stmt = conn.createStatement()) {
            var rs = stmt.executeQuery("SELECT balance FROM wallets WHERE wallet_id=" + walletId);
            assertTrue(rs.next());
            assertEquals(new BigDecimal("1500.00"), rs.getBigDecimal("balance"));

            var txRs = stmt.executeQuery("SELECT COUNT(*) FROM transactions WHERE wallet_id=" + walletId);
            assertTrue(txRs.next());
            assertEquals(1, txRs.getInt(1));
        }
    }

    @Test
    @DisplayName("Atomic rollback: On midway error, wallet balance rollback occurs and no orphan records exist")
    void testAtomicTransactionRollback() throws SQLException {
        // Setup initial wallet with balance ₹1000
        PersonalWallet wallet = new PersonalWallet(0, new BigDecimal("1000.00"), new BigDecimal("50000.00"));
        walletRepository.save(conn, wallet, 2);
        int walletId = wallet.getWalletId();

        // Perform transaction that encounters an intentional failure midway
        conn.setAutoCommit(false);
        try {
            wallet.deposit(new BigDecimal("500.00"));
            walletRepository.update(conn, wallet);

            // Simulate failure before commit (e.g. database constraint / network error)
            throw new RuntimeException("Simulated unexpected failure during payment processing");

        } catch (Exception e) {
            conn.rollback();
        } finally {
            conn.setAutoCommit(true);
        }

        // Verify balance was NOT updated and rolled back to ₹1000.00
        try (Statement stmt = conn.createStatement()) {
            var rs = stmt.executeQuery("SELECT balance FROM wallets WHERE wallet_id=" + walletId);
            assertTrue(rs.next());
            assertEquals(new BigDecimal("1000.00"), rs.getBigDecimal("balance"));

            var txRs = stmt.executeQuery("SELECT COUNT(*) FROM transactions WHERE wallet_id=" + walletId);
            assertTrue(txRs.next());
            assertEquals(0, txRs.getInt(1));
        }
    }
}
