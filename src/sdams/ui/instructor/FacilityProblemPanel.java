package sdams.ui.instructor;

import sdams.model.FacilityProblem;
import sdams.model.Instructor;
import sdams.service.FacilityProblemService;
import sdams.util.DataAccessException;
import sdams.util.InvalidInputException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

public class FacilityProblemPanel extends JPanel {

    private final Instructor instructor;
    private final FacilityProblemService problems = new FacilityProblemService();

    private final JTextField facility = new JTextField(20);
    private final JTextField description = new JTextField(30);
    private final JTextField search = new JTextField(12);

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Problem ID", "Facility", "Description", "Date", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);

    public FacilityProblemPanel(Instructor instructor) {
        this.instructor = instructor;
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Facility name"));
        form.add(facility);
        form.add(new JLabel("Problem description"));
        form.add(description);
        add(form, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (e.getValueIsAdjusting() || row < 0) {
                return;
            }

            facility.setText(model.getValueAt(row, 1).toString());
            description.setText(model.getValueAt(row, 2).toString());
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel();

        addAction(actions, "Report Problem", () -> {
            problems.reportProblem(
                    instructor.getId(), facility.getText(), description.getText()
            );
            search.setText("");
            loadReports();
        });

        addAction(actions, "Update", () -> {
            problems.revise(
                    instructor.getId(), selectedId(),
                    facility.getText(), description.getText()
            );
            loadReports();
        });

        addAction(actions, "Delete", this::deleteReport);

        actions.add(search);
        addAction(actions, "Search", this::loadReports);
        addAction(actions, "Refresh", () -> {
            search.setText("");
            loadReports();
        });

        search.addActionListener(e -> runAction(this::loadReports));
        add(actions, BorderLayout.SOUTH);

        runAction(this::loadReports);
    }

    private String selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            throw new InvalidInputException("Select a report from the table first.");
        }
        return model.getValueAt(row, 0).toString();
    }

    private void deleteReport() {
        String id = selectedId();
        int choice = JOptionPane.showConfirmDialog(
                this, "Delete " + id + "?", "Delete Report",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            problems.remove(instructor.getId(), id);
            loadReports();
        }
    }

    private void loadReports() {
        List<FacilityProblem> matches = problems.search(search.getText().trim());
        model.setRowCount(0);

        for (FacilityProblem problem : matches) {
            if (instructor.getId().equals(problem.getInstructorId())) {
                model.addRow(new Object[]{
                        problem.getId(),
                        problem.getFacilityName(),
                        problem.getDescription(),
                        problem.getDate(),
                        problem.getStatus()
                });
            }
        }
    }

    private void addAction(JPanel panel, String label, Runnable action) {
        JButton button = new JButton(label);
        button.addActionListener(e -> runAction(action));
        panel.add(button);
    }

    private void runAction(Runnable action) {
        try {
            action.run();
        } catch (InvalidInputException | DataAccessException ex) {
            JOptionPane.showMessageDialog(
                    this, ex.getMessage(), "Facility Problems",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}