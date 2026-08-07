package repository.interfaces;

import model.Transaction;
import java.util.List;

public interface ITransactionRepository {
    void save(Transaction transaction, int walletId);
    Transaction findById(int transactionId);
    List<Transaction> findByWalletId(int walletId);
    List<Transaction> findAll();
    void delete(int transactionId);
}
