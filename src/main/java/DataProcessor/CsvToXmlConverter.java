import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class CsvToXmlConverter {

<<<<<<<< HEAD:src/main/java/TransactionAnalyzer/CsvToXmlConverter.java
    public static void main(String[] args) {
        String csvPath = "C:/Users/ASUS/Desktop/CSVXML/monthly_statement_custom_id.csv"; // 替换为你的CSV路径
        String xmlPath = "C:/Users/ASUS/Desktop/CSVXML/transactions.xml";

========
    // 新增的转换入口方法
    public static void convert(String csvPath, String xmlPath) {
>>>>>>>> main:src/main/java/DataProcessor/CsvToXmlConverter.java
        try {
            List<String[]> csvData = loadCSV(csvPath);
            Document xmlDocument = buildXmlDocument(csvData);
            saveXmlToFile(xmlDocument, xmlPath);
            System.out.println("文件已成功生成，路径为: " + xmlPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String[]> loadCSV(String path) throws IOException {
        List<String[]> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path))) {
            String row;
            boolean firstLine = true;
            while ((row = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] items = row.split(",");
                lines.add(items);
            }
        }
        return lines;
    }

    private static Document buildXmlDocument(List<String[]> entries) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element root = document.createElement("transactions");
        document.appendChild(root);

        for (String[] entry : entries) {
            if (entry.length >= 6) {
                Element transaction = document.createElement("transaction");

                Element id = document.createElement("id");
                id.setTextContent(entry[0].trim());
                transaction.appendChild(id);

                String dateStr = entry[1].trim().replace("-", "/").replace(".", "/");
                Element date = document.createElement("date");
                date.setTextContent(dateStr);
                transaction.appendChild(date);

                Element counterparty = document.createElement("counterparty");
                counterparty.setTextContent(entry[2].trim());
                transaction.appendChild(counterparty);

                Element product = document.createElement("product");
                product.setTextContent(entry[3].trim());
                transaction.appendChild(product);

                Element type = document.createElement("type");
                type.setTextContent(entry[4].trim());
                transaction.appendChild(type);

                Element amount = document.createElement("amount");
                amount.setTextContent(entry[5].trim());
                transaction.appendChild(amount);

                root.appendChild(transaction);
            } else {
                System.err.println("数据列不足6项，跳过该行: " + Arrays.toString(entry));
            }
        }
        return document;
    }

    private static void saveXmlToFile(Document doc, String path) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(path));
        transformer.transform(source, result);
    }
}
