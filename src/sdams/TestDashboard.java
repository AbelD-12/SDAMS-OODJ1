package sdams;

import sdams.model.Student;
import sdams.ui.common.BaseDashboard;
import sdams.util.Session;

import javax.swing.*;

public class TestDashboard extends BaseDashboard {

    public TestDashboard() {

        super();

        JPanel homePanel = new JPanel();

        homePanel.add(
                new JLabel("Welcome to the Student Dashboard!")
        );

        addMenuItem(
                "Home",
                homePanel
        );

        addMenuItem(
                "Test Page",
                new JPanel()
        );

        addLogoutButton();

        showPanel("Home");
    }

    public static void main(String[] args) {

        Student student = new Student(
                "STU001",
                "Student One",
                "student",
                "password123",
                "student@example.com",
                "0123456782"
        );

        Session.setCurrentUser(student);

        SwingUtilities.invokeLater(() -> {

            TestDashboard dashboard =
                    new TestDashboard();

            dashboard.setVisible(true);
        });
    }
}