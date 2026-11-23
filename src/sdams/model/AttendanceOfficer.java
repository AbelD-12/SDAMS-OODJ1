package sdams.model;

import sdams.enums.Role;
import javax.swing.JOptionPane;

public class AttendanceOfficer extends User {

    public AttendanceOfficer(String id, String name, String username,
                             String password, String email, String phone) {

        super(id, name, username, password, email, phone);
    }

    @Override
    public Role getRole() {
        return Role.ATTENDANCE_OFFICER;
    }

    @Override
    public void showDashboard() {

        JOptionPane.showMessageDialog(
                null,
                "Attendance Dashboard"
        );
    }
}
