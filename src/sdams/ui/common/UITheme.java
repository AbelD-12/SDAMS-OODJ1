package sdams.ui.common;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class UITheme {

    public static final Font TITLE_FONT =
            new Font("Arial", Font.BOLD, 24);

    public static final Font SUBTITLE_FONT =
            new Font("Arial", Font.BOLD, 18);

    public static final Font NORMAL_FONT =
            new Font("Arial", Font.PLAIN, 14);

    public static final Font BUTTON_FONT =
            new Font("Arial", Font.BOLD, 14);

    public static final Color PRIMARY_COLOR =
            new Color(35, 55, 85);

    public static final Color SECONDARY_COLOR =
            new Color(240, 243, 247);

    public static final Color TEXT_COLOR =
            new Color(40, 40, 40);

    public static final Color WHITE =
            Color.WHITE;

    private UITheme() {
        // Utility class
    }

    public static void styleButton(JButton button) {

        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );
    }

    public static void styleTable(JTable table) {

        table.setFont(NORMAL_FONT);
        table.setRowHeight(28);
        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JTableHeader header =
                table.getTableHeader();

        header.setFont(BUTTON_FONT);
    }

    public static JLabel createTitle(String text) {

        JLabel label = new JLabel(text);

        label.setFont(TITLE_FONT);
        label.setForeground(TEXT_COLOR);

        return label;
    }

    public static JLabel createSubtitle(String text) {

        JLabel label = new JLabel(text);

        label.setFont(SUBTITLE_FONT);
        label.setForeground(TEXT_COLOR);

        return label;
    }

    public static void styleTextField(
            JTextField field
    ) {

        field.setFont(NORMAL_FONT);
    }

    public static void stylePasswordField(
            JPasswordField field
    ) {

        field.setFont(NORMAL_FONT);
    }
}
