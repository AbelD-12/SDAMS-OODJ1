package sdams.ui.manager;

import sdams.enums.Role;
import sdams.model.User;
import sdams.service.UserService;
import sdams.ui.common.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StaffReportPanel extends JPanel {

    private UserService userService = new UserService();

    // The 4 staff roles and their display names (same order in both arrays)
    private Role[] staffRoles = {
            Role.ADMINISTRATOR,
            Role.INSTRUCTOR,
            Role.FACILITIES_OFFICER,
            Role.ATTENDANCE_OFFICER
    };

    private String[] roleNames = {
            "Administrator",
            "Instructor",
            "Facilities Officer",
            "Attendance Officer"
    };

    private JLabel lblTitle;

    private JLabel lblRole;
    private JComboBox<String> cboRole;
    private JLabel lblName;
    private JTextField txtName;
    private JButton btnFilter;
    private JButton btnReset;

    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private JLabel lblAdmins;
    private JLabel lblInstructors;
    private JLabel lblFacilities;
    private JLabel lblAttendance;
    private JLabel lblTotal;
    private JLabel lblShowing;

    public StaffReportPanel() {

        setLayout(null);

        // ----- title
        lblTitle = UITheme.createTitle("Staff Report");
        lblTitle.setBounds(10, 10, 400, 35);

        // ----- filter row
        lblRole = new JLabel("Role:");
        cboRole = new JComboBox<>();
        lblName = new JLabel("Name:");
        txtName = new JTextField(10);
        btnFilter = new JButton("Filter");
        btnReset = new JButton("Reset");

        // first item = everyone, then the 4 roles
        cboRole.addItem("All Staff");
        for (int i = 0; i < roleNames.length; i++) {
            cboRole.addItem(roleNames[i]);
        }

        lblRole.setBounds(10, 55, 50, 25);
        cboRole.setBounds(60, 55, 170, 25);
        lblName.setBounds(250, 55, 50, 25);
        txtName.setBounds(300, 55, 180, 25);
        btnFilter.setBounds(500, 55, 90, 25);
        btnReset.setBounds(600, 55, 90, 25);

        UITheme.styleButton(btnFilter);
        UITheme.styleButton(btnReset);

        // ----- table
        String[] columns = {"ID", "Name", "Role", "Username", "Email", "Phone"};

        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setDefaultEditor(Object.class, null);   // cells cannot be typed in
        UITheme.styleTable(table);

        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 90, 820, 330);

        // ----- totals
        lblAdmins = new JLabel();
        lblInstructors = new JLabel();
        lblFacilities = new JLabel();
        lblAttendance = new JLabel();
        lblTotal = new JLabel();
        lblShowing = new JLabel();

        lblAdmins.setFont(UITheme.BUTTON_FONT);
        lblInstructors.setFont(UITheme.BUTTON_FONT);
        lblFacilities.setFont(UITheme.BUTTON_FONT);
        lblAttendance.setFont(UITheme.BUTTON_FONT);
        lblTotal.setFont(UITheme.BUTTON_FONT);
        lblShowing.setFont(UITheme.NORMAL_FONT);

        lblAdmins.setBounds(10, 435, 200, 25);
        lblInstructors.setBounds(215, 435, 200, 25);
        lblFacilities.setBounds(420, 435, 200, 25);
        lblAttendance.setBounds(625, 435, 200, 25);
        lblTotal.setBounds(10, 465, 300, 25);
        lblShowing.setBounds(10, 495, 400, 25);

        // ----- add everything to the panel
        add(lblTitle);
        add(lblRole);
        add(cboRole);
        add(lblName);
        add(txtName);
        add(btnFilter);
        add(btnReset);
        add(scrollPane);
        add(lblAdmins);
        add(lblInstructors);
        add(lblFacilities);
        add(lblAttendance);
        add(lblTotal);
        add(lblShowing);

        // ----- actions (lambda expressions)
        btnFilter.addActionListener(e -> showReport());
        txtName.addActionListener(e -> showReport());
        cboRole.addActionListener(e -> showReport());

        btnReset.addActionListener(e -> {
            txtName.setText("");
            cboRole.setSelectedIndex(0);
            showReport();
        });

        showReport();
    }

    // Finds the position of a role in staffRoles (-1 if it is not staff)
    private int findRoleIndex(Role role) {

        for (int i = 0; i < staffRoles.length; i++) {
            if (staffRoles[i] == role) {
                return i;
            }
        }

        return -1;
    }

    private void showReport() {

        int roleChoice = cboRole.getSelectedIndex();   // 0 = All Staff
        String nameText = txtName.getText().trim().toLowerCase();

        int[] counts = new int[4];   // total of each role
        int shown = 0;

        tableModel.setRowCount(0);

        for (User user : userService.getAll()) {

            int i = findRoleIndex(user.getRole());

            // Managers and students are not staff, so skip them
            if (i == -1) {
                continue;
            }

            counts[i]++;

            boolean roleMatches = (roleChoice == 0) || (roleChoice == i + 1);
            boolean nameMatches = user.getName().toLowerCase().contains(nameText);

            if (roleMatches && nameMatches) {

                Object[] row = {
                        user.getId(),
                        user.getName(),
                        roleNames[i],
                        user.getUsername(),
                        user.getEmail(),
                        user.getPhone()
                };

                tableModel.addRow(row);
                shown++;
            }
        }

        int total = counts[0] + counts[1] + counts[2] + counts[3];

        lblAdmins.setText("Administrators: " + counts[0]);
        lblInstructors.setText("Instructors: " + counts[1]);
        lblFacilities.setText("Facilities Officers: " + counts[2]);
        lblAttendance.setText("Attendance Officers: " + counts[3]);
        lblTotal.setText("Total staff: " + total);
        lblShowing.setText("Showing " + shown + " record(s) in the table");
    }
}