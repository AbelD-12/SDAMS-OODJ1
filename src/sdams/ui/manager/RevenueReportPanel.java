package sdams.ui.manager;

import sdams.model.RevenueRecord;
import sdams.service.ReportService;
import sdams.ui.common.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class RevenueReportPanel extends JPanel {

    private ReportService reportService = new ReportService();

    private String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    private JLabel lblTitle;

    private JRadioButton rdoMonthly;
    private JRadioButton rdoYearly;
    private JLabel lblYear;
    private JComboBox<String> cboYear;
    private JLabel lblMonth;
    private JComboBox<String> cboMonth;
    private JButton btnView;

    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private JLabel lblRegistrations;
    private JLabel lblGrandTotal;

    public RevenueReportPanel() {

        setLayout(null);

        // ----- title
        lblTitle = UITheme.createTitle("Revenue Report");
        lblTitle.setBounds(10, 10, 400, 35);

        // ----- report type (only one can be selected)
        rdoMonthly = new JRadioButton("Monthly", true);
        rdoYearly = new JRadioButton("Yearly");

        ButtonGroup group = new ButtonGroup();
        group.add(rdoMonthly);
        group.add(rdoYearly);

        rdoMonthly.setBounds(10, 55, 90, 25);
        rdoYearly.setBounds(100, 55, 90, 25);

        // ----- year and month
        lblYear = new JLabel("Year:");
        cboYear = new JComboBox<>();
        lblMonth = new JLabel("Month:");
        cboMonth = new JComboBox<>();

        cboMonth.addItem("All Months");
        for (int i = 0; i < monthNames.length; i++) {
            cboMonth.addItem(monthNames[i]);
        }

        lblYear.setBounds(210, 55, 40, 25);
        cboYear.setBounds(250, 55, 90, 25);
        lblMonth.setBounds(360, 55, 55, 25);
        cboMonth.setBounds(415, 55, 130, 25);

        // ----- view button
        btnView = new JButton("View Report");
        btnView.setBounds(565, 55, 150, 25);
        UITheme.styleButton(btnView);

        // ----- table
        String[] columns = {"Period", "Registrations", "Total Revenue"};

        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setDefaultEditor(Object.class, null);   // cells cannot be typed in
        UITheme.styleTable(table);

        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 90, 820, 330);

        // ----- grand total
        lblRegistrations = new JLabel();
        lblGrandTotal = new JLabel();

        lblRegistrations.setFont(UITheme.BUTTON_FONT);
        lblGrandTotal.setFont(UITheme.SUBTITLE_FONT);

        lblRegistrations.setBounds(10, 435, 400, 25);
        lblGrandTotal.setBounds(10, 465, 500, 30);

        // ----- add everything to the panel
        add(lblTitle);
        add(rdoMonthly);
        add(rdoYearly);
        add(lblYear);
        add(cboYear);
        add(lblMonth);
        add(cboMonth);
        add(btnView);
        add(scrollPane);
        add(lblRegistrations);
        add(lblGrandTotal);

        // ----- actions (lambda expressions)
        btnView.addActionListener(e -> generateReport());

        // a yearly report shows every year, so year and month are not needed
        rdoMonthly.addActionListener(e -> {
            cboYear.setEnabled(true);
            cboMonth.setEnabled(true);
        });

        rdoYearly.addActionListener(e -> {
            cboYear.setEnabled(false);
            cboMonth.setEnabled(false);
        });

        loadYears();
        generateReport();
    }

    // Fills the year box with every year that has PAID payments
    private void loadYears() {

        List<RevenueRecord> years = reportService.getYearlyRevenue();

        // newest year first
        for (int i = years.size() - 1; i >= 0; i--) {
            cboYear.addItem(years.get(i).getPeriod());
        }
    }

    private void generateReport() {

        try {

            if (rdoMonthly.isSelected()) {
                showMonthly();
            } else {
                showTable(reportService.getYearlyRevenue());
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "payments.txt contains an invalid amount.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMonthly() {

        String year = (String) cboYear.getSelectedItem();

        List<RevenueRecord> records = new ArrayList<>();

        if (year != null) {

            List<RevenueRecord> allMonths =
                    reportService.getMonthlyRevenue(Integer.parseInt(year));

            int choice = cboMonth.getSelectedIndex();   // 0 = All Months

            if (choice == 0) {
                records = allMonths;
            } else {
                records.add(allMonths.get(choice - 1));
            }
        }

        showTable(records);
    }

    // Shows the records in the table and the grand total under it
    private void showTable(List<RevenueRecord> records) {

        tableModel.setRowCount(0);

        int totalRegistrations = 0;
        double grandTotal = 0;

        for (RevenueRecord record : records) {

            Object[] row = {
                    record.getPeriod(),
                    record.getRegistrations(),
                    "RM " + money(record.getTotalRevenue())
            };

            tableModel.addRow(row);

            totalRegistrations += record.getRegistrations();
            grandTotal += record.getTotalRevenue();
        }

        lblRegistrations.setText("Total registrations: " + totalRegistrations);
        lblGrandTotal.setText("Grand total revenue: RM " + money(grandTotal));
    }

    // 2000 -> "2,000.00"
    private String money(double amount) {
        return String.format("%,.2f", amount);
    }
}