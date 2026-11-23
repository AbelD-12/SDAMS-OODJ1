package sdams.service;

import sdams.enums.Role;
import sdams.model.User;
import sdams.util.InvalidInputException;
import sdams.util.Validator;

import java.util.ArrayList;
import java.util.List;

public class UserService extends BaseService<User> {

    @Override
    protected String getFileName() {
        return "data/users.txt";
    }

    @Override
    protected User parse(String line) {
        return User.fromFileString(line);
    }

    public List<User> getByRole(Role role) {

        List<User> results = new ArrayList<>();

        for (User user : getAll()) {

            if (user.getRole() == role) {
                results.add(user);
            }
        }

        return results;
    }

    public User findByUsername(String username) {

        if (username == null) {
            return null;
        }

        for (User user : getAll()) {

            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }

        return null;
    }

    public boolean isUsernameTaken(String username) {

        return findByUsername(username) != null;
    }

    @Override
    public List<User> search(String keyword) {

        List<User> results = new ArrayList<>();

        if (keyword == null) {
            keyword = "";
        }

        String searchTerm = keyword.toLowerCase();

        for (User user : getAll()) {

            if (user.getName().toLowerCase().contains(searchTerm)
                    || user.getId().toLowerCase().contains(searchTerm)
                    || user.getUsername().toLowerCase().contains(searchTerm)) {

                results.add(user);
            }
        }

        return results;
    }

    @Override
    public void add(User user) {

        validateUser(user);

        if (isUsernameTaken(user.getUsername())) {
            throw new InvalidInputException(
                    "Username is already taken."
            );
        }

        super.add(user);
    }

    @Override
    public void update(User user) {

        validateUser(user);

        User existing = getById(user.getId());

        if (existing == null) {
            throw new InvalidInputException(
                    "User not found."
            );
        }

        User sameUsername = findByUsername(user.getUsername());

        if (sameUsername != null
                && !sameUsername.getId().equals(user.getId())) {

            throw new InvalidInputException(
                    "Username is already taken."
            );
        }

        super.update(user);
    }

    @Override
    public void delete(String id) {

        User user = getById(id);

        if (user == null) {
            throw new InvalidInputException(
                    "User not found."
            );
        }

        if (user.getRole() == Role.MANAGER) {
            throw new InvalidInputException(
                    "The Manager cannot be deleted."
            );
        }

        super.delete(id);
    }

    private void validateUser(User user) {

        Validator.requireNotEmpty(user.getName(), "Name");
        Validator.requireNotEmpty(user.getUsername(), "Username");
        Validator.requireNotEmpty(user.getPassword(), "Password");
        Validator.requireNotEmpty(user.getEmail(), "Email");
        Validator.requireNotEmpty(user.getPhone(), "Phone");

        if (user.getPassword().length() < 6) {
            throw new InvalidInputException(
                    "Password must be at least 6 characters."
            );
        }

        if (!Validator.isValidEmail(user.getEmail())) {
            throw new InvalidInputException(
                    "Invalid email address."
            );
        }

        if (!Validator.isValidPhone(user.getPhone())) {
            throw new InvalidInputException(
                    "Phone must contain 10 to 11 digits."
            );
        }

        Validator.noPipe(user.getName());
        Validator.noPipe(user.getUsername());
        Validator.noPipe(user.getPassword());
        Validator.noPipe(user.getEmail());
        Validator.noPipe(user.getPhone());
    }
}
