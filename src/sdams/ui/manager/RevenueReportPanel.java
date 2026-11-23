package sdams.ui.manager;

import sdams.ui.common.UITheme;

import javax.swing.*;
import java.awt.*;

public class RevenueReportPanel extends JPanel {

    public RevenueReportPanel() {
        setLayout(new BorderLayout());
        add(UITheme.createTitle("Revenue Report"), BorderLayout.NORTH);
    }
}