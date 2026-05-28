package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public final class UiFactory {
    private UiFactory() {
    }

    public static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(Theme.SURFACE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)));
        return panel;
    }

    public static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Theme.TEXT);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 15f));
        return label;
    }

    public static JLabel caption(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Theme.MUTED_TEXT);
        label.setFont(label.getFont().deriveFont(12f));
        return label;
    }

    public static JButton primaryButton(String text) {
        return button(text, Theme.PRIMARY, Color.WHITE);
    }

    public static JButton secondaryButton(String text) {
        return button(text, Theme.SURFACE_MUTED, Theme.TEXT);
    }

    public static JButton dangerButton(String text) {
        return button(text, Theme.DANGER, Color.WHITE);
    }

    public static JScrollPane scroll(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(new LineBorder(Theme.BORDER, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    public static JTextArea textArea(int rows) {
        JTextArea area = new JTextArea(rows, 20);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(10, 10, 10, 10));
        return area;
    }

    public static JTextField textField() {
        JTextField field = new JTextField();
        field.setBorder(new CompoundBorder(
                new LineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(7, 9, 7, 9)));
        return field;
    }

    private static JButton button(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
                new LineBorder(background.darker(), 1, true),
                new EmptyBorder(8, 13, 8, 13)));
        button.setPreferredSize(new Dimension(Math.max(112, button.getPreferredSize().width), 38));
        return button;
    }
}
