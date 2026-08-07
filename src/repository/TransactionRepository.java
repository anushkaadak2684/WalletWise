package repository;

import repository.interfaces.ITransactionRepository;
import model.Transaction;
import model.enums.TransactionType;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository implements ITransactionRepository {

    // Save Transaction
    public void save(Transaction transaction, int walletId) {

        String sql = "INSERT INTO transactions " +
                "(wallet_id, transaction_type, amount, transaction_date, description) " +
                "VALUES (?, ?, ?, ?, ?)";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, walletId);
            statement.setString(2, transaction.getTransactionType().name());
            statement.setBigDecimal(3, transaction.getAmount());
            statement.setTimestamp(4,
                    Timestamp.valueOf(transaction.getTransactionDate()));
            statement.setString(5, transaction.getDescription());

            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();

            if(rs.next()) {
                transaction.setTransactionId(rs.getInt(1));
            }

        } catch(SQLException e) {
            throw new RuntimeException("Error saving transaction", e);
        }
    }


    // Find Transaction By ID
    public Transaction findById(int transactionId) {

        String sql = "SELECT * FROM transactions WHERE transaction_id=?";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setInt(1, transactionId);

            ResultSet rs = statement.executeQuery();

            if(rs.next()) {
                return mapTransaction(rs);
            }

        } catch(SQLException e) {
            throw new RuntimeException("Error finding transaction", e);
        }

        return null;
    }


    // Find Transactions By Wallet
    public List<Transaction> findByWalletId(int walletId) {

        List<Transaction> transactions = new ArrayList<>();

        String sql = "SELECT * FROM transactions WHERE wallet_id=?";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setInt(1, walletId);

            ResultSet rs = statement.executeQuery();

            while(rs.next()) {
                transactions.add(mapTransaction(rs));
            }

        } catch(SQLException e) {
            throw new RuntimeException("Error fetching transactions", e);
        }

        return transactions;
    }


    // Get All Transactions
    public List<Transaction> findAll() {

        List<Transaction> transactions = new ArrayList<>();

        String sql = "SELECT * FROM transactions";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery()) {

            while(rs.next()) {
                transactions.add(mapTransaction(rs));
            }

        } catch(SQLException e) {
            throw new RuntimeException("Error fetching transactions", e);
        }

        return transactions;
    }


    // Update Transaction
    public void update(Transaction transaction) {

        String sql =
                "UPDATE transactions SET " +
                "transaction_type=?, amount=?, " +
                "transaction_date=?, description=? " +
                "WHERE transaction_id=?";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setString(1,
                    transaction.getTransactionType().name());

            statement.setBigDecimal(2,
                    transaction.getAmount());

            statement.setTimestamp(3,
                    Timestamp.valueOf(
                            transaction.getTransactionDate()));

            statement.setString(4,
                    transaction.getDescription());

            statement.setInt(5,
                    transaction.getTransactionId());

            statement.executeUpdate();

        } catch(SQLException e) {
            throw new RuntimeException("Error updating transaction", e);
        }
    }


    // Delete Transaction
    public void delete(int transactionId) {

        String sql = "DELETE FROM transactions WHERE transaction_id=?";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setInt(1, transactionId);

            statement.executeUpdate();

        } catch(SQLException e) {
            throw new RuntimeException("Error deleting transaction", e);
        }
    }


    // ResultSet -> Transaction Object
    private Transaction mapTransaction(ResultSet rs)
            throws SQLException {

        Transaction transaction = new Transaction();

        transaction.setTransactionId(
                rs.getInt("transaction_id"));

        transaction.setTransactionType(
                TransactionType.valueOf(
                        rs.getString("transaction_type")));

        transaction.setAmount(
                rs.getBigDecimal("amount"));

        transaction.setTransactionDate(
                rs.getTimestamp("transaction_date")
                        .toLocalDateTime());

        transaction.setDescription(
                rs.getString("description"));

        return transaction;
    }
}
