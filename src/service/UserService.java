package service;

import model.User;
import model.Wallet;
import repository.interfaces.IUserRepository;
import repository.UserRepository;

import java.util.List;

public class UserService {
    private IUserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String fullName,
                           String email,
                           String password,
                           String phoneNumber,
                           Wallet wallet) {

        if(fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException(
                    "Name cannot be empty"
            );
        }

        if(email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email cannot be empty"
            );
        }

        if(password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Password cannot be empty"
            );
        }

        User user = new User(
                0,
                fullName,
                email,
                password,
                phoneNumber,
                wallet
        );

        userRepository.save(user);

        return user;
    }

    public void assignWallet(User user,
                             Wallet wallet) {

        if(user == null) {
            throw new IllegalArgumentException(
                    "User cannot be null"
            );
        }

        if(wallet == null) {
            throw new IllegalArgumentException(
                    "Wallet cannot be null"
            );
        }

        user.setWallet(wallet);
    }

    public User getUserById(int userId) {
        return userRepository.findById(userId);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateProfile(User user,
                              String fullName,
                              String phoneNumber) {

        if(user == null) {
            throw new IllegalArgumentException(
                    "User cannot be null"
            );
        }

        user.setFullName(fullName);
        user.setPhoneNumber(phoneNumber);

        userRepository.update(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(int userId) {
        userRepository.delete(userId);
    }

    public void showUsers() {
        List<User> users = userRepository.findAll();

        if(users.isEmpty()) {
            System.out.println("No users available");
            return;
        }

        for(User user : users) {
            user.displayProfile();
        }
    }
}