package sdams.ui.instructor;

import sdams.model.Instructor;
import javax.swing.*;
import java.awt.*;

public class InstructorDashboard extends JFrame {

    private final Instructor instructor;

    public InstructorDashboard(Instructor instructor) {

        this.instructor = instructor;

        setTitle("SDAMS - Instructor Dashboard");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel(
                "Welcome, " + instructor.getName(),
                SwingConstants.CENTER);

        title.setFont(new Font("Arial", Font.BOLD, 24));

        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Manage Classes",
                new ManageClassPanel(instructor));

        tabs.addTab("Facility Problems",
                new FacilityProblemPanel(instructor));

        tabs.addTab("View Ratings",
                new ViewRatingPanel(instructor));

        add(tabs, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");

        logoutButton.addActionListener(e -> {
            sdams.util.Session.logout();
            dispose();
            new sdams.ui.common.LoginFrame().setVisible(true);
        });

        JPanel bottomPanel = new JPanel();

        bottomPanel.add(logoutButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }
}