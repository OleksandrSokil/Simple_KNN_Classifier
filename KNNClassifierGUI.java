import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class KNNClassifierGUI extends JFrame {
    private KNNClassifier knn;
    private JTextField kInput, sampleInput;
    private JTextArea outputArea;
    private JButton trainButton, classifyButton, selectTrainingFileButton, selectTestFileButton;
    private JLabel trainingFileLabel, testFileLabel;
    private File trainingFile, testFile;

    public KNNClassifierGUI() {
        knn = new KNNClassifier();
        setTitle("🌟 KNN Classifier");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Customizing UI
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 14));
        UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("TextArea.font", new Font("SansSerif", Font.PLAIN, 14));

        JPanel filePanel = new JPanel(new GridLayout(2, 2, 10, 10));
        filePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        filePanel.setBackground(new Color(40, 44, 52));

        selectTrainingFileButton = createStyledButton("📂 Select Training File");
        trainingFileLabel = createStyledLabel("No file selected");

        selectTestFileButton = createStyledButton("📂 Select Test File");
        testFileLabel = createStyledLabel("No file selected");

        filePanel.add(selectTrainingFileButton);
        filePanel.add(trainingFileLabel);
        filePanel.add(selectTestFileButton);
        filePanel.add(testFileLabel);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputPanel.setBackground(new Color(50, 54, 62));

        JLabel kLabel = createStyledLabel("Enter k:");
        kInput = createStyledTextField();
        trainButton = createStyledButton("🚀 Train Model");

        JLabel sampleLabel = createStyledLabel("Enter Sample:");
        sampleInput = createStyledTextField();
        classifyButton = createStyledButton("🔍 Classify");
        classifyButton.setEnabled(false);

        inputPanel.add(kLabel);
        inputPanel.add(kInput);
        inputPanel.add(trainButton);
        inputPanel.add(sampleLabel);
        inputPanel.add(sampleInput);
        inputPanel.add(classifyButton);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(30, 34, 42));
        outputArea.setForeground(Color.WHITE);
        outputArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        add(filePanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        selectTrainingFileButton.addActionListener(e -> selectFile(true));
        selectTestFileButton.addActionListener(e -> selectFile(false));
        trainButton.addActionListener(e -> trainModel());
        classifyButton.addActionListener(e -> classifySample());
    }

    private void selectFile(boolean isTraining) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (isTraining) {
                trainingFile = selectedFile;
                trainingFileLabel.setText("📁 " + trainingFile.getName());
            } else {
                testFile = selectedFile;
                testFileLabel.setText("📁 " + testFile.getName());
            }
        }
    }

    private void trainModel() {
        if (trainingFile == null || testFile == null) {
            outputArea.append("⚠️ Please select both training and test files.\n");
            return;
        }
        try {
            knn.loadData(trainingFile.getAbsolutePath(), true);
            knn.loadData(testFile.getAbsolutePath(), false);

            int k = Integer.parseInt(kInput.getText());
            double accuracy = knn.evaluateModel(k);
            outputArea.append("✅ Model trained with k = " + k + "\n");
            outputArea.append(String.format("📊 Accuracy: %.2f%%\n", accuracy));

            classifyButton.setEnabled(true);
        } catch (NumberFormatException ex) {
            outputArea.append("❌ Invalid k value.\n");
        }
    }

    private void classifySample() {
        try {
            int k = Integer.parseInt(kInput.getText());
            String inputText = sampleInput.getText();
            String[] parts = inputText.split(",");
            double[] newSample = new double[parts.length];

            for (int i = 0; i < parts.length; i++) {
                newSample[i] = Double.parseDouble(parts[i].trim());
            }

            String classification = knn.classify(newSample, k);
            outputArea.append("🔍 Predicted class: " + classification + "\n");
        } catch (Exception ex) {
            outputArea.append("⚠️ Error processing input.\n");
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(60, 63, 65));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        return button;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setBackground(new Color(40, 44, 52));
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(Color.WHITE);
        return textField;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KNNClassifierGUI().setVisible(true));
    }
}
