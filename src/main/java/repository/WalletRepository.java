package repository;

import model.BusinessWallet;
import model.PersonalWallet;
import model.Wallet;
import model.enums.WalletType;
import repository.interfaces.IWalletRepository;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class WalletRepository implements IWalletRepository {

    @Override
    public void save(Wallet wallet, int userId) {
        try (Connection connection = DBConnection.getConnection()) {
            save(connection, wallet, userId);
        } catch (SQLException e) {
            throw new RuntimeException("Error saving wallet: " + e.getMessage(), e);
        }
    }

    @Override
    public void save(Connection connection, Wallet wallet, int userId) throws SQLException {
        String sql = "INSERT INTO wallets (user_id, balance, wallet_type, monthly_spending_limit, business_transaction_limit) VALUES (?,?,?,?,?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, userId);
            statement.setBigDecimal(2, wallet.getBalance());
            statement.setString(3, wallet.getWalletType().name());

            if (wallet instanceof PersonalWallet) {
                PersonalWallet personal = (PersonalWallet) wallet;
                statement.setBigDecimal(4, personal.getMonthlySpendingLimit());
                statement.setNull(5, Types.DECIMAL);
            } else if (wallet instanceof BusinessWallet) {
                BusinessWallet business = (BusinessWallet) wallet;
                statement.setNull(4, Types.DECIMAL);
                statement.setBigDecimal(5, business.getBusinessTransactionLimit());
            } else {
                statement.setNull(4, Types.DECIMAL);
                statement.setNull(5, Types.DECIMAL);
            }

            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    wallet.setWalletId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public Wallet findById(int walletId) {
        String sql = "SELECT * FROM wallets WHERE wallet_id=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, walletId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapWallet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding wallet by id: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Wallet findByUserId(int userId) {
        String sql = "SELECT * FROM wallets WHERE user_id=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapWallet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding wallet by user id: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Wallet> findAll() {
        List<Wallet> wallets = new ArrayList<>();
        String sql = "SELECT * FROM wallets";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                wallets.add(mapWallet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching wallets: " + e.getMessage(), e);
        }
        return wallets;
    }

    @Override
    public void update(Wallet wallet) {
        try (Connection connection = DBConnection.getConnection()) {
            update(connection, wallet);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating wallet: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Connection connection, Wallet wallet) throws SQLException {
        String sql = "UPDATE wallets SET balance=?, wallet_type=?, monthly_spending_limit=?, business_transaction_limit=? WHERE wallet_id=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBigDecimal(1, wallet.getBalance());
            statement.setString(2, wallet.getWalletType().name());

            if (wallet instanceof PersonalWallet) {
                PersonalWallet personal = (PersonalWallet) wallet;
                statement.setBigDecimal(3, personal.getMonthlySpendingLimit());
                statement.setNull(4, Types.DECIMAL);
            } else if (wallet instanceof BusinessWallet) {
                BusinessWallet business = (BusinessWallet) wallet;
                statement.setNull(3, Types.DECIMAL);
                statement.setBigDecimal(4, business.getBusinessTransactionLimit());
            } else {
                statement.setNull(3, Types.DECIMAL);
                statement.setNull(4, Types.DECIMAL);
            }

            statement.setInt(5, wallet.getWalletId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(int walletId) {
        String sql = "DELETE FROM wallets WHERE wallet_id=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, walletId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting wallet: " + e.getMessage(), e);
        }
    }

    private Wallet mapWallet(ResultSet rs) throws SQLException {
        Wallet wallet;
        WalletType type = WalletType.valueOf(rs.getString("wallet_type"));

        if (type == WalletType.PERSONAL) {
            wallet = new PersonalWallet(
                    rs.getInt("wallet_id"),
                    rs.getBigDecimal("balance"),
                    rs.getBigDecimal("monthly_spending_limit")
            );
        } else {
            wallet = new BusinessWallet(
                    rs.getInt("wallet_id"),
                    rs.getBigDecimal("balance"),
                    rs.getBigDecimal("business_transaction_limit")
            );
        }
        return wallet;
    }
}
