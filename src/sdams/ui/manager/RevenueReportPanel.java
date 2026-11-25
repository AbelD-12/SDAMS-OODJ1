package sdams.ui.manager;

import sdams.model.RevenueRecord;
import sdams.service.ReportService;
import sdams.ui.common.UITheme;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class RevenueReportPanel extends JPanel {

    private ReportService reportService = new ReportService();

    private String[] months = {
            "All Months", "January", "February", "March", "April", "May",
            "June", "July", "August", "September", "October",
            "November", "December"
    };

    private String[] columns = {"Period", "Registrations", "Total Revenue"};

    private JLabel lblTitle;

    private JRadioButton rdoMonthly;
    private JRadioButton rdoYearly;
    private JLabel lblYear;
    private JComboBox<String> cboYear;
    private JLabel lblMonth;
    private JComboBox<String> cboMonth;
    private JButton btnView;

    private JTable table;
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
        cboMonth = new JComboBox<>(months);

        lblYear.setBounds(210, 55, 40, 25);
        cboYear.setBounds(250, 55, 90, 25);
        lblMonth.setBounds(360, 55, 55, 25);
        cboMonth.setBounds(415, 55, 130, 25);

        // ----- view button
        btnView = new JButton("View Report");
        btnView.setBounds(565, 55, 150, 25);
        UITheme.styleButton(btnView);

        // ----- table
        table = new JTable(new Object[0][3], columns);
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

        // ----- action (lambda expression)
        btnView.addActionListener(e -> viewReport());

        loadYears();
        viewReport();
    }

    // Puts every year that has PAID payments into the year box
    private void loadYears() {

        List<RevenueRecord> years = reportService.getYearlyRevenue();

        // newest year first
        for (int i = years.size() - 1; i >= 0; i--) {
            cboYear.addItem(years.get(i).getPeriod());
        }
    }

    private void viewReport() {

        try {

            if (rdoMonthly.isSelected()) {
                showMonthly();
            } else {
                showTable(reportService.getYearlyRevenue());
            }

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "payments.txt has a wrong line:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMonthly() {

        String year = (String) cboYear.getSelectedItem();

        // no payments in the file at all
        if (year == null) {
            showTable(new ArrayList<>());
            return;
        }

        List<RevenueRecord> records =
                reportService.getMonthlyRevenue(Integer.parseInt(year));

        int choice = cboMonth.getSelectedIndex();   // 0 = All Months

        // one month chosen: keep only that month
        if (choice != 0) {
            RevenueRecord one = records.get(choice - 1);
            records = new ArrayList<>();
            records.add(one);
        }

        showTable(records);
    }

    // Shows the records in the table and the grand total under it
    private void showTable(List<RevenueRecord> records) {

        Object[][] data = new Object[records.size()][3];

        int totalRegistrations = 0;
        double grandTotal = 0;

        for (int i = 0; i < records.size(); i++) {

            RevenueRecord record = records.get(i);

            data[i][0] = record.getPeriod();
            data[i][1] = record.getRegistrations();
            data[i][2] = "RM " + String.format("%.2f", record.getTotalRevenue());

            totalRegistrations += record.getRegistrations();
            grandTotal += record.getTotalRevenue();
        }

        table = new JTable(data, columns);
        UITheme.styleTable(table);
        scrollPane.setViewportView(table);

        lblRegistrations.setText("Total registrations: " + totalRegistrations);
        lblGrandTotal.setText(
                "Grand total revenue: RM " + String.format("%.2f", grandTotal));
    }
}