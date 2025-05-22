package Backend;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class TransactionDataLoader {
    private Map<String, Transaction_FZ> transactionData = new HashMap<>();

    // Load from XML
    public void loadFromXML(String xmlFilePath) {
        try {
            File file = new File(xmlFilePath);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("transaction");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element elem = (Element) nodeList.item(i);
                String id = elem.getElementsByTagName("id").item(0).getTextContent();
                LocalDate date = LocalDate.parse(elem.getElementsByTagName("date").item(0).getTextContent(), formatter);
                String counterparty = elem.getElementsByTagName("counterparty").item(0).getTextContent();
                String product = elem.getElementsByTagName("product").item(0).getTextContent();
                String type = elem.getElementsByTagName("type").item(0).getTextContent();
                double amount = Double.parseDouble(elem.getElementsByTagName("amount").item(0).getTextContent());

                Transaction_FZ transaction = new Transaction_FZ(id, date, counterparty, product, type, amount);
                transactionData.put(id, transaction);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Save to XML
    public void saveToXML(String outputPath) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            Element root = doc.createElement("transactions");
            doc.appendChild(root);

            for (Transaction_FZ t : transactionData.values()) {
                Element transactionElem = doc.createElement("transaction");

                Element id = doc.createElement("id");
                id.appendChild(doc.createTextNode(t.getId()));
                transactionElem.appendChild(id);

                Element date = doc.createElement("date");
                date.appendChild(doc.createTextNode(t.getDate().toString().replace("-", "/")));
                transactionElem.appendChild(date);

                Element counterparty = doc.createElement("counterparty");
                counterparty.appendChild(doc.createTextNode(t.getCounterparty()));
                transactionElem.appendChild(counterparty);

                Element product = doc.createElement("product");
                product.appendChild(doc.createTextNode(t.getProduct()));
                transactionElem.appendChild(product);

                Element type = doc.createElement("type");
                type.appendChild(doc.createTextNode(t.getType()));
                transactionElem.appendChild(type);

                Element amount = doc.createElement("amount");
                amount.appendChild(doc.createTextNode(String.format("%.2f", t.getAmount())));
                transactionElem.appendChild(amount);

                if (t.getClassification() != null) {
                    Element classification = doc.createElement("classification");
                    classification.appendChild(doc.createTextNode(t.getClassification()));
                    transactionElem.appendChild(classification);
                }

                root.appendChild(transactionElem);
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(outputPath));
            transformer.transform(source, result);

            System.out.println("Exported to: " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Transaction_FZ> getTransactionData() {
        return transactionData;
    }
}
