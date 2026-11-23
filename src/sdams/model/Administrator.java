package sdams.model;

import sdams.enums.Role;
import javax.swing.JOptionPane;

public class Administrator extends User {

    public Administrator(String id, String name, String username,
                         String password, String email, String phone) {

        super(id, name, username, password, email, phone);
    }

    @Override
    public Role getRole() {
        return Role.ADMINISTRATOR;
    }

    @Override
    public void showDashboard() {

        JOptionPane.showMessageDialog(
                null,
                "Administrator Dashboard"
        );
    }
}