package sdams.ui.student;

import sdams.model.*;
import sdams.service.*;
import sdams.ui.common.UITheme;
import sdams.util.*;
import java.awt.BorderLayout;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Shows available classes and lets the current student book a class. */
public class BookClassPanel extends JPanel {
    private final ClassService classes = new ClassService();
    private final BookingService bookings = new BookingService();
    private final UserService users = new UserService();
    private final JTextField txtSearch = new JTextField(18);
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Class ID", "Discipline", "Instructor", "Date", "Start", "End", "Fee (RM)", "Seats"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tblClasses = new JTable(model);

    /** Creates the available-class table, search and booking buttons. */
    public BookClassPanel() {
        setLayout(new BorderLayout(10, 10));
        JPanel top = new JPanel(new BorderLayout());
        top.add(UITheme.createTitle("Book a Class"), BorderLayout.NORTH);
        JPanel controls = new JPanel();
        JButton btnSearch = new JButton("Search");
        JButton btnClear = new JButton("Clear");
        JButton btnBook = new JButton("Book Selected");
        controls.add(new JLabel("Discipline / instructor / date:"));
        controls.add(txtSearch);
        controls.add(btnSearch);
        controls.add(btnClear);
        controls.add(btnBook);
        top.add(controls, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);
        UITheme.styleTable(tblClasses);
        add(new JScrollPane(tblClasses), BorderLayout.CENTER);
        btnSearch.addActionListener(e -> refresh());
        btnClear.addActionListener(e -> { txtSearch.setText(""); refresh(); });
        btnBook.addActionListener(e -> book());
        txtSearch.addActionListener(e -> refresh());
    }

    /** Reloads open classes matching the search field. */
    public void refresh() {
        try {
            model.setRowCount(0);
            String query = txtSearch.getText().trim().toLowerCase(Locale.ROOT);
            for (ClassSession c : classes.getOpenClasses()) {
                if (c.getDate().isBefore(LocalDate.now()) || (c.getDate().equals(LocalDate.now())
                        && !c.getStartTime().isAfter(LocalTime.now()))) continue;
                User instructor = users.getById(c.getInstructorId());
                String name = instructor == null ? c.getInstructorId() : instructor.getName();
                int seats = c.getCapacity() - bookings.countActive(c.getId());
                String text = c.getId() + " " + c.getDiscipline() + " " + name + " " + c.getInstructorId() + " " + c.getDate();
                if (seats > 0 && text.toLowerCase(Locale.ROOT).contains(query)) {
                    model.addRow(new Object[]{c.getId(), c.getDiscipline(), name, c.getDate(), c.getStartTime(), c.getEndTime(), c.getFee(), seats});
                }
            }
        } catch (RuntimeException ex) { showError(ex); }
    }

    private void book() {
        try {
            int row = tblClasses.getSelectedRow();
            if (row < 0) throw new InvalidInputException("Select a class first.");
            String id = model.getValueAt(tblClasses.convertRowIndexToModel(row), 0).toString();
            Booking b = bookings.book(Session.getCurrentUser().getId(), id);
            JOptionPane.showMessageDialog(this, "Booked successfully: " + b.getId() + ". Pay from the Payments menu.");
            refresh();
        } catch (RuntimeException ex) { showError(ex); }
    }

    private void showError(RuntimeException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
