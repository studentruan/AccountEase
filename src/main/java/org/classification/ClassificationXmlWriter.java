package org.classification;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Map;

public class ClassificationXmlWriter {
    public static void writeClassifications(String xmlFilePath, Map<Transaction, String> categorizedTransactions) throws Exception {
        // Parse the XML file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(xmlFilePath));

        // Get all transaction elements
        NodeList transactionNodes = doc.getElementsByTagName("transaction");

        // Iterate through transactions and add classification
        for (int i = 0; i < transactionNodes.getLength(); i++) {
            Element transactionElement = (Element) transactionNodes.item(i);

            // Find matching transaction in the map
            Transaction tx = findTransaction(transactionElement, categorizedTransactions);
            if (tx != null) {
                String category = categorizedTransactions.get(tx);

                // Create or update classification element
                NodeList classNodes = transactionElement.getElementsByTagName("class");
                if (classNodes.getLength() > 0) {
                    // Update existing class node
                    classNodes.item(0).setTextContent(category);
                } else {
                    // Add new class node
                    Element classElement = doc.createElement("class");
                    classElement.setTextContent(category);
                    transactionElement.appendChild(classElement);
                }
            }
        }

        // Write the modified document back to file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(xmlFilePath));
        transformer.transform(source, result);
    }

    private static Transaction findTransaction(Element transactionElement, Map<Transaction, String> categorizedTransactions) {
        String id = transactionElement.getAttribute("id");
        String description = transactionElement.getElementsByTagName("description").item(0).getTextContent();
        String amount = transactionElement.getElementsByTagName("amount").item(0).getTextContent();

        for (Transaction tx : categorizedTransactions.keySet()) {
            if (tx.getId().equals(id)) {
                return tx;
            }
        }
        return null;
    }
}