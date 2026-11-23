package sdams.util;

import sdams.model.User;

public class Session {

    private static User currentUser;

    private Session() {
        // Utility class
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }
}