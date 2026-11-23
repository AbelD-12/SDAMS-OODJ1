package sdams.ui.manager;

import sdams.ui.common.BaseDashboard;

public class ManagerDashboard extends BaseDashboard {

    public static final String MANAGE_ADMINS  = "Manage Administrators";
    public static final String STAFF_REPORT   = "Staff Report";
    public static final String REVENUE_REPORT = "Revenue Report";

    public ManagerDashboard() {

        super();

        addMenuItem(MANAGE_ADMINS,  new ManageAdministratorsPanel());
        addMenuItem(STAFF_REPORT,   new StaffReportPanel());
        addMenuItem(REVENUE_REPORT, new RevenueReportPanel());

        addLogoutButton();

        showPanel(MANAGE_ADMINS);
    }
}