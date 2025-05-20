package com.myapp.controller;

import AIUtilities.classification.ClassificationXmlWriter;
import AIUtilities.classification.Transaction;
import AIUtilities.classification.TransactionClassifier;
import AIUtilities.classification.TransactionXmlParser;

import Backend.*;

import DataProcessor.CsvToXmlConverter;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InputSettingsController {

    @FXML
    private TextField filePathField;
    private static final String THIRD_DIR = "src/main/resources/thirdlevel_json/";

    // 文件选择逻辑
    @FXML
    private void handleBrowseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择账单文件");

        // 设置文件过滤器
        FileChooser.ExtensionFilter excelFilter = new FileChooser.ExtensionFilter(
                "电子表格文件 (*.xlsx, *.csv)", "*.xlsx", "*.csv", "*.xml");
        fileChooser.getExtensionFilters().add(excelFilter);

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    // 保存路径逻辑
    @FXML
    private void saveFilePath() {
        String filePath = filePathField.getText();
        if (filePath == null || filePath.isEmpty()) {
            System.err.println("请选择一个有效的文件路径");
            return;
        }

        File selectedFile = new File(filePath);
        if (!selectedFile.exists() || !selectedFile.isFile()) {
            System.err.println("选择的文件无效或不存在");
            return;
        }
        String ledgerId = GlobalContext.getInstance().getCurrentLedgerId();

        // 步骤2：验证ID有效性
        if (ledgerId == null || ledgerId.trim().isEmpty()) {
            throw new IllegalStateException("未找到有效的账本ID，请先选择账本");
        }
        // 定义目录路径
        String inputDir = "src/main/resources/Transactions_Record_CSV/"+ledgerId+"/";
        String outputDir = "src/main/resources/Transactions_Record_XML/"+ledgerId+"/";

        String fourthLevelDir = "src/main/resources/fourthlevel_xml/"+ledgerId+"/"; // 新增四级目录

        try {


            // 1. 确保输入目录存在
            File inputDirectory = new File(inputDir);
            if (!inputDirectory.exists() && !inputDirectory.mkdirs()) {
                System.err.println("无法创建输入目录: " + inputDir);
                return;
            }

            // 2. 将文件复制到输入目录
            File targetCsvFile = new File(inputDir, selectedFile.getName());
            Files.copy(
                    selectedFile.toPath(),
                    targetCsvFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );

            // 3. 准备输出路径（同名 XML 文件）
            String xmlFileName = selectedFile.getName().replace(".csv", ".xml");
            File outputXmlFile = new File(outputDir, xmlFileName);

            // 确保输出目录存在
            File outputDirectory = new File(outputDir);
            if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
                System.err.println("无法创建输出目录: " + outputDir);
                return;
            }

            // 4. 执行 CSV 转 XML
            CsvToXmlConverter.convert(targetCsvFile.getAbsolutePath(), outputXmlFile.getAbsolutePath());
            System.out.println("CSV 转 XML 成功: " + outputXmlFile.getAbsolutePath());

            // 5. 解析 XML 文件获取交易数据
            List<Transaction> transactions = TransactionXmlParser.parse(outputXmlFile.getAbsolutePath());

            // 6. 初始化分类器（需替换为实际路径）
            String tokenizerDir = "src/main/resources/Tokenizer";
            String modelPath = "src/main/resources/bert_transaction_categorization.onnx";
            TransactionClassifier classifier = new TransactionClassifier(Paths.get(tokenizerDir), modelPath);

            // 7. 执行分类
            Map<Transaction, String> categorizedTransactions = classifier.classifyBatch(transactions);

            // 8. 准备四级目录路径
            File fourthLevelXmlFile = new File(fourthLevelDir, xmlFileName);
            new File(fourthLevelDir).mkdirs(); // 创建四级目录（如果不存在）

            // 9. 写入分类后的 XML
            ClassificationXmlWriter.writeClassifications(
                    outputXmlFile.getAbsolutePath(),
                    fourthLevelXmlFile.getAbsolutePath(),
                    categorizedTransactions
            );
            System.out.println("分类 XML 已保存至: " + fourthLevelXmlFile.getAbsolutePath());


            TransactionDataLoader dataLoader = new TransactionDataLoader();
            dataLoader.loadFromXML(fourthLevelXmlFile.getAbsolutePath());
            Map<String, Transaction_FZ> transactions_FZ = dataLoader.getTransactionData();




            FinanceDataProcessor processor = new FinanceDataProcessor(ledgerId,transactions_FZ);
            processor.process();



            // 10.4 保存到三级目录

            processor.saveToThirdLevel(THIRD_DIR);

            System.out.println("财务数据已保存至：" +
                    Paths.get(FinanceData.THIRD_DIR, ledgerId + ".json"));





        } catch (IOException e) {
            System.err.println("文件操作失败: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("处理失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 可选：关闭分类器资源
            // if (classifier != null) classifier.close();
        }


    }




}