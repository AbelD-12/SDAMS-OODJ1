package sdams.ui.manager;

import sdams.ui.common.UITheme;

import javax.swing.*;
import java.awt.*;

public class StaffReportPanel extends JPanel {

    public StaffReportPanel() {
        setLayout(new BorderLayout());
        add(UITheme.createTitle("Staff Report"), BorderLayout.NORTH);
    }
}