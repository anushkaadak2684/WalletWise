package repository;

import repository.interfaces.IExpenseRepository;
import model.*;
import model.enums.*;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseRepository implements IExpenseRepository {

    // Save Expense
    public void save(Expense expense, int walletId) {

        String sql = "INSERT INTO expenses " +
                "(wallet_id, category, amount, date, description, expense_type, recurring_frequency, maximum_expected_amount) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, walletId);
            statement.setString(2, expense.getCategory().name());
            statement.setBigDecimal(3, expense.getAmount());
            statement.setDate(4, Date.valueOf(expense.getDate()));
            statement.setString(5, expense.getDescription());
            statement.setString(6, expense.getExpenseType());

            if(expense instanceof FixedExpense) {

                FixedExpense fixedExpense = (FixedExpense) expense;

                statement.setString(7,
                        fixedExpense.getRecurringFrequency().name());

                statement.setNull(8, Types.DECIMAL);

            } else if(expense instanceof VariableExpense) {

                VariableExpense variableExpense = (VariableExpense) expense;

                statement.setNull(7, Types.VARCHAR);

                statement.setBigDecimal(8,
                        variableExpense.getMaximumExpectedAmount());
            }

            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();

            if(rs.next()) {
                expense.setExpenseId(rs.getInt(1));
            }

        } catch(SQLException e) {
            throw new RuntimeException("Error saving expense", e);
        }
    }


    // Find Expense By ID
    public Expense findById(int expenseId) {

        String sql = "SELECT * FROM expenses WHERE expense_id=?";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, expenseId);

            ResultSet rs = statement.executeQuery();

            if(rs.next()) {
                return mapExpense(rs);
            }

        } catch(SQLException e) {
            throw new RuntimeException("Error finding expense", e);
        }

        return null;
    }


    // Find Expenses By Wallet
    public List<Expense> findByWalletId(int walletId) {

        List<Expense> expenses = new ArrayList<>();

        String sql = "SELECT * FROM expenses WHERE wallet_id=?";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setInt(1, walletId);

            ResultSet rs = statement.executeQuery();

            while(rs.next()) {
                expenses.add(mapExpense(rs));
            }

        } catch(SQLException e) {
            throw new RuntimeException("Error fetching expenses", e);
        }

        return expenses;
    }


    // Get All Expenses
    public List<Expense> findAll() {

        List<Expense> expenses = new ArrayList<>();

        String sql = "SELECT * FROM expenses";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery()) {

            while(rs.next()) {
                expenses.add(mapExpense(rs));
            }

        } catch(SQLException e) {
            throw new RuntimeException("Error fetching expenses", e);
        }

        return expenses;
    }


    // Update Expense
    public void update(Expense expense) {

        String sql =
                "UPDATE expenses SET category=?, amount=?, date=?, description=?, " +
                "expense_type=?, recurring_frequency=?, maximum_expected_amount=? " +
                "WHERE expense_id=?";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setString(1,
                    expense.getCategory().name());

            statement.setBigDecimal(2,
                    expense.getAmount());

            statement.setDate(3,
                    Date.valueOf(expense.getDate()));

            statement.setString(4,
                    expense.getDescription());

            statement.setString(5,
                    expense.getExpenseType());

            if(expense instanceof FixedExpense) {

                FixedExpense fixedExpense =
                        (FixedExpense) expense;

                statement.setString(6,
                        fixedExpense.getRecurringFrequency().name());

                statement.setNull(7, Types.DECIMAL);

            } else {

                VariableExpense variableExpense = (VariableExpense) expense;

                statement.setNull(6, Types.VARCHAR);

                statement.setBigDecimal(7,
                        variableExpense.getMaximumExpectedAmount());
            }

            statement.setInt(8,
                    expense.getExpenseId());

            statement.executeUpdate();

        } catch(SQLException e) {
            throw new RuntimeException("Error updating expense", e);
        }
    }


    // Delete Expense
    public void delete(int expenseId) {

        String sql = "DELETE FROM expenses WHERE expense_id=?";

        try(Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setInt(1, expenseId);
            statement.executeUpdate();

        } catch(SQLException e) {
            throw new RuntimeException("Error deleting expense", e);
        }
    }


    // ResultSet -> Expense Object
    private Expense mapExpense(ResultSet rs)
            throws SQLException {

        Expense expense;

        String type = rs.getString("expense_type");

        ExpenseCategory category =
                ExpenseCategory.valueOf(
                        rs.getString("category"));

        if(type.equals("FIXED")) {

            expense = new FixedExpense(
                    rs.getInt("expense_id"),
                    category,
                    rs.getBigDecimal("amount"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("description"),
                    RecurringFrequency.valueOf(
                            rs.getString("recurring_frequency"))
            );

        } else {

            expense = new VariableExpense(
                    rs.getInt("expense_id"),
                    category,
                    rs.getBigDecimal("amount"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("description"),
                    rs.getBigDecimal(
                            "maximum_expected_amount")
            );
        }

        return expense;
    }
}
