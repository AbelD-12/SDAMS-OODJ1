package sdams.ui.student;

import sdams.model.*;
import sdams.service.*;
import sdams.ui.common.UITheme;
import sdams.util.*;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Lets students search, reschedule and cancel their own bookings. */
public class MyBookingsPanel extends JPanel {
    private final BookingService bookings = new BookingService();
    private final ClassService classes = new ClassService();
    private final PaymentService payments = new PaymentService();
    private final JTextField txtSearch = new JTextField(15);
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Booking ID", "Class ID", "Discipline", "Class Date", "Booking Date", "Status", "Payment"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tblBookings = new JTable(model);

    /** Creates the booking history and action buttons. */
    public MyBookingsPanel() {
        setLayout(new BorderLayout(10, 10));
        JPanel top = new JPanel(new BorderLayout());
        top.add(UITheme.createTitle("My Bookings"), BorderLayout.NORTH);
        JPanel controls = new JPanel();
        JButton btnSearch = new JButton("Search");
        JButton btnClear = new JButton("Clear");
        JButton btnReschedule = new JButton("Reschedule");
        JButton btnCancel = new JButton("Cancel Booking");
        controls.add(new JLabel("Search:"));
        controls.add(txtSearch);
        controls.add(btnSearch);
        controls.add(btnClear);
        controls.add(btnReschedule);
        controls.add(btnCancel);
        top.add(controls, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);
        UITheme.styleTable(tblBookings);
        add(new JScrollPane(tblBookings), BorderLayout.CENTER);
        btnSearch.addActionListener(e -> refresh());
        btnClear.addActionListener(e -> { txtSearch.setText(""); refresh(); });
        btnReschedule.addActionListener(e -> reschedule());
        btnCancel.addActionListener(e -> cancel());
        txtSearch.addActionListener(e -> refresh());
    }

    /** Reloads only the current student's bookings. */
    public void refresh() {
        try {
            model.setRowCount(0);
            String query = txtSearch.getText().trim().toLowerCase(Locale.ROOT);
            for (Booking b : bookings.getByStudent(Session.getCurrentUser().getId())) {
                ClassSession c = classes.getById(b.getClassId());
                Payment p = payments.getByBooking(b.getId());
                String discipline = c == null ? "" : c.getDiscipline().toString();
                String date = c == null ? "" : c.getDate().toString();
                String status = p == null ? "UNPAID" : p.getStatus().name();
                String text = b.toString() + " " + discipline + " " + date + " " + status;
                if (text.toLowerCase(Locale.ROOT).contains(query)) {
                    model.addRow(new Object[]{b.getId(), b.getClassId(), discipline, date, b.getBookingDate(), b.getStatus(), status});
                }
            }
        } catch (RuntimeException ex) { showError(ex); }
    }

    private String selectedBooking() {
        int row = tblBookings.getSelectedRow();
        if (row < 0) throw new InvalidInputException("Select a booking first.");
        String id = model.getValueAt(tblBookings.convertRowIndexToModel(row), 0).toString();
        Booking b = bookings.getById(id);
        if (b == null || !b.getStudentId().equals(Session.getCurrentUser().getId())) {
            throw new InvalidInputException("Booking not found.");
        }
        return id;
    }

    private void reschedule() {
        try {
            String id = selectedBooking();
            Booking old = bookings.getById(id);
            List<String> options = new ArrayList<>();
            for (ClassSession c : classes.getOpenClasses()) {
                if (!c.getId().equals(old.getClassId()) && !c.getDate().isBefore(java.time.LocalDate.now())
                        && !(c.getDate().equals(java.time.LocalDate.now()) && !c.getStartTime().isAfter(java.time.LocalTime.now()))) {
                    options.add(c.getId() + " | " + c.getDiscipline() + " | " + c.getDate() + " " + c.getStartTime());
                }
            }
            if (options.isEmpty()) throw new InvalidInputException("No other open classes available.");
            String selected = (String) JOptionPane.showInputDialog(this, "Select new class", "Reschedule",
                    JOptionPane.PLAIN_MESSAGE, null, options.toArray(), options.get(0));
            if (selected == null) return;
            bookings.reschedule(id, selected.split(" \\| ")[0]);
            JOptionPane.showMessageDialog(this, "Booking rescheduled.");
            refresh();
        } catch (RuntimeException ex) { showError(ex); }
    }

    private void cancel() {
        try {
            String id = selectedBooking();
            if (JOptionPane.showConfirmDialog(this, "Cancel this booking? Paid bookings will be refunded.",
                    "Confirm Cancellation", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            bookings.cancel(id);
            JOptionPane.showMessageDialog(this, "Booking cancelled.");
            refresh();
        } catch (RuntimeException ex) { showError(ex); }
    }

    private void showError(RuntimeException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
