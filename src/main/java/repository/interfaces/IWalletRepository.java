package repository.interfaces;

import model.Wallet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IWalletRepository {
    void save(Wallet wallet, int userId);
    void save(Connection connection, Wallet wallet, int userId) throws SQLException;
    Wallet findById(int walletId);
    Wallet findByUserId(int userId);
    List<Wallet> findAll();
    void update(Wallet wallet);
    void update(Connection connection, Wallet wallet) throws SQLException;
    void delete(int walletId);
}
