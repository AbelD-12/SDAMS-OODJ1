package sdams.ui.student;

import sdams.model.*;
import sdams.service.*;
import sdams.ui.common.UITheme;
import sdams.util.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Allows students to rate attended classes and view their submitted ratings. */
public class RatingPanel extends JPanel {
    private final RatingService ratings = new RatingService();
    private final BookingService bookings = new BookingService();
    private final ClassService classes = new ClassService();
    private final AttendanceService attendance = new AttendanceService();
    private final UserService users = new UserService();
    private final JComboBox<String> cmbClass = new JComboBox<>();
    private final JComboBox<Integer> cmbScore = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
    private final JTextField txtComment = new JTextField(20);
    private final JLabel lblAverage = new JLabel("Instructor average: N/A");
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Rating ID", "Class ID", "Instructor", "Score", "Comment", "Date", "Instructor Avg"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tblRatings = new JTable(model);

    /** Creates the rating form and the student's rating history. */
    public RatingPanel() {
        setLayout(new BorderLayout(10, 10));
        JPanel top = new JPanel(new GridLayout(3, 1));
        top.add(UITheme.createTitle("Instructor Ratings"));
        JPanel controls = new JPanel();
        JButton btnSubmit = new JButton("Submit Rating");
        controls.add(new JLabel("Attended class:"));
        controls.add(cmbClass);
        controls.add(new JLabel("Score:"));
        controls.add(cmbScore);
        controls.add(new JLabel("Comment:"));
        controls.add(txtComment);
        controls.add(btnSubmit);
        top.add(controls);
        top.add(lblAverage);
        add(top, BorderLayout.NORTH);
        UITheme.styleTable(tblRatings);
        add(new JScrollPane(tblRatings), BorderLayout.CENTER);
        btnSubmit.addActionListener(e -> submit());
        cmbClass.addActionListener(e -> updateAverage());
    }

    /** Reloads eligible attended classes and rating history. */
    public void refresh() {
        try {
            String studentId = Session.getCurrentUser().getId();
            model.setRowCount(0);
            Set<String> rated = new HashSet<>();
            for (Rating r : ratings.getByStudent(studentId)) {
                rated.add(r.getClassId());
                User instructor = users.getById(r.getInstructorId());
                model.addRow(new Object[]{r.getId(), r.getClassId(), instructor == null ? r.getInstructorId() : instructor.getName(),
                        r.getScore(), r.getComment(), r.getDate(), String.format("%.2f", ratings.getAverageScore(r.getInstructorId()))});
            }
            cmbClass.removeAllItems();
            Set<String> listed = new HashSet<>();
            for (Booking b : bookings.getByStudent(studentId)) {
                ClassSession c = classes.getById(b.getClassId());
                if (c != null && !rated.contains(c.getId()) && listed.add(c.getId())
                        && attendance.hasAttended(studentId, c.getId())) {
                    cmbClass.addItem(c.getId() + " | " + c.getDiscipline() + " | " + c.getDate());
                }
            }
            updateAverage();
        } catch (RuntimeException ex) { showError(ex); }
    }

    private ClassSession selectedClass() {
        String selected = (String) cmbClass.getSelectedItem();
        return selected == null ? null : classes.getById(selected.split(" \\| ")[0]);
    }

    private void updateAverage() {
        try {
            ClassSession c = selectedClass();
            double avg = c == null ? 0 : ratings.getAverageScore(c.getInstructorId());
            lblAverage.setText("Instructor average: " + (avg == 0 ? "N/A" : String.format("%.2f / 5", avg)));
        } catch (RuntimeException ex) { showError(ex); }
    }

    private void submit() {
        try {
            ClassSession c = selectedClass();
            if (c == null) throw new InvalidInputException("Select an attended class.");
            Rating r = new Rating(null, Session.getCurrentUser().getId(), c.getInstructorId(), c.getId(),
                    (Integer) cmbScore.getSelectedItem(), txtComment.getText().trim(), LocalDate.now());
            ratings.addRating(r);
            JOptionPane.showMessageDialog(this, "Rating submitted.");
            txtComment.setText("");
            refresh();
        } catch (RuntimeException ex) { showError(ex); }
    }

    private void showError(RuntimeException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
