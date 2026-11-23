package sdams.ui.manager;

import sdams.ui.common.UITheme;

import javax.swing.*;
import java.awt.*;

public class ManageAdministratorsPanel extends JPanel {

    public ManageAdministratorsPanel() {
        setLayout(new BorderLayout());
        add(UITheme.createTitle("Manage Administrators"), BorderLayout.NORTH);
    }
}