package repository.interfaces;

import model.User;
import java.util.List;

public interface IUserRepository {
    void save(User user);
    User findById(int userId);
    User findByEmail(String email);
    List<User> findAll();
    void update(User user);
    void delete(int userId);
}
