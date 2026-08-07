package repository;

import repository.interfaces.IWalletRepository;
import model.*;
import model.enums.*;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class WalletRepository implements IWalletRepository {

    // SAVE WALLET
    public void save(Wallet wallet, int userId) {

        String sql =
                "INSERT INTO wallets " +
                "(user_id,balance,wallet_type," +
                "monthly_spending_limit," +
                "business_transaction_limit) " +
                "VALUES (?,?,?,?,?)";


        try(Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    )) {

            statement.setInt(
                    1,
                    userId
            );

            statement.setBigDecimal(
                    2,
                    wallet.getBalance()
            );


            statement.setString(
                    3,
                    wallet.getWalletType().name()
            );


            // Personal Wallet field
            if(wallet instanceof PersonalWallet) {

                PersonalWallet personal =
                        (PersonalWallet) wallet;


                statement.setBigDecimal(
                        4,
                        personal.getMonthlySpendingLimit()
                );

                statement.setNull(
                        5,
                        Types.DECIMAL
                );

            }

            // Business Wallet field
            else if(wallet instanceof BusinessWallet) {

                BusinessWallet business =
                        (BusinessWallet) wallet;

                statement.setNull(
                        4,
                        Types.DECIMAL
                );

                statement.setBigDecimal(
                        5,
                        business.getBusinessTransactionLimit()
                );

            }
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();

            if(rs.next()) {
                wallet.setWalletId(
                        rs.getInt(1)
                );

            }


        }
        catch(SQLException e){

            throw new RuntimeException(
                    "Error saving wallet",
                    e
            );

        }

    }

    // FIND WALLET BY ID
    public Wallet findById(int walletId) {

        String sql = "SELECT * FROM wallets WHERE wallet_id=?";

        try(Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    walletId
            );

            ResultSet rs = statement.executeQuery();
            if(rs.next()) {
                return mapWallet(rs);
            }
        }
        catch(SQLException e){

            throw new RuntimeException(
                    "Error finding wallet",
                    e
            );

        }
        return null;

    }

    // FIND WALLET BY USER
    public Wallet findByUserId(int userId) {
        String sql = "SELECT * FROM wallets WHERE user_id=?";

        try(Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    userId
            );

            ResultSet rs = statement.executeQuery();
            if(rs.next()) {
                return mapWallet(rs);
            }
        }
        catch(SQLException e){

            throw new RuntimeException(
                    "Error finding wallet",
                    e
            );

        }
        return null;

    }

    // GET ALL WALLETS
    public List<Wallet> findAll() {
        List<Wallet> wallets = new ArrayList<>();

        String sql = "SELECT * FROM wallets";


        try(Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            ResultSet rs =
                    statement.executeQuery()) {

            while(rs.next()) {
                wallets.add(mapWallet(rs));
            }

        }
        catch(SQLException e){

            throw new RuntimeException(
                    "Error fetching wallets",
                    e
            );

        }

        return wallets;
    }

    // UPDATE WALLET
    public void update(Wallet wallet) {
        String sql =
                "UPDATE wallets SET " +
                "balance=?, " +
                "wallet_type=?, " +
                "monthly_spending_limit=?, " +
                "business_transaction_limit=? " +
                "WHERE wallet_id=?";


        try(Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setBigDecimal(
                    1,
                    wallet.getBalance()
            );

            statement.setString(
                    2,
                    wallet.getWalletType().name()
            );


            if(wallet instanceof PersonalWallet) {
                PersonalWallet personal =
                        (PersonalWallet) wallet;


                statement.setBigDecimal(
                        3,
                        personal.getMonthlySpendingLimit()
                );

                statement.setNull(
                        4,
                        Types.DECIMAL
                );

            }
            else {
                BusinessWallet business =
                        (BusinessWallet) wallet;

                statement.setNull(
                        3,
                        Types.DECIMAL
                );

                statement.setBigDecimal(
                        4,
                        business.getBusinessTransactionLimit()
                );

            }


            statement.setInt(
                    5,
                    wallet.getWalletId()
            );
            statement.executeUpdate();
        }
        catch(SQLException e){

            throw new RuntimeException(
                    "Error updating wallet",
                    e
            );

        }

    }

    // DELETE WALLET
    public void delete(int walletId) {


        String sql = "DELETE FROM wallets WHERE wallet_id=?";


        try(Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)) {


            statement.setInt(
                    1,
                    walletId
            );

            statement.executeUpdate();
        }
        catch(SQLException e){

            throw new RuntimeException(
                    "Error deleting wallet",
                    e
            );

        }

    }

    // RESULTSET → OBJECT
    private Wallet mapWallet(ResultSet rs)
            throws SQLException {


        Wallet wallet;

        WalletType type =
                WalletType.valueOf(
                        rs.getString("wallet_type")
                );

        if(type == WalletType.PERSONAL) {
            wallet =
                new PersonalWallet(
                    rs.getInt("wallet_id"),
                    rs.getBigDecimal("balance"),
                    rs.getBigDecimal(
                         "monthly_spending_limit"
                    )
                );

        }

        else {
            wallet =
                new BusinessWallet(
                    rs.getInt("wallet_id"),
                    rs.getBigDecimal("balance"),
                    rs.getBigDecimal(
                         "business_transaction_limit"
                    )
                );

        }
        return wallet;
    }
}
