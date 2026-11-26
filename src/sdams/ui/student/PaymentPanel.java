package sdams.ui.student;

import sdams.enums.*;
import sdams.model.*;
import sdams.service.*;
import sdams.ui.common.UITheme;
import sdams.util.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Simulates payment for bookings and displays the student's payment history. */
public class PaymentPanel extends JPanel {
    private final BookingService bookings = new BookingService();
    private final PaymentService payments = new PaymentService();
    private final ClassService classes = new ClassService();
    private final JComboBox<String> cmbBooking = new JComboBox<>();
    private final JComboBox<PaymentMethod> cmbMethod = new JComboBox<>(PaymentMethod.values());
    private final JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"ALL", "PAID", "PENDING", "REFUNDED"});
    private final JTextField txtFrom = new JTextField(10);
    private final JTextField txtTo = new JTextField(10);
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Payment ID", "Booking ID", "Amount (RM)", "Method", "Date", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tblPayments = new JTable(model);

    /** Builds the payment form and history filters. */
    public PaymentPanel() {
        setLayout(new BorderLayout(10, 10));
        JPanel top = new JPanel(new GridLayout(3, 1));
        top.add(UITheme.createTitle("Payments"));
        JPanel pay = new JPanel();
        JButton btnPay = new JButton("Pay");
        pay.add(new JLabel("Unpaid booking:"));
        pay.add(cmbBooking);
        pay.add(new JLabel("Method:"));
        pay.add(cmbMethod);
        pay.add(btnPay);
        top.add(pay);
        JPanel filters = new JPanel();
        JButton btnFilter = new JButton("Filter");
        JButton btnClear = new JButton("Clear");
        filters.add(new JLabel("From (yyyy-MM-dd):"));
        filters.add(txtFrom);
        filters.add(new JLabel("To:"));
        filters.add(txtTo);
        filters.add(cmbStatus);
        filters.add(btnFilter);
        filters.add(btnClear);
        top.add(filters);
        add(top, BorderLayout.NORTH);
        UITheme.styleTable(tblPayments);
        add(new JScrollPane(tblPayments), BorderLayout.CENTER);
        btnPay.addActionListener(e -> pay());
        btnFilter.addActionListener(e -> refresh());
        btnClear.addActionListener(e -> { txtFrom.setText(""); txtTo.setText(""); cmbStatus.setSelectedIndex(0); refresh(); });
    }

    /** Reloads payable bookings and filtered payment history. */
    public void refresh() {
        try {
            String studentId = Session.getCurrentUser().getId();
            cmbBooking.removeAllItems();
            for (Booking b : bookings.getByStudent(studentId)) {
                Payment p = payments.getByBooking(b.getId());
                ClassSession c = classes.getById(b.getClassId());
                if (b.getStatus() == BookingStatus.BOOKED && (p == null || p.getStatus() == PaymentStatus.PENDING)
                        && c != null && (c.getStatus() == ClassStatus.OPEN || c.getStatus() == ClassStatus.FULL)) {
                    cmbBooking.addItem(b.getId() + " | " + c.getDiscipline() + " | RM " + c.getFee());
                }
            }
            LocalDate from = txtFrom.getText().isBlank() ? null : LocalDate.parse(txtFrom.getText().trim());
            LocalDate to = txtTo.getText().isBlank() ? null : LocalDate.parse(txtTo.getText().trim());
            PaymentStatus status = cmbStatus.getSelectedIndex() == 0 ? null : PaymentStatus.valueOf((String) cmbStatus.getSelectedItem());
            java.util.List<Payment> results = payments.filter(studentId, from, to, status);
            model.setRowCount(0);
            for (Payment p : results) {
                model.addRow(new Object[]{p.getId(), p.getBookingId(), p.getAmount(), p.getMethod(), p.getDate(), p.getStatus()});
            }
        } catch (java.time.format.DateTimeParseException ex) {
            showError(new InvalidInputException("Enter dates in yyyy-MM-dd format."));
        } catch (RuntimeException ex) { showError(ex); }
    }

    private void pay() {
        try {
            String selected = (String) cmbBooking.getSelectedItem();
            if (selected == null) throw new InvalidInputException("No unpaid booking selected.");
            String id = selected.split(" \\| ")[0];
            Booking b = bookings.getById(id);
            if (b == null || !b.getStudentId().equals(Session.getCurrentUser().getId())) throw new InvalidInputException("Booking not found.");
            Payment p = payments.pay(id, (PaymentMethod) cmbMethod.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Payment successful: " + p.getId());
            refresh();
        } catch (RuntimeException ex) { showError(ex); }
    }

    private void showError(RuntimeException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
