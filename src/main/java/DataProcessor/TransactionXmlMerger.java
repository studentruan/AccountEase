package DataProcessor;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * Utility class to merge all transaction XML files from a specified folder
 * into a single custom output XML file.
 */
public class TransactionXmlMerger {



    /**
     * Merges all
     * @param inputFolderPath The folder containing XML files to merge.
     * @param outputFilePath  The full path of the output XML file, e.g., "D:/merged/merged_transactions.xml".
     * @return The output file path if success, or null if failed.
     */
    public static String mergeTransactionXmlFiles(String inputFolderPath, String outputFilePath) {
        try {
            File folder = new File(inputFolderPath);
            if (!folder.isDirectory()) {
                throw new IllegalArgumentException("Provided input path is not a folder: " + inputFolderPath);
            }

            // Prepare the output document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document mergedDoc = builder.newDocument();
            Element rootElement = mergedDoc.createElement("transactions");
            mergedDoc.appendChild(rootElement);

            // Read all .xml files from the folder
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));
            if (files == null || files.length == 0) {
                throw new RuntimeException("No .xml files found in the input folder.");
            }

            // Import <transaction> nodes
            for (File file : files) {
                Document doc = builder.parse(file);
                NodeList transactionNodes = doc.getElementsByTagName("transaction");

                for (int i = 0; i < transactionNodes.getLength(); i++) {
                    Node importedNode = mergedDoc.importNode(transactionNodes.item(i), true);
                    rootElement.appendChild(importedNode);
                }
            }

            // Create parent directory if it doesn't exist
            File outputFile = new File(outputFilePath);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // Write the merged document to the output file
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(mergedDoc);
            StreamResult result = new StreamResult(outputFile);
            transformer.transform(source, result);

            return outputFilePath;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
