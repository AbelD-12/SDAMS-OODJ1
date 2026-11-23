package sdams.service;

import sdams.model.User;
import sdams.util.InvalidInputException;

public class AuthService {

    private final UserService userService;

    public AuthService() {
        this.userService = new UserService();
    }

    public User login(String username, String password) {

        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException(
                    "Username cannot be empty."
            );
        }

        if (password == null || password.isEmpty()) {
            throw new InvalidInputException(
                    "Password cannot be empty."
            );
        }

        User user = userService.findByUsername(username);

        if (user == null) {
            throw new InvalidInputException(
                    "Invalid username or password."
            );
        }

        if (!user.getPassword().equals(password)) {
            throw new InvalidInputException(
                    "Invalid username or password."
            );
        }

        return user;
    }
}
