package service;

import at.favre.lib.crypto.bcrypt.BCrypt;
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
                           String phoneNumber) {
        return createUser(fullName, email, password, phoneNumber, null);
    }

    public User createUser(String fullName,
                           String email,
                           String password,
                           String phoneNumber,
                           Wallet wallet) {

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Securely hash password using BCrypt (cost factor 12)
        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        User user = new User(
                0,
                fullName.trim(),
                email.trim(),
                hashedPassword,
                phoneNumber != null ? phoneNumber.trim() : null,
                wallet
        );

        userRepository.save(user);
        return user;
    }

    /**
     * Authenticates a user by email and plaintext password.
     * Verifies against BCrypt hash and transparently upgrades legacy plaintext records.
     */
    public User authenticate(String email, String plaintextPassword) {
        if (email == null || email.isBlank() || plaintextPassword == null || plaintextPassword.isBlank()) {
            throw new IllegalArgumentException("Email and password cannot be empty");
        }

        User user = userRepository.findByEmail(email.trim());
        if (user == null) {
            return null;
        }

        String stored = user.getPassword();
        if (stored == null) {
            return null;
        }

        // Check if stored password is in BCrypt format
        if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
            BCrypt.Result result = BCrypt.verifyer().verify(plaintextPassword.toCharArray(), stored);
            if (result.verified) {
                return user;
            }
        } else {
            // Legacy plaintext fallback check with transparent migration to BCrypt
            if (stored.equals(plaintextPassword)) {
                String upgradedHash = BCrypt.withDefaults().hashToString(12, plaintextPassword.toCharArray());
                user.setPassword(upgradedHash);
                userRepository.update(user);
                return user;
            }
        }

        return null;
    }

    public void assignWallet(User user, Wallet wallet) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (wallet == null) {
            throw new IllegalArgumentException("Wallet cannot be null");
        }
        user.setWallet(wallet);
    }

    public User getUserById(int userId) {
        return userRepository.findById(userId);
    }

    public User getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return userRepository.findByEmail(email.trim());
    }

    public void updateProfile(User user, String fullName, String phoneNumber) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
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
        if (users.isEmpty()) {
            System.out.println("No users available");
            return;
        }
        for (User user : users) {
            user.displayProfile();
        }
    }
}