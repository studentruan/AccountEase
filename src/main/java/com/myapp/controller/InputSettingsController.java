package com.myapp.controller;

import AIUtilities.classification.ClassificationXmlWriter;
import AIUtilities.classification.Transaction;
import AIUtilities.classification.TransactionClassifier;
import AIUtilities.classification.TransactionXmlParser;
import Backend.*;
import com.myapp.util.I18nUtil;
import DataProcessor.CsvToXmlConverter;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static Backend.FinanceData.THIRD_DIR;

/**
        * Controller for handling file input settings and processing financial transactions.
        * <p>
 * This controller manages the file selection, conversion, classification, and processing
 * of financial transaction data through a series of steps:
        * <ol>
 *   <li>File selection via file chooser</li>
        *   <li>CSV to XML conversion</li>
        *   <li>Transaction classification using AI</li>
        *   <li>Data processing and storage</li>
        * </ol>
        * </p>
 *
 * @author JiaYi Du and Shang Shi
 * @version 2.0
 * @since March 17, 2023
        */
public class InputSettingsController {

    /** Text field for displaying selected file path */
    @FXML
    private TextField filePathField;

    /** Directory path for third-level JSON output */
    private static final String THIRD_DIR = "src/main/resources/thirdlevel_json/";

    /**
            * Handles file browsing action.
            * <p>
     * Opens a file chooser dialog to select transaction files (CSV, Excel, or XML).
            * Sets the selected file path in the filePathField.
            * </p>
            */
    @FXML
    private void handleBrowseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(I18nUtil.get("file.chooser.title"));

        // Set file filters
        FileChooser.ExtensionFilter excelFilter = new FileChooser.ExtensionFilter(
                I18nUtil.get("file.filter.spreadsheet"),
                "*.xlsx", "*.csv", "*.xml");
        fileChooser.getExtensionFilters().add(excelFilter);

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    /**
            * Processes and saves the selected file.
            * <p>
     * Performs the complete transaction processing pipeline:
            * <ol>
     *   <li>Validates file path and ledger ID</li>
            *   <li>Copies file to transaction records directory</li>
            *   <li>Converts CSV to XML format</li>
            *   <li>Classifies transactions using AI</li>
            *   <li>Merges and processes transaction data</li>
            *   <li>Saves final financial data</li>
            * </ol>
            * </p>
            */
    @FXML
    private void saveFilePath() {
        String filePath = filePathField.getText();
        if (filePath == null || filePath.isEmpty()) {
            System.err.println(I18nUtil.get("error.file.path.empty"));
            return;
        }

        File selectedFile = new File(filePath);
        if (!selectedFile.exists() || !selectedFile.isFile()) {
            System.err.println(I18nUtil.get("error.file.invalid"));
            return;
        }
        String ledgerId = GlobalContext.getInstance().getCurrentLedgerId();

        // Validate ID
        if (ledgerId == null || ledgerId.trim().isEmpty()) {
            throw new IllegalStateException(I18nUtil.get("error.ledger.id.missing"));
        }

        // Define directory paths
        String inputDir = "src/main/resources/Transactions_Record_CSV/"+ledgerId+"/";
        String outputDir = "src/main/resources/Transactions_Record_XML/"+ledgerId+"/";
        String fourthLevelDir = "src/main/resources/fourthlevel_xml/"+ledgerId+"/";

        try {
            // 1. Ensure input directory exists
            File inputDirectory = new File(inputDir);
            if (!inputDirectory.exists() && !inputDirectory.mkdirs()) {
                System.err.println(I18nUtil.get("error.dir.create.failed") + ": " + inputDir);
                return;
            }

            // 2. Copy file to input directory
            File targetCsvFile = new File(inputDir, selectedFile.getName());
            Files.copy(
                    selectedFile.toPath(),
                    targetCsvFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );

            // 3. Prepare output path (same name with XML extension)
            String xmlFileName = selectedFile.getName().replace(".csv", ".xml");
            File outputXmlFile = new File(outputDir, xmlFileName);

            // Ensure output directory exists
            File outputDirectory = new File(outputDir);
            if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
                System.err.println(I18nUtil.get("error.dir.create.failed") + ": " + outputDir);
                return;
            }

            // 4. Convert CSV to XML
            CsvToXmlConverter.convert(targetCsvFile.getAbsolutePath(), outputXmlFile.getAbsolutePath());
            System.out.println(I18nUtil.get("message.csv.to.xml.success") + ": " + outputXmlFile.getAbsolutePath());

            // 5. Parse XML file to get transactions
            List<Transaction> transactions = TransactionXmlParser.parse(outputXmlFile.getAbsolutePath());

            // 6. Initialize classifier
            Path tokenizerDir = Paths.get("src/main/resources/Tokenizer");
            String modelPath = "src/main/resources/bert_transaction_categorization.onnx";
            Path descriptionPath = Paths.get("src/main/resources/counterparty_description.json");
            TransactionClassifier classifier = new TransactionClassifier(tokenizerDir, modelPath, descriptionPath);

            // 7. Perform classification
            Map<Transaction, Map<String,String>> categorized = classifier.classifyBatch(transactions);

            // 8. Prepare fourth level directory path
            File fourthLevelXmlFile = new File(fourthLevelDir, xmlFileName);
            new File(fourthLevelDir).mkdirs();

            // 9. Write classified XML
            ClassificationXmlWriter.writeClassifications(
                    outputXmlFile.getAbsolutePath(),
                    fourthLevelXmlFile.getAbsolutePath(),
                    categorized
            );
            System.out.println(I18nUtil.get("message.classified.xml.saved") + ": " + fourthLevelXmlFile.getAbsolutePath());

            String Finalfile = mergeTransactionFiles();
            TransactionDataLoader dataLoader = new TransactionDataLoader();
            dataLoader.loadFromXML(Finalfile);
            Map<String, Transaction_FZ> transactions_FZ = dataLoader.getTransactionData();

            FinanceDataProcessor processor = new FinanceDataProcessor(ledgerId,transactions_FZ);
            processor.process();
            processor.saveToThirdLevel(THIRD_DIR);

            System.out.println(I18nUtil.get("message.finance.data.saved") + ": " +
                    Paths.get(FinanceData.THIRD_DIR, ledgerId + ".json"));

        } catch (IOException e) {
            System.err.println(I18nUtil.get("error.file.operation.failed") + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(I18nUtil.get("error.processing.failed") + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
            * Merges individual transaction XML files into a single file.
     *
             * @return path to the merged XML file
     * @throws Exception if merging fails
     */
    private String mergeTransactionFiles() throws Exception {
        GlobalContext globalContext = GlobalContext.getInstance();
        String ledgerId = globalContext.getCurrentLedgerId();
        String inputDir = "src/main/resources/fourthlevel_xml/" + ledgerId + "/";
        String outputFile = inputDir + "merged_transactions.xml";
        String FF = TransactionXmlMerger.mergeTransactionXmlFiles(inputDir, outputFile);
        if (FF != null) {
            cleanUpIndividualFiles(inputDir);
        }
        return outputFile;
    }

    /**
            * Cleans up individual transaction files after merging.
     *
             * @param dirPath directory containing files to clean up
     */
    private void cleanUpIndividualFiles(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles((d, name) ->
                name.endsWith(".xml") && !name.equals("merged_transactions.xml")
        );

        if (files != null) {
            for (File file : files) {
                if (!file.delete()) {
                    System.err.println(I18nUtil.get("error.file.delete.failed") + ": " + file.getAbsolutePath());
                }
            }
        }
    }
}