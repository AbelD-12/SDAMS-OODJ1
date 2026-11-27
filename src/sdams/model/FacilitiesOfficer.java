package sdams.model;

import sdams.enums.Role;
import sdams.ui.facilities.FacilitiesDashboard;

public class FacilitiesOfficer extends User {

    public FacilitiesOfficer(String id, String name, String username,
                             String password, String email, String phone) {
        super(id, name, username, password, email, phone);
    }

    public Role getRole() {
        return Role.FACILITIES_OFFICER;
    }

    public void showDashboard() {
        new FacilitiesDashboard().setVisible(true);
    }
}