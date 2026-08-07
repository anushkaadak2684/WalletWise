package test;

import model.User;
import service.UserService;

public class UserTest {
    public static void main(String[] args) {

        UserService userService = new UserService();

        // CREATE USER
        User user = userService.createUser(
                "Anushka",
                "anushka@gmail.com",
                "Test@123",
                "9876543210",
                null
        );

        System.out.println("===== CREATED USER =====");
        user.displayProfile();

        // FIND USER
        User fetchedUser =
                userService.getUserById(
                        user.getUserId()
                );


        System.out.println("\n===== FETCHED USER =====");
        fetchedUser.displayProfile();

        // UPDATE USER
        userService.updateProfile(
                fetchedUser,
                "Anushka Updated",
                "8888888888"
        );

        System.out.println("\n===== UPDATED USER =====");

        User updatedUser =
                userService.getUserById(
                        user.getUserId()
                );

        updatedUser.displayProfile();

        // ALL USERS
        System.out.println("\n===== ALL USERS =====");

        userService.showUsers();

    }
}
