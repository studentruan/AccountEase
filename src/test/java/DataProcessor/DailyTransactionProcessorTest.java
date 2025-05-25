package DataProcessor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DailyTransactionProcessorTest {
    private DailyTransactionProcessor processor;
    private static final String TEST_XML_DIR = "src/test/java/DataProcessor/test_xml/";

    @BeforeEach
    void setup() throws IOException {
        // Create an XML file containing multiple test scenarios
        Files.createDirectories(Paths.get(TEST_XML_DIR));
        String xmlContent = """
            <transactions>
                <!-- 正常交易记录 -->
                <transaction>
                    <id>1</id>
                    <date>2025/03/03</date>
                    <counterparty>SupplierA</counterparty>
                    <product>Office Supplies</product>
                    <type>Expense</type>
                    <amount>500.50</amount>
                </transaction>
                <transaction>
                    <id>2</id>
                    <date>2025/03/03</date>
                    <counterparty>ClientX</counterparty>
                    <product>Web Development</product>
                    <type>Income</type>
                    <amount>2000.00</amount>
                </transaction>
                
                <!-- 同一天不同产品 -->
                <transaction>
                    <id>3</id>
                    <date>2025/03/03</date>
                    <counterparty>SupplierB</counterparty>
                    <product>Business Travel</product>
                    <type>Expense</type>
                    <amount>1200.00</amount>
                </transaction>
                
                <!-- 不同日期的交易 -->
                <transaction>
                    <id>4</id>
                    <date>2025/03/04</date>
                    <counterparty>ClientY</counterparty>
                    <product>Consulting</product>
                    <type>Income</type>
                    <amount>3500.75</amount>
                </transaction>
                
                <!-- 边界值测试 -->
                <transaction>
                    <id>5</id>
                    <date>2025/03/04</date>
                    <counterparty>SupplierC</counterparty>
                    <product>Software License</product>
                    <type>Expense</type>
                    <amount>0.99</amount> <!-- 极小金额 -->
                </transaction>
                <transaction>
                    <id>6</id>
                    <date>2025/03/05</date>
                    <counterparty>ClientZ</counterparty>
                    <product>Enterprise Solution</product>
                    <type>Income</type>
                    <amount>999999.99</amount> <!-- 极大金额 -->
                </transaction>
                
                <!-- 相同产品多次交易 -->
                <transaction>
                    <id>7</id>
                    <date>2025/03/03</date>
                    <counterparty>SupplierA</counterparty>
                    <product>Office Supplies</product>
                    <type>Expense</type>
                    <amount>300.25</amount>
                </transaction>
            </transactions>
            """;
        Files.write(Paths.get(TEST_XML_DIR + "test_data.xml"), xmlContent.getBytes());

        processor = new DailyTransactionProcessor();
        try {
            var field = DailyTransactionProcessor.class.getDeclaredField("outputDir");
            field.setAccessible(true);
            field.set(processor, TEST_XML_DIR);
        } catch (Exception e) {
            fail("反射设置字段失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Get transactions by date - normal scenario")
    void testGetTransactionsByDate() {
        Map<String, List<Map<String, String>>> result = processor.getTransactionsByDate("2025/03/03");

        // 验证记录数量
        assertEquals(3, result.get("Expense").size());
        assertEquals(1, result.get("Income").size());

        // 验证具体字段
        assertEquals("Web Development", result.get("Income").get(0).get("product"));
        assertEquals("2000.00", result.get("Income").get(0).get("amount"));
    }

    @Test
    @DisplayName("Amount calculation - Summary of multiple transactions")
    void testAmountCalculations() {
        // 2025/03/03 支出总和：500.50 + 1200.00 + 300.25 = 2000.75
        assertEquals(2000.75, processor.getTotalExpenses("2025/03/03"), 0.001);

        // 2025/03/03 收入总和：2000.00
        assertEquals(2000.00, processor.getTotalIncome("2025/03/03"), 0.001);

        // 净收支：2000.00 - 2000.75 = -0.75
        assertEquals(-0.75, processor.getTotalTransactions("2025/03/03"), 0.001);
    }

    @Test
    @DisplayName("Expenditure classification statistics - Multiple product scenarios")
    void testExpenseBreakdown() {
        Map<Integer, Map<String, String>> breakdown = processor.getExpenseBreakdown("2025/03/03");

        // 验证分类数量
        assertEquals(2, breakdown.size());

        // 验证金额最大的分类排在第一位
        assertEquals("Business Travel", breakdown.get(1).get("Product"));
        assertEquals("1200", breakdown.get(1).get("Total Amount"));
        assertEquals("59.98%", breakdown.get(1).get("Percentage")); // 1200/2000.75 ≈ 60%

        // 验证相同产品的合并
        assertEquals("Office Supplies", breakdown.get(2).get("Product"));
        assertEquals("800.75", breakdown.get(2).get("Total Amount")); // 500.50 + 300.25
        assertEquals("40.02%", breakdown.get(2).get("Percentage")); // 800.75/2000.75 ≈ 40%
    }

    @Test
    @DisplayName("Boundary value test - Extremely small/extremely large amount")
    void testBoundaryValues() {
        // Very small amount
        assertEquals(0.99, processor.getTotalExpenses("2025/03/04"), 0.001);

        // huge amount
        assertEquals(999999.99, processor.getTotalIncome("2025/03/05"), 0.001);
    }

    @Test
    @DisplayName("Abnormal scene - Non-existent date")
    void testNonExistentDate() {
        Map<String, List<Map<String, String>>> result = processor.getTransactionsByDate("2999/01/01");

        assertTrue(result.get("Income").isEmpty());
        assertTrue(result.get("Expense").isEmpty());
        assertEquals(0.0, processor.getTotalIncome("2999/01/01"));
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_XML_DIR + "test_data.xml"));
        Files.deleteIfExists(Paths.get(TEST_XML_DIR));
    }
}
