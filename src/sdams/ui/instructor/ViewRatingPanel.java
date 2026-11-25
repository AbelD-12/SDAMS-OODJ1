package sdams.ui.instructor;

import sdams.model.Instructor;
import sdams.model.Rating;
import sdams.service.RatingService;
import sdams.util.DataAccessException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.util.List;
import java.util.regex.Pattern;

public class ViewRatingPanel extends JPanel {

    private final Instructor instructor;
    private final RatingService ratingService = new RatingService();

    private final JLabel averageLabel = new JLabel("", SwingConstants.CENTER);
    private final JTextField searchField = new JTextField(16);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Rating ID", "Student ID", "Class ID", "Score", "Comment", "Date"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final TableRowSorter<DefaultTableModel> sorter =
            new TableRowSorter<>(tableModel);

    public ViewRatingPanel(Instructor instructor) {
        this.instructor = instructor;
        setLayout(new BorderLayout(10, 10));

        add(averageLabel, BorderLayout.NORTH);

        JTable table = new JTable(tableModel);
        table.setRowSorter(sorter);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel();
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");

        searchButton.addActionListener(e -> searchRatings());
        searchField.addActionListener(e -> searchRatings());

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            searchRatings();
            loadRatings();
        });

        actions.add(searchField);
        actions.add(searchButton);
        actions.add(refreshButton);
        add(actions, BorderLayout.SOUTH);

        loadRatings();
    }

    private void loadRatings() {
        try {
            String instructorId = instructor.getId();
            List<Rating> ratings = ratingService.getByInstructor(instructorId);
            double average = ratingService.getAverageScore(instructorId);

            tableModel.setRowCount(0);

            for (Rating rating : ratings) {
                tableModel.addRow(new Object[]{
                        rating.getId(),
                        rating.getStudentId(),
                        rating.getClassId(),
                        rating.getScore(),
                        rating.getComment(),
                        rating.getDate()
                });
            }

            if (ratings.isEmpty()) {
                averageLabel.setText("No ratings yet");
            } else {
                averageLabel.setText(String.format("Average rating %.2f / 5", average));
            }
        } catch (DataAccessException ex) {
            JOptionPane.showMessageDialog(
                    this, ex.getMessage(), "View Ratings", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void searchRatings() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?iu)" + Pattern.quote(keyword)));
        }
    }
}