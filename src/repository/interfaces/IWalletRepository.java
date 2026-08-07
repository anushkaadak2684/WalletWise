package repository.interfaces;

import model.Wallet;
import java.util.List;

public interface IWalletRepository {
    void save(Wallet wallet, int userId);
    Wallet findById(int walletId);
    Wallet findByUserId(int userId);
    List<Wallet> findAll();
    void update(Wallet wallet);
    void delete(int walletId);
}
