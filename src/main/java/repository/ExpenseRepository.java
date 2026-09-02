package repository;

import model.Expense;
import model.FixedExpense;
import model.VariableExpense;
import model.enums.ExpenseCategory;
import model.enums.RecurringFrequency;
import repository.interfaces.IExpenseRepository;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ExpenseRepository implements IExpenseRepository {

    @Override
    public void save(Expense expense, int walletId) {
        try (Connection connection = DBConnection.getConnection()) {
            save(connection, expense, walletId);
        } catch (SQLException e) {
            throw new RuntimeException("Error saving expense: " + e.getMessage(), e);
        }
    }

    @Override
    public void save(Connection connection, Expense expense, int walletId) throws SQLException {
        String sql = "INSERT INTO expenses (wallet_id, category, amount, date, description, expense_type, recurring_frequency, maximum_expected_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, walletId);
            statement.setString(2, expense.getCategory().name());
            statement.setBigDecimal(3, expense.getAmount());
            statement.setDate(4, Date.valueOf(expense.getDate()));
            statement.setString(5, expense.getDescription());
            statement.setString(6, expense.getExpenseType());

            if (expense instanceof FixedExpense) {
                FixedExpense fixedExpense = (FixedExpense) expense;
                statement.setString(7, fixedExpense.getRecurringFrequency().name());
                statement.setNull(8, Types.DECIMAL);
            } else if (expense instanceof VariableExpense) {
                VariableExpense variableExpense = (VariableExpense) expense;
                statement.setNull(7, Types.VARCHAR);
                statement.setBigDecimal(8, variableExpense.getMaximumExpectedAmount());
            } else {
                statement.setNull(7, Types.VARCHAR);
                statement.setNull(8, Types.DECIMAL);
            }

            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    expense.setExpenseId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public Expense findById(int expenseId) {
        String sql = "SELECT * FROM expenses WHERE expense_id=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, expenseId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapExpense(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding expense: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Expense> findByWalletId(int walletId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE wallet_id=? ORDER BY date DESC, expense_id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, walletId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapExpense(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching expenses: " + e.getMessage(), e);
        }
        return expenses;
    }

    @Override
    public List<Expense> findAll() {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses ORDER BY date DESC, expense_id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                expenses.add(mapExpense(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching expenses: " + e.getMessage(), e);
        }
        return expenses;
    }

    @Override
    public void update(Expense expense) {
        String sql = "UPDATE expenses SET category=?, amount=?, date=?, description=?, expense_type=?, recurring_frequency=?, maximum_expected_amount=? WHERE expense_id=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, expense.getCategory().name());
            statement.setBigDecimal(2, expense.getAmount());
            statement.setDate(3, Date.valueOf(expense.getDate()));
            statement.setString(4, expense.getDescription());
            statement.setString(5, expense.getExpenseType());

            if (expense instanceof FixedExpense) {
                FixedExpense fixedExpense = (FixedExpense) expense;
                statement.setString(6, fixedExpense.getRecurringFrequency().name());
                statement.setNull(7, Types.DECIMAL);
            } else if (expense instanceof VariableExpense) {
                VariableExpense variableExpense = (VariableExpense) expense;
                statement.setNull(6, Types.VARCHAR);
                statement.setBigDecimal(7, variableExpense.getMaximumExpectedAmount());
            } else {
                statement.setNull(6, Types.VARCHAR);
                statement.setNull(7, Types.DECIMAL);
            }

            statement.setInt(8, expense.getExpenseId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating expense: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int expenseId) {
        String sql = "DELETE FROM expenses WHERE expense_id=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, expenseId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting expense: " + e.getMessage(), e);
        }
    }

    private Expense mapExpense(ResultSet rs) throws SQLException {
        Expense expense;
        String type = rs.getString("expense_type");
        ExpenseCategory category = ExpenseCategory.valueOf(rs.getString("category"));

        if ("FIXED".equalsIgnoreCase(type)) {
            String freqStr = rs.getString("recurring_frequency");
            RecurringFrequency freq = freqStr != null ? RecurringFrequency.valueOf(freqStr) : RecurringFrequency.MONTHLY;
            expense = new FixedExpense(
                    rs.getInt("expense_id"),
                    category,
                    rs.getBigDecimal("amount"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("description"),
                    freq
            );
        } else {
            BigDecimal maxExp = rs.getBigDecimal("maximum_expected_amount");
            expense = new VariableExpense(
                    rs.getInt("expense_id"),
                    category,
                    rs.getBigDecimal("amount"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("description"),
                    maxExp != null ? maxExp : rs.getBigDecimal("amount")
            );
        }
        return expense;
    }
}
