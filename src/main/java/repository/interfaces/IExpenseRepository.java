package repository.interfaces;

import model.Expense;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IExpenseRepository {
    void save(Expense expense, int walletId);
    void save(Connection connection, Expense expense, int walletId) throws SQLException;
    Expense findById(int expenseId);
    List<Expense> findByWalletId(int walletId);
    List<Expense> findAll();
    void update(Expense expense);
    void delete(int expenseId);
}
