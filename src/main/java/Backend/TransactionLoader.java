package Backend;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
public class TransactionLoader {

    private Map<String, Transactions> transactionData = new HashMap<>();

    public void loadTransactionsFromXml(String filePath) {
        try {
            File xmlFile = new File(filePath);
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("transaction");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String id = element.getElementsByTagName("id").item(0).getTextContent();
                    LocalDate date = LocalDate.parse(
                            element.getElementsByTagName("date").item(0).getTextContent(), formatter);
                    String counterparty = element.getElementsByTagName("counterparty").item(0).getTextContent();
                    String product = element.getElementsByTagName("product").item(0).getTextContent();
                    String type = element.getElementsByTagName("type").item(0).getTextContent();
                    double amount = Double.parseDouble(
                            element.getElementsByTagName("amount").item(0).getTextContent());

                    // 获取分类（如果有的话）
                    String classification = null;
                    NodeList classNodes = element.getElementsByTagName("class");
                    if (classNodes.getLength() > 0) {
                        classification = classNodes.item(0).getTextContent();
                    }

                    // 创建 Transactions 对象并填充数据
                    Transactions transaction = new Transactions(
                            id, date, counterparty, product, type, amount, classification
                    );

                    transactionData.put(id, transaction);
                }
            }

            System.out.println("Loaded transactions: " + transactionData.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Transactions> getTransactionData() {
        return transactionData;
    }
}
