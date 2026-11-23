package sdams;

import sdams.model.User;
import sdams.service.AuthService;

public class TestAuthService {

    public static void main(String[] args) {

        AuthService authService = new AuthService();

        try {

            User user =
                    authService.login(
                            "student",
                            "password123"
                    );

            System.out.println("Login successful!");
            System.out.println("Name: " + user.getName());
            System.out.println("Role: " + user.getRole());

        } catch (Exception e) {

            System.out.println(
                    "Login failed: " + e.getMessage()
            );
        }
    }
}