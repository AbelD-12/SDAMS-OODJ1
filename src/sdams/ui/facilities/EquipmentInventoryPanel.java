package sdams.ui.facilities;

import sdams.enums.EquipmentCondition;
import sdams.model.Equipment;
import sdams.service.EquipmentService;
import sdams.util.DataAccessException;
import sdams.util.InvalidInputException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

public class EquipmentInventoryPanel extends JPanel {

    private final EquipmentService service = new EquipmentService();

    private final JTextField nameField = new JTextField(20);
    private final JTextField quantityField = new JTextField(10);
    private final JTextField searchField = new JTextField(12);
    private final JComboBox<EquipmentCondition> conditionBox =
            new JComboBox<>(EquipmentCondition.values());

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Equipment ID", "Name", "Quantity", "Condition"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);

    public EquipmentInventoryPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.add(new JLabel("Equipment name"));
        form.add(nameField);
        form.add(new JLabel("Quantity"));
        form.add(quantityField);
        form.add(new JLabel("Condition"));
        form.add(conditionBox);
        add(form, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(event -> {
            int row = table.getSelectedRow();
            if (!event.getValueIsAdjusting() && row >= 0) {
                nameField.setText(tableModel.getValueAt(row, 1).toString());
                quantityField.setText(tableModel.getValueAt(row, 2).toString());
                conditionBox.setSelectedItem(tableModel.getValueAt(row, 3));
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(event -> runAction(this::addEquipment));
        updateButton.addActionListener(event -> runAction(this::updateEquipment));
        deleteButton.addActionListener(event -> runAction(this::deleteEquipment));
        searchButton.addActionListener(event -> runAction(this::loadEquipment));
        searchField.addActionListener(event -> runAction(this::loadEquipment));
        refreshButton.addActionListener(event -> runAction(this::refresh));

        JPanel buttons = new JPanel();
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(searchField);
        buttons.add(searchButton);
        buttons.add(refreshButton);
        add(buttons, BorderLayout.SOUTH);

        runAction(this::loadEquipment);
    }

    private void addEquipment() {
        service.addEquipment(nameField.getText(),
                Integer.parseInt(quantityField.getText().trim()),
                (EquipmentCondition) conditionBox.getSelectedItem());
        refresh();
    }

    private void updateEquipment() {
        service.updateEquipment(selectedId(), nameField.getText(),
                Integer.parseInt(quantityField.getText().trim()),
                (EquipmentCondition) conditionBox.getSelectedItem());
        refresh();
    }

    private void deleteEquipment() {
        String id = selectedId();
        int choice = JOptionPane.showConfirmDialog(
                this, "Delete equipment " + id + "?",
                "Delete Equipment", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            service.delete(id);
            loadEquipment();
        }
    }

    private String selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            throw new InvalidInputException("Select an equipment item first.");
        }
        return tableModel.getValueAt(row, 0).toString();
    }

    private void refresh() {
        searchField.setText("");
        loadEquipment();
    }

    private void loadEquipment() {
        List<Equipment> items = service.search(searchField.getText().trim());

        tableModel.setRowCount(0);
        for (Equipment item : items) {
            tableModel.addRow(new Object[]{
                    item.getId(), item.getName(), item.getQuantity(), item.getCondition()
            });
        }
    }

    private void runAction(Runnable action) {
        try {
            action.run();
        } catch (NumberFormatException ex) {
            showError("Enter a whole number for quantity.");
        } catch (InvalidInputException | DataAccessException ex) {
            showError(ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this, message, "Equipment Inventory", JOptionPane.ERROR_MESSAGE);
    }
}