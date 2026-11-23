package sdams;

import sdams.enums.Role;
import sdams.model.User;
import sdams.service.UserService;

import java.util.List;

public class TestUserService {

    public static void main(String[] args) {

        UserService userService = new UserService();

        System.out.println("===== ALL USERS =====");

        List<User> users = userService.getAll();

        for (User user : users) {
            System.out.println(
                    user.getId() + " - "
                            + user.getName() + " - "
                            + user.getRole()
            );
        }

        System.out.println("\n===== STUDENTS =====");

        List<User> students =
                userService.getByRole(Role.STUDENT);

        for (User student : students) {
            System.out.println(student.getName());
        }

        System.out.println("\n===== FIND USERNAME =====");

        User user =
                userService.findByUsername("student");

        if (user != null) {
            System.out.println(
                    user.getName() + " found."
            );
        }

        System.out.println("\n===== SEARCH =====");

        List<User> results =
                userService.search("student");

        for (User result : results) {
            System.out.println(
                    result.getId() + " - "
                            + result.getName()
            );
        }
    }
}
