package DataProcessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.*;
import javax.xml.parsers.*;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CsvToXmlConverterTest {
    private static final String TEST_CSV = "src/test/java/DataProcessor/test_transactions.csv";
    private static final String OUTPUT_XML = "src/test/java/DataProcessor/output_test.xml";

    // Test data expected values
    private static final List<List<String>> EXPECTED_DATA = Arrays.asList(
            Arrays.asList("1", "2025/03/03", "SupplierA", "Office", "Expense", "500.00"),
            Arrays.asList("2", "2025/03/03", "ClientX", "ProjectA", "Income", "2000.00"),
            Arrays.asList("3", "2025/03/04", "SupplierB", "Travel", "Expense", "300.00")
    );

    @BeforeEach
    void setup() throws IOException {
        // Create a test CSV file with abnormal data
        String csvContent = """
            id,date,counterparty,product,type,amount
            1,2025-03-03,SupplierA,Office,Expense,500.00
            2,2025.03.03,ClientX,ProjectA,Income,2000.00
            3,2025/03/04,SupplierB,Travel,Expense,300.00
            invalid_line
            """;
        Files.write(Paths.get(TEST_CSV), csvContent.getBytes());
    }

    @Test
    void testCompleteRecordConversion() throws Exception {
        // Performing the converting
        CsvToXmlConverter.convert(TEST_CSV, OUTPUT_XML);

        // Parse the generated XML
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new File(OUTPUT_XML));
        doc.getDocumentElement().normalize();

        // Verify all records
        NodeList transactions = doc.getElementsByTagName("transaction");
        assertEquals(3, transactions.getLength(), "应转换3条有效记录");

        for (int i = 0; i < transactions.getLength(); i++) {
            Element transaction = (Element) transactions.item(i);
            List<String> expected = EXPECTED_DATA.get(i);

            // Verify each field
            assertFieldEquals(transaction, "id", expected.get(0));
            assertFieldEquals(transaction, "date", expected.get(1));
            assertFieldEquals(transaction, "counterparty", expected.get(2));
            assertFieldEquals(transaction, "product", expected.get(3));
            assertFieldEquals(transaction, "type", expected.get(4));
            assertFieldEquals(transaction, "amount", expected.get(5));
            NodeList children = transaction.getChildNodes();
            for (int j = 0; j < children.getLength(); j++) {
                System.out.println("node types：" + children.item(j).getNodeType()
                        + " name：" + children.item(j).getNodeName());
            }

            // 验证节点数量 减去#text 数量， minus the number of "#text"
            assertEquals(6, transaction.getChildNodes().getLength()-7,
                    "Each transaction should have six sub-elements.");
        }
    }

    private void assertFieldEquals(Element element, String tagName, String expected) {
        String actual = element.getElementsByTagName(tagName)
                .item(0)
                .getTextContent();
        assertEquals(expected, actual,
                tagName + "The field values do not match");
    }

    @Test
    void testDataFormatConversion() throws Exception {
        CsvToXmlConverter.convert(TEST_CSV, OUTPUT_XML);

        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new File(OUTPUT_XML));

        // 验证不同日期格式转换
        Element secondRecord = (Element) doc.getElementsByTagName("transaction").item(1);
        assertEquals("2025/03/03",
                secondRecord.getElementsByTagName("date").item(0).getTextContent(),
                "The date format should be uniformly converted to yyyy/MM/dd");

        // 验证数值格式
        Element thirdRecord = (Element) doc.getElementsByTagName("transaction").item(2);
        String amount = thirdRecord.getElementsByTagName("amount").item(0).getTextContent();
        assertTrue(amount.matches("^\\d+\\.\\d{2}$"),
                "The amount should be retained to two decimal places\n" +
                        "\n");
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_CSV));
        Files.deleteIfExists(Paths.get(OUTPUT_XML));
    }
}
