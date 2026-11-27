package sdams.ui.instructor;

import sdams.enums.Discipline;
import sdams.model.ClassSession;
import sdams.model.Instructor;
import sdams.service.ClassService;
import sdams.util.DataAccessException;
import sdams.util.InvalidInputException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class ManageClassPanel extends JPanel {

    private final Instructor instructor;
    private final ClassService classes = new ClassService();

    private final JComboBox<Discipline> discipline =
            new JComboBox<>(Discipline.values());
    private final JTextField date = new JTextField();
    private final JTextField start = new JTextField();
    private final JTextField end = new JTextField();
    private final JTextField capacity = new JTextField();
    private final JTextField fee = new JTextField();
    private final JTextField search = new JTextField(16);

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{
                    "Class ID", "Discipline", "Date", "Start", "End",
                    "Capacity", "Fee", "Status"
            }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);

    public ManageClassPanel(Instructor instructor) {
        this.instructor = instructor;

        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Discipline"));
        form.add(discipline);
        form.add(new JLabel("Date (YYYY-MM-DD)"));
        form.add(date);
        form.add(new JLabel("Start time (HH:mm)"));
        form.add(start);
        form.add(new JLabel("End time (HH:mm)"));
        form.add(end);
        form.add(new JLabel("Capacity"));
        form.add(capacity);
        form.add(new JLabel("Fee (RM)"));
        form.add(fee);
        add(form, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (e.getValueIsAdjusting() || row < 0) {
                return;
            }

            discipline.setSelectedItem(model.getValueAt(row, 1));
            date.setText(model.getValueAt(row, 2).toString());
            start.setText(model.getValueAt(row, 3).toString());
            end.setText(model.getValueAt(row, 4).toString());
            capacity.setText(model.getValueAt(row, 5).toString());
            fee.setText(model.getValueAt(row, 6).toString());
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel();
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel Class");
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> run(() -> save(null)));
        updateButton.addActionListener(e -> run(() -> save(selectedId())));
        cancelButton.addActionListener(e -> run(this::cancelClass));
        searchButton.addActionListener(e -> run(this::loadClasses));
        search.addActionListener(e -> run(this::loadClasses));

        refreshButton.addActionListener(e -> run(() -> {
            search.setText("");
            loadClasses();
        }));

        actions.add(addButton);
        actions.add(updateButton);
        actions.add(cancelButton);
        actions.add(search);
        actions.add(searchButton);
        actions.add(refreshButton);
        add(actions, BorderLayout.SOUTH);

        run(this::loadClasses);
    }

    private void save(String id) {
        LocalDate classDate = LocalDate.parse(date.getText().trim());
        LocalTime startTime = LocalTime.parse(start.getText().trim());
        LocalTime endTime = LocalTime.parse(end.getText().trim());
        int seats = Integer.parseInt(capacity.getText().trim());
        double price = Double.parseDouble(fee.getText().trim());
        Discipline selected = (Discipline) discipline.getSelectedItem();

        if (id == null) {
            classes.schedule(instructor.getId(), selected, classDate,
                    startTime, endTime, seats, price);
        } else {
            classes.revise(instructor.getId(), id, selected, classDate,
                    startTime, endTime, seats, price);
        }

        search.setText("");
        loadClasses();
    }

    private String selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            throw new InvalidInputException("Select a class from the table first.");
        }
        return model.getValueAt(row, 0).toString();
    }

    private void cancelClass() {
        String id = selectedId();
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Cancel " + id + " and refund any paid bookings?",
                "Cancel Class",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            classes.cancel(instructor.getId(), id);
            loadClasses();
        }
    }

    private void loadClasses() {
        var matches = classes.search(search.getText().trim());
        model.setRowCount(0);

        for (ClassSession session : matches) {
            if (instructor.getId().equals(session.getInstructorId())) {
                model.addRow(new Object[]{
                        session.getId(),
                        session.getDiscipline(),
                        session.getDate(),
                        session.getStartTime(),
                        session.getEndTime(),
                        session.getCapacity(),
                        session.getFee(),
                        session.getStatus()
                });
            }
        }
    }

    private void run(Runnable action) {
        try {
            action.run();
        } catch (DateTimeParseException ex) {
            showError("Use YYYY-MM-DD for dates and HH:mm for times.");
        } catch (NumberFormatException ex) {
            showError("Enter a whole number for capacity and a number for fee.");
        } catch (InvalidInputException | DataAccessException ex) {
            showError(ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this, message, "Manage Classes", JOptionPane.ERROR_MESSAGE
        );
    }
}