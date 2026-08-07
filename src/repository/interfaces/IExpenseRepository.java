package repository.interfaces;

import model.Expense;
import java.util.List;

public interface IExpenseRepository {
    void save(Expense expense, int walletId);
    Expense findById(int expenseId);
    List<Expense> findByWalletId(int walletId);
    List<Expense> findAll();
    void update(Expense expense);
    void delete(int expenseId);
}
