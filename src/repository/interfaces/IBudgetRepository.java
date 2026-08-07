package repository.interfaces;

import model.Budget;
import java.util.List;

public interface IBudgetRepository {
    void save(Budget budget, int walletId);
    Budget findById(int budgetId);
    List<Budget> findByWalletId(int walletId);
    List<Budget> findAll();
    void update(Budget budget);
    void delete(int budgetId);
}
