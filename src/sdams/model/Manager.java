package sdams.model;

import sdams.enums.Role;
import javax.swing.JOptionPane;

public class Manager extends User {

    public Manager(String id, String name, String username,
                   String password, String email, String phone) {

        super(id, name, username, password, email, phone);
    }

    @Override
    public Role getRole() {
        return Role.MANAGER;
    }

    @Override
    public void showDashboard() {

        ManagerDashboard dashboard = new ManagerDashboard();
        dashboard.setVisible(true);
    }
}