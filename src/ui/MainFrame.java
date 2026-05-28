package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.GeneralSecurityException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import crypto.DsaService;
import model.DataMode;
import model.DsaKeyPairText;
import util.EncodingUtils;
import util.KeyFileFormatter;

public class MainFrame extends JFrame {
    private final DsaService dsaService = new DsaService();

    private final JTextArea inputArea = UiFactory.textArea(12);
    private final JTextArea privateKeyArea = UiFactory.textArea(6);
    private final JTextArea publicKeyArea = UiFactory.textArea(6);
    private final JTextArea signatureArea = UiFactory.textArea(6);
    private final JTextArea logArea = UiFactory.textArea(5);
    private final JTextField selectedFileField = UiFactory.textField();
    private final JTextField hashField = UiFactory.textField();
    private final JLabel statusLabel = new JLabel("Sẵn sàng");
    private final JRadioButton textModeButton = new JRadioButton("Văn bản", true);
    private final JRadioButton fileModeButton = new JRadioButton("Tệp tin");

    public MainFrame() {
        super("Hệ thống chữ ký số DSA");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 760));
        setContentPane(createContent());
        setSize(1220, 800);
        setLocationRelativeTo(null);
        generateKeyPair();
    }

    private JPanel createContent() {
        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBackground(Theme.BACKGROUND);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.add(createHeader(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createDataPanel(), createSignaturePanel());
        splitPane.setResizeWeight(0.45);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setDividerSize(10);
        root.add(splitPane, BorderLayout.CENTER);
        root.add(createLogPanel(), BorderLayout.SOUTH);
        return root;
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout(12, 8));
        panel.setOpaque(false);

        JLabel title = new JLabel("Hệ thống chữ ký số DSA");
        title.setForeground(Theme.TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));

        JLabel subtitle = new JLabel("Ký số và xác minh tính toàn vẹn dữ liệu bằng SHA256withDSA");
        subtitle.setForeground(Theme.MUTED_TEXT);
        subtitle.setFont(subtitle.getFont().deriveFont(13f));

        JPanel titlePanel = new JPanel(new BorderLayout(4, 4));
        titlePanel.setOpaque(false);
        titlePanel.add(title, BorderLayout.NORTH);
        titlePanel.add(subtitle, BorderLayout.CENTER);

        statusLabel.setOpaque(true);
        statusLabel.setBackground(Theme.SURFACE);
        statusLabel.setForeground(Theme.PRIMARY_DARK);
        statusLabel.setBorder(new EmptyBorder(8, 14, 8, 14));

        panel.add(titlePanel, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createDataPanel() {
        JPanel panel = UiFactory.card();
        panel.setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setOpaque(false);
        top.add(UiFactory.sectionTitle("Dữ liệu cần xử lý"), BorderLayout.WEST);
        top.add(createModeSelector(), BorderLayout.EAST);

        inputArea.setText("Nhập nội dung cần ký hoặc cần xác minh tại đây.");
        selectedFileField.setEditable(false);
        hashField.setEditable(false);

        panel.add(top, BorderLayout.NORTH);
        panel.add(UiFactory.scroll(inputArea), BorderLayout.CENTER);
        panel.add(createDataFooter(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createModeSelector() {
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        modePanel.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        group.add(textModeButton);
        group.add(fileModeButton);
        textModeButton.setOpaque(false);
        fileModeButton.setOpaque(false);
        modePanel.add(textModeButton);
        modePanel.add(fileModeButton);
        return modePanel;
    }

    private JPanel createDataFooter() {
        JPanel footer = new JPanel(new GridBagLayout());
        footer.setOpaque(false);
        GridBagConstraints gbc = baseConstraints();

        JButton chooseFileButton = UiFactory.secondaryButton("Chọn tệp");
        chooseFileButton.addActionListener(event -> chooseDataFile());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        footer.add(selectedFileField, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        footer.add(chooseFileButton, gbc);

        JButton hashButton = UiFactory.primaryButton("Tính hash");
        JButton clearButton = UiFactory.secondaryButton("Xóa");
        hashButton.addActionListener(event -> calculateHash());
        clearButton.addActionListener(event -> clearInput());

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        footer.add(hashField, gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(clearButton);
        actions.add(hashButton);

        gbc.gridx = 1;
        gbc.weightx = 0;
        footer.add(actions, gbc);
        return footer;
    }

    private JPanel createSignaturePanel() {
        JPanel panel = UiFactory.card();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = baseConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;

        addSection(panel, gbc, 0, "Khóa bí mật PKCS#8", privateKeyArea, 0.24);
        addSection(panel, gbc, 1, "Khóa công khai X.509", publicKeyArea, 0.22);
        addSection(panel, gbc, 2, "Chữ ký DSA Base64", signatureArea, 0.24);

        gbc.gridy = 3;
        gbc.weighty = 0;
        panel.add(createActionPanel(), gbc);
        return panel;
    }

    private void addSection(JPanel parent, GridBagConstraints gbc, int row, String title, JTextArea area, double weightY) {
        JPanel section = new JPanel(new BorderLayout(0, 6));
        section.setOpaque(false);
        section.add(UiFactory.sectionTitle(title), BorderLayout.NORTH);
        section.add(UiFactory.scroll(area), BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weighty = weightY;
        parent.add(section, gbc);
    }

    private JPanel createActionPanel() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);

        JButton generateButton = UiFactory.secondaryButton("Tạo khóa");
        JButton signButton = UiFactory.primaryButton("Ký dữ liệu");
        JButton verifyButton = UiFactory.primaryButton("Xác minh");
        JButton saveKeysButton = UiFactory.secondaryButton("Lưu khóa");
        JButton loadKeysButton = UiFactory.secondaryButton("Mở khóa");
        JButton saveSignatureButton = UiFactory.secondaryButton("Lưu chữ ký");
        JButton loadSignatureButton = UiFactory.secondaryButton("Mở chữ ký");

        generateButton.addActionListener(event -> generateKeyPair());
        signButton.addActionListener(event -> signData());
        verifyButton.addActionListener(event -> verifySignature());
        saveKeysButton.addActionListener(event -> saveKeys());
        loadKeysButton.addActionListener(event -> loadKeys());
        saveSignatureButton.addActionListener(event -> saveSignature());
        loadSignatureButton.addActionListener(event -> loadSignature());

        actions.add(generateButton);
        actions.add(signButton);
        actions.add(verifyButton);
        actions.add(saveKeysButton);
        actions.add(loadKeysButton);
        actions.add(saveSignatureButton);
        actions.add(loadSignatureButton);
        return actions;
    }

    private JPanel createLogPanel() {
        JPanel panel = UiFactory.card();
        panel.setLayout(new BorderLayout(0, 8));
        logArea.setEditable(false);
        panel.add(UiFactory.sectionTitle("Nhật ký xử lý"), BorderLayout.NORTH);
        panel.add(UiFactory.scroll(logArea), BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(100, 150));
        return panel;
    }

    private void chooseDataFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            selectedFileField.setText(file.getAbsolutePath());
            fileModeButton.setSelected(true);
            setStatus("Đã chọn tệp", Theme.PRIMARY_DARK);
            log("Đã chọn tệp: " + file.getName());
        }
    }

    private void generateKeyPair() {
        try {
            DsaKeyPairText keyPair = dsaService.generateKeyPair();
            privateKeyArea.setText(keyPair.privateKeyBase64());
            publicKeyArea.setText(keyPair.publicKeyBase64());
            signatureArea.setText("");
            setStatus("Đã tạo khóa", Theme.PRIMARY_DARK);
            log("Đã tạo cặp khóa DSA " + DsaService.KEY_SIZE + " bit.");
        } catch (GeneralSecurityException ex) {
            showError("Không thể tạo cặp khóa DSA.", ex);
        }
    }

    private void signData() {
        try {
            String signature = dsaService.sign(readCurrentData(), privateKeyArea.getText());
            signatureArea.setText(signature);
            calculateHash();
            setStatus("Ký thành công", Theme.SUCCESS);
            log("Ký thành công bằng " + DsaService.SIGNATURE_ALGORITHM + ".");
        } catch (GeneralSecurityException | IOException | IllegalArgumentException ex) {
            showError("Không thể ký dữ liệu.", ex);
        }
    }

    private void verifySignature() {
        try {
            boolean valid = dsaService.verify(readCurrentData(), signatureArea.getText(), publicKeyArea.getText());
            calculateHash();
            if (valid) {
                setStatus("Chữ ký hợp lệ", Theme.SUCCESS);
                log("Hợp lệ: dữ liệu còn nguyên vẹn và đúng với khóa công khai.");
                JOptionPane.showMessageDialog(this, "Chữ ký hợp lệ. Dữ liệu chưa bị thay đổi.",
                        "Kết quả xác minh", JOptionPane.INFORMATION_MESSAGE);
            } else {
                setStatus("Chữ ký không hợp lệ", Theme.WARNING);
                log("Không hợp lệ: dữ liệu, chữ ký hoặc khóa công khai không khớp.");
                JOptionPane.showMessageDialog(this, "Chữ ký không hợp lệ. Dữ liệu có thể đã bị sửa đổi.",
                        "Kết quả xác minh", JOptionPane.WARNING_MESSAGE);
            }
        } catch (GeneralSecurityException | IOException | IllegalArgumentException ex) {
            showError("Không thể xác minh chữ ký.", ex);
        }
    }

    private void calculateHash() {
        try {
            hashField.setText(dsaService.sha256(readCurrentData()));
            log("Đã tính SHA-256 cho " + currentMode().displayName() + ".");
        } catch (GeneralSecurityException | IOException | IllegalArgumentException ex) {
            showError("Không thể tính hash.", ex);
        }
    }

    private byte[] readCurrentData() throws IOException {
        if (currentMode() == DataMode.FILE) {
            String path = selectedFileField.getText().trim();
            if (path.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng chọn tệp tin cần xử lý.");
            }
            return Files.readAllBytes(new File(path).toPath());
        }
        return inputArea.getText().getBytes(StandardCharsets.UTF_8);
    }

    private DataMode currentMode() {
        return fileModeButton.isSelected() ? DataMode.FILE : DataMode.TEXT;
    }

    private void clearInput() {
        inputArea.setText("");
        selectedFileField.setText("");
        hashField.setText("");
        textModeButton.setSelected(true);
        setStatus("Đã xóa dữ liệu", Theme.MUTED_TEXT);
        log("Đã xóa dữ liệu đầu vào.");
    }

    private void saveKeys() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("dsa_keys.txt"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            DsaKeyPairText keyPair = new DsaKeyPairText(privateKeyArea.getText(), publicKeyArea.getText());
            writeTextFile(chooser.getSelectedFile(), KeyFileFormatter.format(keyPair), "Đã lưu cặp khóa.");
        }
    }

    private void loadKeys() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String content = Files.readString(chooser.getSelectedFile().toPath(), StandardCharsets.UTF_8);
                DsaKeyPairText keyPair = KeyFileFormatter.parse(content);
                dsaService.parsePrivateKey(keyPair.privateKeyBase64());
                dsaService.parsePublicKey(keyPair.publicKeyBase64());
                privateKeyArea.setText(keyPair.privateKeyBase64());
                publicKeyArea.setText(keyPair.publicKeyBase64());
                setStatus("Đã mở khóa", Theme.PRIMARY_DARK);
                log("Đã mở cặp khóa từ tệp.");
            } catch (IOException | GeneralSecurityException | RuntimeException ex) {
                showError("Tệp khóa không hợp lệ.", ex);
            }
        }
    }

    private void saveSignature() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("dsa_signature.txt"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            writeTextFile(chooser.getSelectedFile(), signatureArea.getText().trim() + "\n", "Đã lưu chữ ký.");
        }
    }

    private void loadSignature() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String signature = Files.readString(chooser.getSelectedFile().toPath(), StandardCharsets.UTF_8).trim();
                EncodingUtils.fromBase64(signature);
                signatureArea.setText(signature);
                setStatus("Đã mở chữ ký", Theme.PRIMARY_DARK);
                log("Đã mở chữ ký từ tệp.");
            } catch (IOException | IllegalArgumentException ex) {
                showError("Tệp chữ ký không hợp lệ.", ex);
            }
        }
    }

    private void writeTextFile(File file, String content, String successMessage) {
        try {
            Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
            setStatus("Đã lưu tệp", Theme.PRIMARY_DARK);
            log(successMessage + " " + file.getAbsolutePath());
        } catch (IOException ex) {
            showError("Không thể ghi tệp.", ex);
        }
    }

    private void setStatus(String text, java.awt.Color color) {
        statusLabel.setText(text);
        statusLabel.setForeground(color);
    }

    private void log(String message) {
        logArea.append(message + System.lineSeparator());
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void showError(String message, Exception ex) {
        setStatus("Có lỗi", Theme.DANGER);
        log(message + " " + ex.getMessage());
        JOptionPane.showMessageDialog(this, message + "\n" + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private static GridBagConstraints baseConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }
}
