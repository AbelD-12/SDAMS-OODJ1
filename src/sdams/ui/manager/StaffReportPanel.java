package sdams.ui.manager;

import sdams.enums.Role;
import sdams.model.User;
import sdams.service.ReportService;
import sdams.ui.common.UITheme;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class StaffReportPanel extends JPanel {

    private ReportService reportService = new ReportService();

    // Same order in both arrays. Index 0 = all staff (no role filter)
    private Role[] roles = {
            null,
            Role.ADMINISTRATOR,
            Role.INSTRUCTOR,
            Role.FACILITIES_OFFICER,
            Role.ATTENDANCE_OFFICER
    };

    private String[] roleNames = {
            "All Staff",
            "Administrator",
            "Instructor",
            "Facilities Officer",
            "Attendance Officer"
    };

    private String[] columns = {"ID", "Name", "Role", "Username", "Email", "Phone"};

    private JLabel lblTitle;

    private JLabel lblRole;
    private JComboBox<String> cboRole;
    private JLabel lblName;
    private JTextField txtName;
    private JButton btnFilter;
    private JButton btnReset;

    private JTable table;
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
        cboRole = new JComboBox<>(roleNames);
        lblName = new JLabel("Name:");
        txtName = new JTextField(10);
        btnFilter = new JButton("Filter");
        btnReset = new JButton("Reset");

        lblRole.setBounds(10, 55, 50, 25);
        cboRole.setBounds(60, 55, 170, 25);
        lblName.setBounds(250, 55, 50, 25);
        txtName.setBounds(300, 55, 180, 25);
        btnFilter.setBounds(500, 55, 90, 25);
        btnReset.setBounds(600, 55, 90, 25);

        UITheme.styleButton(btnFilter);
        UITheme.styleButton(btnReset);

        // ----- table
        table = new JTable(new Object[0][6], columns);
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

        btnReset.addActionListener(e -> {
            txtName.setText("");
            cboRole.setSelectedIndex(0);
            showReport();
        });

        showReport();
    }

    private void showReport() {

        // role = null when "All Staff" is chosen
        Role role = roles[cboRole.getSelectedIndex()];
        String name = txtName.getText().trim();

        // ----- table: staff filtered by role and name
        List<User> staff = reportService.searchStaff(role, name);

        Object[][] data = new Object[staff.size()][6];

        for (int i = 0; i < staff.size(); i++) {

            User user = staff.get(i);

            data[i][0] = user.getId();
            data[i][1] = user.getName();
            data[i][2] = roleName(user.getRole());
            data[i][3] = user.getUsername();
            data[i][4] = user.getEmail();
            data[i][5] = user.getPhone();
        }

        table = new JTable(data, columns);
        UITheme.styleTable(table);
        scrollPane.setViewportView(table);

        // ----- totals: all staff, not affected by the filter
        Map<Role, Integer> counts = reportService.countStaffByRole();

        int admins = counts.get(Role.ADMINISTRATOR);
        int instructors = counts.get(Role.INSTRUCTOR);
        int facilities = counts.get(Role.FACILITIES_OFFICER);
        int attendance = counts.get(Role.ATTENDANCE_OFFICER);

        int total = admins + instructors + facilities + attendance;

        lblAdmins.setText("Administrators: " + admins);
        lblInstructors.setText("Instructors: " + instructors);
        lblFacilities.setText("Facilities Officers: " + facilities);
        lblAttendance.setText("Attendance Officers: " + attendance);
        lblTotal.setText("Total staff: " + total);
        lblShowing.setText("Showing " + staff.size() + " record(s) in the table");
    }

    // Turns Role.FACILITIES_OFFICER into "Facilities Officer"
    private String roleName(Role role) {

        for (int i = 1; i < roles.length; i++) {
            if (roles[i] == role) {
                return roleNames[i];
            }
        }

        return "";
    }
}