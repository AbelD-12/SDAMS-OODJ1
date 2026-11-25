package sdams.model;

import sdams.enums.Role;
import sdams.ui.manager.ManagerDashboard;

/**
 * The Manager user. Opens the Manager dashboard after login.
 */
public class Manager extends User {

    /** Creates a manager. */
    public Manager(String id, String name, String username,
                   String password, String email, String phone) {

        super(id, name, username, password, email, phone);
    }

    /** Returns the MANAGER role. */
    @Override
    public Role getRole() {
        return Role.MANAGER;
    }

    /** Opens the Manager dashboard window. */
    @Override
    public void showDashboard() {

        ManagerDashboard dashboard = new ManagerDashboard();
        dashboard.setVisible(true);
    }
}