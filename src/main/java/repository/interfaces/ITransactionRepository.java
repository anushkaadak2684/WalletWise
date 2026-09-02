package repository.interfaces;

import model.Transaction;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ITransactionRepository {
    void save(Transaction transaction, int walletId);
    void save(Connection connection, Transaction transaction, int walletId) throws SQLException;
    Transaction findById(int transactionId);
    List<Transaction> findByWalletId(int walletId);
    List<Transaction> findAll();
    void delete(int transactionId);
}
