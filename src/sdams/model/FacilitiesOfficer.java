package sdams.model;

import sdams.enums.Role;
import javax.swing.JOptionPane;

public class FacilitiesOfficer extends User {

    public FacilitiesOfficer(String id, String name, String username,
                             String password, String email, String phone) {

        super(id, name, username, password, email, phone);
    }

    @Override
    public Role getRole() {
        return Role.FACILITIES_OFFICER;
    }

    @Override
    public void showDashboard() {

        JOptionPane.showMessageDialog(
                null,
                "Facilities Dashboard"
        );
    }
}
