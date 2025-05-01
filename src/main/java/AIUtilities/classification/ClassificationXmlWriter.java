package AIUtilities.classification;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Map;

/**
 * Utility class for writing classification results to an XML file.
 *
 * This class reads an original XML file containing transaction records,
 * updates each <transaction> element with a predicted <class> tag based on the
 * classification results, and writes the modified content to a new output XML file.
 *  @version 1.0
 *  @date 10 April
 *  @author studentruan
 */
public class ClassificationXmlWriter {

    /**
     * Writes classification results into a new XML file by updating or appending
     * a <class> element for each transaction in the original XML file.
     *
     * @param inputXmlFilePath        Path to the original transaction XML file
     * @param outputXmlFilePath       Path to the output XML file with classification results
     * @param categorizedTransactions Map of transactions to their classification results;
     *                                the key is a Transaction object, and the value is a map
     *                                with enriched text and its predicted category
     * @throws Exception if there is any issue during XML parsing or writing
     */
    public static void writeClassifications(
            String inputXmlFilePath,
            String outputXmlFilePath,
            Map<Transaction, Map<String, String>> categorizedTransactions
    ) throws Exception {
        // Parse the input XML file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(inputXmlFilePath));

        NodeList transactionNodes = doc.getElementsByTagName("transaction");

        // Iterate over all <transaction> elements and apply classification results
        for (int i = 0; i < transactionNodes.getLength(); i++) {
            Element transactionElement = (Element) transactionNodes.item(i);

            // Find the matching transaction object by ID
            Transaction tx = findTransaction(transactionElement, categorizedTransactions);
            if (tx != null) {
                Map<String, String> categoryMap = categorizedTransactions.get(tx);

                // Assume there is only one entry: <enriched_text, category>
                Map.Entry<String, String> entry = categoryMap.entrySet().iterator().next();
                String category = entry.getValue();

                // Update existing <class> tag or append a new one
                NodeList classNodes = transactionElement.getElementsByTagName("class");
                if (classNodes.getLength() > 0) {
                    classNodes.item(0).setTextContent(category);
                } else {
                    Element classElement = doc.createElement("class");
                    classElement.setTextContent(category);
                    transactionElement.appendChild(classElement);
                }
            }
        }

        // Write to the new output XML file (do not overwrite original)
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");  // Enable pretty-print
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(outputXmlFilePath));
        transformer.transform(source, result);
    }

    /**
     * Finds the corresponding Transaction object for the given XML element
     * by comparing transaction IDs.
     *
     * @param transactionElement      The <transaction> element from the XML
     * @param categorizedTransactions Map of classified Transaction objects
     * @return The matching Transaction object, or null if not found
     */
    private static Transaction findTransaction(Element transactionElement, Map<Transaction, Map<String, String>> categorizedTransactions) {
        String id = transactionElement.getElementsByTagName("id").item(0).getTextContent();

        for (Transaction tx : categorizedTransactions.keySet()) {
            if (tx.getId().equals(id)) {
                return tx;
            }
        }
        return null;
    }
}