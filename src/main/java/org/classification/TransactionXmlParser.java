package org.classification;

/**
 * A XML Parser used to extract transaction information
 *
 * @version 1.0
 * @date 31 March
 * @author studentruan
 */

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

public class TransactionXmlParser {
    /**
     * Parsing XML document and get the transactions formed by Transaction class
     * @param filePath The path of the parsed XML document
     */
    public static List<Transaction> parse(String filePath) throws Exception {
        List<Transaction> transactions = new ArrayList<>();

        // 1. Dom Parser
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // 2. Parse XML Document
        Document document = builder.parse(new File(filePath));

        // 3. Get all transactions
        NodeList nodes = document.getElementsByTagName("transaction");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            String id = element.getAttribute("id");
            String counterparty = element.getElementsByTagName("counterparty").item(0).getTextContent();
            String product = element.getElementsByTagName("product").item(0).getTextContent();
            transactions.add(new Transaction(id, counterparty, product));
        }

        return transactions;
    }
}
