package org.classification;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Map;

public class ClassificationXmlWriter {
    public static void writeClassifications(
            String inputXmlFilePath,
            String outputXmlFilePath,
            Map<Transaction, String> categorizedTransactions
    ) throws Exception {
        // 解析输入 XML 文件
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(inputXmlFilePath));

        NodeList transactionNodes = doc.getElementsByTagName("transaction");

        // 遍历并更新分类
        for (int i = 0; i < transactionNodes.getLength(); i++) {
            Element transactionElement = (Element) transactionNodes.item(i);

            // 查找匹配的交易
            Transaction tx = findTransaction(transactionElement, categorizedTransactions);
            if (tx != null) {
                String category = categorizedTransactions.get(tx);
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

        // 4. 写入新的 XML 文件（而不是覆盖原文件）
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");  // 格式化输出
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(outputXmlFilePath));  // 写入新文件
        transformer.transform(source, result);
    }

    private static Transaction findTransaction(Element transactionElement, Map<Transaction, String> categorizedTransactions) {
        String id = transactionElement.getElementsByTagName("id").item(0).getTextContent();

        for (Transaction tx : categorizedTransactions.keySet()) {
            if (tx.getId().equals(id)) {
                return tx;
            }
        }
        return null;
    }
}