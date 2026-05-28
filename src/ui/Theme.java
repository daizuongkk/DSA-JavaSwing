package ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public final class Theme {
    public static final Color BACKGROUND = new Color(245, 247, 251);
    public static final Color SURFACE = Color.WHITE;
    public static final Color SURFACE_MUTED = new Color(239, 243, 248);
    public static final Color BORDER = new Color(220, 226, 235);
    public static final Color TEXT = new Color(28, 35, 45);
    public static final Color MUTED_TEXT = new Color(96, 108, 124);
    public static final Color PRIMARY = new Color(30, 111, 236);
    public static final Color PRIMARY_DARK = new Color(23, 83, 178);
    public static final Color SUCCESS = new Color(31, 143, 91);
    public static final Color WARNING = new Color(198, 94, 26);
    public static final Color DANGER = new Color(194, 52, 71);

    private Theme() {
    }

    public static void install() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException ignored) {
        }

        Font baseFont = new Font("Segoe UI", Font.PLAIN, 14);
        UIManager.put("defaultFont", baseFont);
        UIManager.put("Label.font", baseFont);
        UIManager.put("Button.font", baseFont.deriveFont(Font.BOLD));
        UIManager.put("RadioButton.font", baseFont);
        UIManager.put("TextArea.font", new Font("Consolas", Font.PLAIN, 13));
        UIManager.put("TextField.font", new Font("Consolas", Font.PLAIN, 13));
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
    }
}
