package sdams.model;

import sdams.enums.Role;
import sdams.util.Storable;

public abstract class User implements Storable {

    private String id;
    private String name;
    private String username;
    private String password;
    private String email;
    private String phone;

    public User(String id, String name, String username,
                String password, String email, String phone) {

        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public abstract Role getRole();

    public abstract void showDashboard();

    @Override
    public String toFileString() {
        return id + "|" +
                getRole() + "|" +
                name + "|" +
                username + "|" +
                password + "|" +
                email + "|" +
                phone;
    }
    public static User fromFileString(String line) {

        String[] parts = line.split("\\|", -1);

        if (parts.length != 7) {
            throw new IllegalArgumentException(
                    "Invalid user record: " + line
            );
        }

        String id = parts[0];
        Role role = Role.valueOf(parts[1]);
        String name = parts[2];
        String username = parts[3];
        String password = parts[4];
        String email = parts[5];
        String phone = parts[6];

        return switch (role) {

            case MANAGER ->
                    new Manager(id, name, username, password, email, phone);

            case ADMINISTRATOR ->
                    new Administrator(id, name, username, password, email, phone);

            case INSTRUCTOR ->
                    new Instructor(id, name, username, password, email, phone);

            case STUDENT ->
                    new Student(id, name, username, password, email, phone);

            case FACILITIES_OFFICER ->
                    new FacilitiesOfficer(
                            id, name, username, password, email, phone
                    );

            case ATTENDANCE_OFFICER ->
                    new AttendanceOfficer(
                            id, name, username, password, email, phone
                    );
        };
    }
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", role=" + getRole() +
                '}';
    }
}
