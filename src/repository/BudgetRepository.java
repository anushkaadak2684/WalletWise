package repository;

import repository.interfaces.IBudgetRepository;
import model.Budget;
import model.enums.ExpenseCategory;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetRepository implements IBudgetRepository {

    // Save Budget
    public void save(Budget budget, int walletId) {

        String sql = "INSERT INTO budgets " +
                "(wallet_id, category, limit_amount, spent_amount, start_date, end_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, walletId);
            statement.setString(2, budget.getCategory().name());
            statement.setBigDecimal(3, budget.getLimitAmount());
            statement.setBigDecimal(4, budget.getSpentAmount());
            statement.setDate(5, Date.valueOf(budget.getStartDate()));
            statement.setDate(6, Date.valueOf(budget.getEndDate()));

            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();

            if(rs.next()) {
                budget.setBudgetId(rs.getInt(1));
            }

        } catch(SQLException e) {
            throw new RuntimeException("Error saving budget", e);
        }
    }


    // Find Budget By ID
    public Budget findById(int budgetId) {

        String sql = "SELECT * FROM budgets WHERE budget_id=?";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setInt(1, budgetId);

            ResultSet rs = statement.executeQuery();

            if(rs.next()) {
                return mapBudget(rs);
            }

        } catch(SQLException e) {
            throw new RuntimeException("Error finding budget", e);
        }

        return null;
    }


    // Find Budgets By Wallet
    public List<Budget> findByWalletId(int walletId) {

        List<Budget> budgets = new ArrayList<>();

        String sql =
                "SELECT * FROM budgets WHERE wallet_id=?";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setInt(1, walletId);

            ResultSet rs = statement.executeQuery();

            while(rs.next()) {
                budgets.add(mapBudget(rs));
            }

        } catch(SQLException e) {
            throw new RuntimeException("Error fetching budgets", e);
        }

        return budgets;
    }


    // Get All Budgets
    public List<Budget> findAll() {

        List<Budget> budgets = new ArrayList<>();

        String sql = "SELECT * FROM budgets";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery()) {

            while(rs.next()) {
                budgets.add(mapBudget(rs));
            }

        } catch(SQLException e) {
            throw new RuntimeException("Error fetching budgets", e);
        }

        return budgets;
    }


    // Update Budget
    public void update(Budget budget) {

        String sql =
                "UPDATE budgets SET category=?, limit_amount=?, " +
                "spent_amount=?, start_date=?, end_date=? WHERE budget_id=?";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setString(1,
                    budget.getCategory().name());

            statement.setBigDecimal(2,
                    budget.getLimitAmount());

            statement.setBigDecimal(3,
                    budget.getSpentAmount());

            statement.setDate(4,
                    Date.valueOf(budget.getStartDate()));

            statement.setDate(5,
                    Date.valueOf(budget.getEndDate()));

            statement.setInt(6,
                    budget.getBudgetId());

            statement.executeUpdate();

        } catch(SQLException e) {
            throw new RuntimeException("Error updating budget", e);
        }
    }


    // Delete Budget
    public void delete(int budgetId) {

        String sql ="DELETE FROM budgets WHERE budget_id=?";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setInt(1, budgetId);

            statement.executeUpdate();

        } catch(SQLException e) {
            throw new RuntimeException("Error deleting budget", e);
        }
    }


    // ResultSet -> Budget Object
    private Budget mapBudget(ResultSet rs)
        throws SQLException {

        Budget budget = new Budget(
            rs.getInt("budget_id"),
            ExpenseCategory.valueOf(
                    rs.getString("category")),
            rs.getBigDecimal("limit_amount"),
            rs.getDate("start_date").toLocalDate(),
            rs.getDate("end_date").toLocalDate()
        );

        budget.setSpentAmount(rs.getBigDecimal("spent_amount"));

        return budget;
    }
}
