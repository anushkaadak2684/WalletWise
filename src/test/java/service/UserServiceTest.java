package service;

import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.interfaces.IUserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    @DisplayName("createUser() hashes plaintext password with BCrypt before saving")
    void testCreateUserHashesPassword() {
        String rawPassword = "SecretPassword123!";
        User user = userService.createUser("John Doe", "john@example.com", rawPassword, "9876543210");

        assertNotNull(user);
        assertNotEquals(rawPassword, user.getPassword());
        assertTrue(user.getPassword().startsWith("$2a$") || user.getPassword().startsWith("$2b$"));

        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("authenticate() succeeds with correct BCrypt password")
    void testAuthenticateSuccess() {
        String rawPassword = "MySecurePassword";
        String hashed = at.favre.lib.crypto.bcrypt.BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray());

        User existingUser = new User(1, "Alice", "alice@example.com", hashed, "1234567890");
        when(userRepository.findByEmail("alice@example.com")).thenReturn(existingUser);

        User authenticated = userService.authenticate("alice@example.com", rawPassword);
        assertNotNull(authenticated);
        assertEquals(1, authenticated.getUserId());
    }

    @Test
    @DisplayName("authenticate() returns null on incorrect password")
    void testAuthenticateFailureWrongPassword() {
        String hashed = at.favre.lib.crypto.bcrypt.BCrypt.withDefaults().hashToString(12, "CorrectPassword".toCharArray());
        User existingUser = new User(2, "Bob", "bob@example.com", hashed, "1234567890");
        when(userRepository.findByEmail("bob@example.com")).thenReturn(existingUser);

        User authenticated = userService.authenticate("bob@example.com", "WrongPassword");
        assertNull(authenticated);
    }

    @Test
    @DisplayName("authenticate() seamlessly migrates legacy plaintext password to BCrypt hash")
    void testLegacyPlaintextMigration() {
        String plaintextPassword = "LegacyPlainPassword";
        User legacyUser = new User(3, "Charlie", "charlie@example.com", plaintextPassword, "1234567890");
        when(userRepository.findByEmail("charlie@example.com")).thenReturn(legacyUser);

        User authenticated = userService.authenticate("charlie@example.com", plaintextPassword);
        assertNotNull(authenticated);

        // Verify password was converted to BCrypt hash and saved to repository
        assertTrue(authenticated.getPassword().startsWith("$2a$") || authenticated.getPassword().startsWith("$2b$"));
        verify(userRepository, times(1)).update(legacyUser);
    }
}
