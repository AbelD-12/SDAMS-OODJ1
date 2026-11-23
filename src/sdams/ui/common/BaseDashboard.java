package sdams.ui.common;

import sdams.model.User;
import sdams.util.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class BaseDashboard extends JFrame {

    protected JPanel sidebar;
    protected JPanel contentPanel;
    protected CardLayout cardLayout;

    protected JLabel lblUser;
    protected JLabel lblRole;

    private JButton btnLogout;

    public BaseDashboard() {

        User currentUser = Session.getCurrentUser();

        if (currentUser == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "No user is currently logged in.",
                    "Session Error",
                    JOptionPane.ERROR_MESSAGE
            );

            dispose();
            return;
        }

        setTitle("SDAMS - " + currentUser.getRole());
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                logout();
            }
        });

        initializeLayout();
    }

    private void initializeLayout() {

        setLayout(new BorderLayout());

        createHeader();
        createSidebar();
        createContentArea();
    }



    private void createHeader() {

        JPanel header = new JPanel(new BorderLayout());

        header.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 20, 10, 20
                )
        );

        User user = Session.getCurrentUser();

        JLabel title =
                UITheme.createTitle("SDAMS");

        lblUser = new JLabel(
                "User: " + user.getName()
        );

        lblUser.setFont(
                UITheme.NORMAL_FONT
        );

        lblRole = new JLabel(
                "Role: " + user.getRole()
        );

        lblRole.setFont(
                UITheme.NORMAL_FONT
        );

        JPanel userInfo =
                new JPanel(new GridLayout(2, 1));

        userInfo.add(lblUser);
        userInfo.add(lblRole);

        header.add(
                title,
                BorderLayout.WEST
        );

        header.add(
                userInfo,
                BorderLayout.EAST
        );

        add(
                header,
                BorderLayout.NORTH
        );
    }

    private void createSidebar() {

        sidebar = new JPanel();

        sidebar.setLayout(
                new BoxLayout(
                        sidebar,
                        BoxLayout.Y_AXIS
                )
        );

        sidebar.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 10, 15, 10
                )
        );

        sidebar.setPreferredSize(
                new Dimension(220, 0)
        );

        btnLogout = new JButton("Logout");

        UITheme.styleButton(btnLogout);

        btnLogout.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        btnLogout.addActionListener(
                e -> logout()
        );

        add(
                sidebar,
                BorderLayout.WEST
        );
    }


    private void createContentArea() {

        cardLayout = new CardLayout();

        contentPanel = new JPanel(cardLayout);

        contentPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 15, 15, 15
                )
        );

        add(
                contentPanel,
                BorderLayout.CENTER
        );
    }

    protected void addMenuItem(
            String label,
            JPanel panel
    ) {

        JButton button = new JButton(label);

        UITheme.styleButton(button);

        button.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        button.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        45
                )
        );

        button.addActionListener(
                e -> showPanel(label)
        );

        sidebar.add(button);

        sidebar.add(
                Box.createVerticalStrut(10)
        );

        contentPanel.add(
                panel,
                label
        );
    }


    protected void addLogoutButton() {

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);
    }

    protected void showPanel(String panelName) {

        cardLayout.show(
                contentPanel,
                panelName
        );
    }

    private void logout() {

        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {

            Session.logout();

            dispose();

            // LoginFrame will be created later.
            // We will connect it here in the next step.
        }
    }
}