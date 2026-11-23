package sdams;

import sdams.model.Student;
import sdams.model.User;
import sdams.util.Session;

public class TestFoundation {

    public static void main(String[] args) {

        Student student = new Student(
                "STU001",
                "Abel",
                "abel",
                "password123",
                "abel@email.com",
                "0123456789"
        );

        Session.setCurrentUser(student);

        System.out.println("Logged in user:");
        System.out.println(Session.getCurrentUser().getName());

        System.out.println("User role:");
        System.out.println(Session.getCurrentUser().getRole());

        Session.logout();

        System.out.println("After logout:");
        System.out.println(Session.getCurrentUser());
    }
}