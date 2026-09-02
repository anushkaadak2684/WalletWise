package repository;

import repository.interfaces.IUserRepository;
import model.User;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class UserRepository implements IUserRepository {

    // CREATE USER
    public void save(User user) {

        String sql =
                "INSERT INTO users " +
                "(full_name,email,password,phone_number) " +
                "VALUES (?,?,?,?)";

        try(Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    )) {

            statement.setString(
                    1,
                    user.getFullName()
            );

            statement.setString(
                    2,
                    user.getEmail()
            );

            statement.setString(
                    3,
                    user.getPassword()
            );

            statement.setString(
                    4,
                    user.getPhoneNumber()
            );

            statement.executeUpdate();


            // Getting AUTO_INCREMENT ID
            ResultSet rs = statement.getGeneratedKeys();

            if(rs.next()) {
                user.setUserId(rs.getInt(1));
            }
        }
        catch(SQLException e){

            throw new RuntimeException(
                    "Error saving user",
                    e
            );
        }

    }

    // FIND USER BY ID
    public User findById(int userId) {

        String sql = "SELECT * FROM users WHERE user_id=?";

        try(Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    userId
            );

            ResultSet rs = statement.executeQuery();

            if(rs.next()) {
                return mapUser(rs);
            }
        }
        catch(SQLException e){

            throw new RuntimeException(
                    "Error finding user",
                    e
            );
        }
        return null;
    }

    // FIND USER BY EMAIL
    public User findByEmail(String email) {

        String sql = "SELECT * FROM users WHERE email=?";


        try(Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    email
            );

            ResultSet rs = statement.executeQuery();

            if(rs.next()) {
                return mapUser(rs);
            }


        }
        catch(SQLException e){

            throw new RuntimeException(
                    "Error finding user",
                    e
            );

        }
        return null;
    }


    // GET ALL USERS
    public List<User> findAll() {
        List<User> users =new ArrayList<>();

        String sql = "SELECT * FROM users";


        try(Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            ResultSet rs =
                    statement.executeQuery()) {


            while(rs.next()) {
                users.add(
                        mapUser(rs)
                );

            }


        }
        catch(SQLException e){

            throw new RuntimeException(
                    "Error fetching users",
                    e
            );

        }
        return users;
    }

    // UPDATE USER
    public void update(User user) {
        String sql =
                "UPDATE users SET " +
                "full_name=?, " +
                "password=?, " +
                "phone_number=? " +
                "WHERE user_id=?";


        try(Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    user.getFullName()
            );

            statement.setString(
                    2,
                    user.getPassword()
            );

            statement.setString(
                    3,
                    user.getPhoneNumber()
            );

            statement.setInt(
                    4,
                    user.getUserId()
            );

            statement.executeUpdate();

        }
        catch(SQLException e){

            throw new RuntimeException(
                    "Error updating user",
                    e
            );

        }

    }

    // DELETE USER
    public void delete(int userId) {


        String sql = "DELETE FROM users WHERE user_id=?";


        try(Connection connection =
                    DBConnection.getConnection();

            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    userId
            );

            statement.executeUpdate();
        }
        catch(SQLException e){

            throw new RuntimeException(
                    "Error deleting user",
                    e
            );

        }

    }

    // RESULTSET → USER OBJECT
    private User mapUser(ResultSet rs)
            throws SQLException {


        User user = new User();

        user.setUserId(
                rs.getInt("user_id")
        );

        user.setFullName(
                rs.getString("full_name")
        );

        user.setPassword(
                rs.getString("password")
        );

        user.setPhoneNumber(
                rs.getString("phone_number")
        );

        user.setEmail(
                rs.getString("email")
        );
        return user;

    }

}