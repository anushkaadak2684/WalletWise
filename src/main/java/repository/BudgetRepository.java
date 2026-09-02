package repository;

import repository.interfaces.IBudgetRepository;
import model.Budget;
import model.enums.ExpenseCategory;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetRepository implements IBudgetRepository {

    @Override
    public void save(Budget budget, int walletId) {
        String sql = "INSERT INTO budgets (wallet_id, category, limit_amount, spent_amount, start_date, end_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, walletId);
            statement.setString(2, budget.getCategory().name());
            statement.setBigDecimal(3, budget.getLimitAmount());
            statement.setBigDecimal(4, budget.getSpentAmount());
            statement.setDate(5, Date.valueOf(budget.getStartDate()));
            statement.setDate(6, Date.valueOf(budget.getEndDate()));

            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    budget.setBudgetId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving budget: " + e.getMessage(), e);
        }
    }

    @Override
    public Budget findById(int budgetId) {
        String sql = "SELECT * FROM budgets WHERE budget_id=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, budgetId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapBudget(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding budget: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Budget> findByWalletId(int walletId) {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT * FROM budgets WHERE wallet_id=? ORDER BY budget_id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, walletId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    budgets.add(mapBudget(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching budgets: " + e.getMessage(), e);
        }
        return budgets;
    }

    @Override
    public List<Budget> findAll() {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT * FROM budgets ORDER BY budget_id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                budgets.add(mapBudget(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching budgets: " + e.getMessage(), e);
        }
        return budgets;
    }

    @Override
    public void update(Budget budget) {
        try (Connection connection = DBConnection.getConnection()) {
            update(connection, budget);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating budget: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Connection connection, Budget budget) throws SQLException {
        String sql = "UPDATE budgets SET category=?, limit_amount=?, spent_amount=?, start_date=?, end_date=? WHERE budget_id=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, budget.getCategory().name());
            statement.setBigDecimal(2, budget.getLimitAmount());
            statement.setBigDecimal(3, budget.getSpentAmount());
            statement.setDate(4, Date.valueOf(budget.getStartDate()));
            statement.setDate(5, Date.valueOf(budget.getEndDate()));
            statement.setInt(6, budget.getBudgetId());

            statement.executeUpdate();
        }
    }

    @Override
    public void delete(int budgetId) {
        String sql = "DELETE FROM budgets WHERE budget_id=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, budgetId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting budget: " + e.getMessage(), e);
        }
    }

    private Budget mapBudget(ResultSet rs) throws SQLException {
        Budget budget = new Budget(
                rs.getInt("budget_id"),
                ExpenseCategory.valueOf(rs.getString("category")),
                rs.getBigDecimal("limit_amount"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate()
        );
        budget.setSpentAmount(rs.getBigDecimal("spent_amount"));
        return budget;
    }
}
