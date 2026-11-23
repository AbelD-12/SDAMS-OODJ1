package sdams.ui.common;

import sdams.model.User;
import sdams.service.AuthService;
import sdams.util.InvalidInputException;
import sdams.util.Session;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnClear;

    private final AuthService authService;

    public LoginFrame() {

        authService = new AuthService();

        initializeFrame();
        initializeComponents();
        initializeLayout();
        initializeActions();
    }

    private void initializeFrame() {

        setTitle("SDAMS - Login");

        setSize(500, 400);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setResizable(false);
    }

    private void initializeComponents() {

        txtUsername = new JTextField(20);

        txtPassword = new JPasswordField(20);

        btnLogin = new JButton("Login");

        btnClear = new JButton("Clear");

        UITheme.styleTextField(
                txtUsername
        );

        UITheme.stylePasswordField(
                txtPassword
        );

        UITheme.styleButton(btnLogin);

        UITheme.styleButton(btnClear);
    }

    private void initializeLayout() {

        JPanel mainPanel =
                new JPanel(new BorderLayout(10, 10));

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 40, 30, 40
                )
        );

        JLabel title =
                UITheme.createTitle(
                        "Self Defence Academy"
                );

        title.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        mainPanel.add(
                title,
                BorderLayout.NORTH
        );

        JPanel formPanel =
                new JPanel(new GridBagLayout());

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(10, 10, 10, 10);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;

        formPanel.add(
                new JLabel("Username:"),
                gbc
        );

        gbc.gridx = 1;

        formPanel.add(
                txtUsername,
                gbc
        );

        gbc.gridx = 0;
        gbc.gridy = 1;

        formPanel.add(
                new JLabel("Password:"),
                gbc
        );

        gbc.gridx = 1;

        formPanel.add(
                txtPassword,
                gbc
        );

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER
                        )
                );

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnClear);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;

        formPanel.add(
                buttonPanel,
                gbc
        );

        mainPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        add(mainPanel);
    }

    private void initializeActions() {

        btnLogin.addActionListener(
                e -> login()
        );

        btnClear.addActionListener(
                e -> clearFields()
        );

        txtPassword.addActionListener(
                e -> login()
        );
    }

    private void login() {

        String username =
                txtUsername.getText().trim();

        String password =
                new String(
                        txtPassword.getPassword()
                );

        try {

            User user =
                    authService.login(
                            username,
                            password
                    );

            Session.setCurrentUser(user);

            dispose();

            user.showDashboard();

        } catch (InvalidInputException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );

            txtPassword.setText("");

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "An unexpected error occurred:\n"
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearFields() {

        txtUsername.setText("");

        txtPassword.setText("");

        txtUsername.requestFocus();
    }
}