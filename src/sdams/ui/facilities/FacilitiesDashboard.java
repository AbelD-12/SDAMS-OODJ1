package sdams.ui.facilities;

import sdams.ui.common.BaseDashboard;

public class FacilitiesDashboard extends BaseDashboard {

    public FacilitiesDashboard() {
        addMenuItem("Equipment Inventory", new EquipmentInventoryPanel());
        addLogoutButton();
        showPanel("Equipment Inventory");
    }
}