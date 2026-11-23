package sdams.ui.manager;

import sdams.enums.Role;
import sdams.model.Administrator;
import sdams.model.User;
import sdams.service.UserService;
import sdams.ui.common.UITheme;
import sdams.util.InvalidInputException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class ManageAdministratorsPanel extends JPanel {

    private UserService userService = new UserService();

    // ID of the administrator selected in the table (null = nothing selected)
    private String selectedId = null;

    private JLabel lblTitle;
    private JLabel lblSearch;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnShowAll;

    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private JLabel lblName;
    private JLabel lblUsername;
    private JLabel lblPassword;
    private JLabel lblEmail;
    private JLabel lblPhone;

    private JTextField txtName;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtEmail;
    private JTextField txtPhone;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    public ManageAdministratorsPanel() {

        setLayout(null);

        // ----- title
        lblTitle = UITheme.createTitle("Manage Administrators");
        lblTitle.setBounds(10, 10, 400, 35);

        // ----- search row
        lblSearch = new JLabel("Search:");
        txtSearch = new JTextField(10);
        btnSearch = new JButton("Search");
        btnShowAll = new JButton("Show All");

        lblSearch.setBounds(10, 55, 60, 25);
        txtSearch.setBounds(70, 55, 200, 25);
        btnSearch.setBounds(280, 55, 90, 25);
        btnShowAll.setBounds(380, 55, 100, 25);

        // ----- table
        String[] columns = {"ID", "Name", "Username", "Email", "Phone"};

        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setDefaultEditor(Object.class, null);   // cells cannot be typed in
        UITheme.styleTable(table);

        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 90, 500, 420);

        // ----- form labels
        lblName = new JLabel("Name:");
        lblUsername = new JLabel("Username:");
        lblPassword = new JLabel("Password:");
        lblEmail = new JLabel("Email:");
        lblPhone = new JLabel("Phone:");

        lblName.setBounds(530, 90, 80, 28);
        lblUsername.setBounds(530, 130, 80, 28);
        lblPassword.setBounds(530, 170, 80, 28);
        lblEmail.setBounds(530, 210, 80, 28);
        lblPhone.setBounds(530, 250, 80, 28);

        // ----- form fields
        txtName = new JTextField(10);
        txtUsername = new JTextField(10);
        txtPassword = new JPasswordField(10);
        txtEmail = new JTextField(10);
        txtPhone = new JTextField(10);

        txtName.setBounds(610, 90, 200, 28);
        txtUsername.setBounds(610, 130, 200, 28);
        txtPassword.setBounds(610, 170, 200, 28);
        txtEmail.setBounds(610, 210, 200, 28);
        txtPhone.setBounds(610, 250, 200, 28);

        // ----- form buttons
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");

        btnAdd.setBounds(530, 300, 130, 35);
        btnUpdate.setBounds(680, 300, 130, 35);
        btnDelete.setBounds(530, 345, 130, 35);
        btnClear.setBounds(680, 345, 130, 35);

        UITheme.styleButton(btnSearch);
        UITheme.styleButton(btnShowAll);
        UITheme.styleButton(btnAdd);
        UITheme.styleButton(btnUpdate);
        UITheme.styleButton(btnDelete);
        UITheme.styleButton(btnClear);

        // ----- add everything to the panel
        add(lblTitle);
        add(lblSearch);
        add(txtSearch);
        add(btnSearch);
        add(btnShowAll);
        add(scrollPane);
        add(lblName);
        add(lblUsername);
        add(lblPassword);
        add(lblEmail);
        add(lblPhone);
        add(txtName);
        add(txtUsername);
        add(txtPassword);
        add(txtEmail);
        add(txtPhone);
        add(btnAdd);
        add(btnUpdate);
        add(btnDelete);
        add(btnClear);

        // ----- button actions (lambda expressions)
        btnAdd.addActionListener(e -> addAdmin());
        btnUpdate.addActionListener(e -> updateAdmin());
        btnDelete.addActionListener(e -> deleteAdmin());
        btnClear.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> searchAdmins());
        btnShowAll.addActionListener(e -> {
            txtSearch.setText("");
            refreshTable();
        });

        // when a row is clicked, copy it into the form
        table.getSelectionModel().addListSelectionListener(e -> fillForm());

        refreshTable();
    }

    // ---------------------------------------------------------- Add

    private void addAdmin() {

        try {

            Administrator admin = makeAdmin(createId());

            userService.add(admin);

            JOptionPane.showMessageDialog(
                    this, "Administrator added.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            refreshTable();

        } catch (InvalidInputException e) {

            JOptionPane.showMessageDialog(
                    this, e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------- Update

    private void updateAdmin() {

        if (selectedId == null) {
            JOptionPane.showMessageDialog(
                    this, "Please select an administrator first.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {

            Administrator admin = makeAdmin(selectedId);

            userService.update(admin);

            JOptionPane.showMessageDialog(
                    this, "Administrator updated.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            refreshTable();

        } catch (InvalidInputException e) {

            JOptionPane.showMessageDialog(
                    this, e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------- Delete

    private void deleteAdmin() {

        if (selectedId == null) {
            JOptionPane.showMessageDialog(
                    this, "Please select an administrator first.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this, "Are you sure you want to delete this administrator?",
                "Delete", JOptionPane.YES_NO_OPTION);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            userService.delete(selectedId);

            JOptionPane.showMessageDialog(
                    this, "Administrator deleted.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            refreshTable();

        } catch (InvalidInputException e) {

            JOptionPane.showMessageDialog(
                    this, e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------- Search

    private void searchAdmins() {

        List<User> found = userService.search(txtSearch.getText().trim());

        // search() returns every role, so keep only administrators
        List<User> admins = new ArrayList<>();

        for (User user : found) {
            if (user.getRole() == Role.ADMINISTRATOR) {
                admins.add(user);
            }
        }

        showUsers(admins);
    }

    // --------------------------------------------------------- Clear

    private void clearForm() {

        selectedId = null;

        txtName.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtEmail.setText("");
        txtPhone.setText("");

        table.clearSelection();
    }

    // ------------------------------------------------------- Helpers

    private void refreshTable() {

        showUsers(userService.getByRole(Role.ADMINISTRATOR));
    }

    private void showUsers(List<User> users) {

        tableModel.setRowCount(0);

        for (User user : users) {
            Object[] row = {
                    user.getId(),
                    user.getName(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhone()
            };
            tableModel.addRow(row);
        }

        selectedId = null;
    }

    private void fillForm() {

        int row = table.getSelectedRow();

        if (row == -1) {
            return;
        }

        String id = tableModel.getValueAt(row, 0).toString();

        User user = userService.getById(id);

        selectedId = id;

        txtName.setText(user.getName());
        txtUsername.setText(user.getUsername());
        txtPassword.setText(user.getPassword());
        txtEmail.setText(user.getEmail());
        txtPhone.setText(user.getPhone());
    }

    // builds an Administrator object from what is typed in the form
    private Administrator makeAdmin(String id) {

        String name = txtName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        return new Administrator(id, name, username, password, email, phone);
    }

    // finds the next free ID: ADM001, ADM002, ADM003 ...
    private String createId() {

        int number = 1;
        String id = "ADM001";

        while (userService.getById(id) != null) {

            number++;

            if (number < 10) {
                id = "ADM00" + number;
            } else if (number < 100) {
                id = "ADM0" + number;
            } else {
                id = "ADM" + number;
            }
        }

        return id;
    }
}