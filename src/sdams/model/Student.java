package sdams.model;

import sdams.enums.Role;
import sdams.ui.student.StudentDashboard;

public class Student extends User {

    public Student(String id, String name, String username,
                   String password, String email, String phone) {
        super(id, name, username, password, email, phone);
    }

    @Override
    public Role getRole() {
        return Role.STUDENT;
    }

    @Override
    public void showDashboard() {
        new StudentDashboard().setVisible(true);
    }
}