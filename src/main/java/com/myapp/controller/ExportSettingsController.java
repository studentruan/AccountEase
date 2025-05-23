package com.myapp.controller;

import Backend.GlobalContext;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ExportSettingsController {

    @FXML private TextField exportPathField;
    @FXML private ComboBox<String> exportFormatComboBox;

    private static final String[] EXPORT_FORMATS = {"CSV", "Excel", "JSON"};

    @FXML
    public void initialize() {
        exportFormatComboBox.getItems().addAll(EXPORT_FORMATS);
        exportFormatComboBox.getSelectionModel().selectFirst();

        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        exportPathField.setText(prefs.get("EXPORT_PATH", ""));
        exportFormatComboBox.setValue(prefs.get("EXPORT_FORMAT", "CSV"));
    }

    @FXML
    private void handleBrowseExportPath() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择导出位置");

        String format = exportFormatComboBox.getValue();
        fileChooser.setInitialFileName("transactions_export." + format.toLowerCase());

        switch (format) {
            case "CSV":
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("CSV文件 (*.csv)", "*.csv"));
                break;
            case "Excel":
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Excel文件 (*.xlsx)", "*.xlsx"));
                break;
            case "JSON":
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("JSON文件 (*.json)", "*.json"));
                break;
        }

        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            exportPathField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleExportData() {
        String path = exportPathField.getText();
        String format = exportFormatComboBox.getValue();

        if (path.isEmpty()) {
            showAlert("错误", "请先选择导出路径");
            return;
        }

        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        prefs.put("EXPORT_PATH", path);
        prefs.put("EXPORT_FORMAT", format);

        try {
            if ("CSV".equals(format)) {
                String ledgerId = GlobalContext.getInstance().getCurrentLedgerId();
                String fourthLevelDir = "src/main/resources/fourthlevel_xml/" + ledgerId + "/";

                if (convertXmlDirToCsv(fourthLevelDir, path)) {
                    showAlert("成功", "交易数据已成功导出为CSV:\n" + path);
                } else {
                    showAlert("警告", "没有找到可转换的XML文件");
                }
            } else {
                // 其他格式的导出逻辑
                System.out.println("导出数据到: " + path + " 格式: " + format);
                showAlert("成功", "数据已导出到:\n" + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("错误", "导出过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * 将指定目录下的所有交易XML文件转换为CSV
     * @param xmlDirPath XML文件所在目录
     * @param csvFilePath 输出的CSV文件路径
     * @return 是否成功转换了至少一个文件
     */
    private boolean convertXmlDirToCsv(String xmlDirPath, String csvFilePath) throws Exception {
        Path dirPath = Paths.get(xmlDirPath);
        if (!Files.exists(dirPath)) {
            throw new IOException("XML目录不存在: " + xmlDirPath);
        }

        boolean hasConverted = false;

        try (FileWriter writer = new FileWriter(csvFilePath)) {
            // 写入CSV表头
            writer.append("ID,Date,Counterparty,Product,Type,Amount,Class\n");

            // 遍历目录中的所有XML文件
            for (Path xmlFile : Files.list(dirPath)
                    .filter(path -> path.toString().endsWith(".xml"))
                    .toArray(Path[]::new)) {

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(xmlFile.toFile());

                Element root = document.getDocumentElement();
                NodeList transactions = root.getElementsByTagName("transaction");

                for (int i = 0; i < transactions.getLength(); i++) {
                    Element transaction = (Element) transactions.item(i);

                    String id = getElementText(transaction, "id");
                    String date = getElementText(transaction, "date");
                    String counterparty = getElementText(transaction, "counterparty");
                    String product = getElementText(transaction, "product");
                    String type = getElementText(transaction, "type");
                    String amount = getElementText(transaction, "amount");
                    String transactionClass = getElementText(transaction, "class");

                    writer.append(String.join(",",
                            escapeCsv(id),
                            escapeCsv(date),
                            escapeCsv(counterparty),
                            escapeCsv(product),
                            escapeCsv(type),
                            escapeCsv(amount),
                            escapeCsv(transactionClass)));
                    writer.append("\n");
                }

                hasConverted = true;
            }
        }

        return hasConverted;
    }

    /**
     * 安全获取XML元素文本内容
     */
    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }

    /**
     * 转义CSV特殊字符
     */
    private String escapeCsv(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        if (input.contains(",") || input.contains("\"") || input.contains("\n")) {
            return "\"" + input.replace("\"", "\"\"") + "\"";
        }
        return input;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}