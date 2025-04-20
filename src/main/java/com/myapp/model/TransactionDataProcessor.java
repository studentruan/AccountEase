package com.myapp.model;

import com.myapp.model.Transaction;
import com.myapp.model.Ledger;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.time.LocalDate;
import java.util.UUID;

public class TransactionDataProcessor {

    public static void loadTransactionsFromXml(File xmlFile, Ledger ledger) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            NodeList transactionNodes = doc.getElementsByTagName("transaction");

            for (int i = 0; i < transactionNodes.getLength(); i++) {
                Element element = (Element) transactionNodes.item(i);

                String id = element.getAttribute("id");
                if (id == null || id.isEmpty()) {
                    id = UUID.randomUUID().toString(); // 自动生成唯一ID
                }

                LocalDate date = LocalDate.parse(element.getElementsByTagName("date").item(0).getTextContent());
                String category = element.getElementsByTagName("category").item(0).getTextContent();
                double amount = Double.parseDouble(element.getElementsByTagName("amount").item(0).getTextContent());
                String description = element.getElementsByTagName("description").item(0).getTextContent();

                Transaction transaction = new Transaction(id, date, category, amount, description);
                ledger.addTransaction(transaction);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
