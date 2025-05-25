package DataProcessor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.io.*;
import java.nio.file.*;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionAnalyzerTest {
    private static final String TEST_XML = "src/test/java/DataProcessor/transactions.xml";

    @BeforeEach
    void setup() throws Exception {
        String xmlContent = """
            <transactions>
                <transaction>
                    <date>2024/03/03</date>
                    <type>Income</type>
                    <amount>1800</amount>
                </transaction>
                <transaction>
                    <date>2024/03/03</date>
                    <type>Expense</type>
                    <amount>600</amount>
                </transaction>
                <transaction>
                    <date>2024/03/04</date>
                    <type>Income</type>
                    <amount>1000</amount>
                </transaction>
                <transaction>
                    <date>2024/03/05</date>
                    <type>Expense</type>
                    <amount>300</amount>
                </transaction>
                <transaction>
                    <date>2025/03/03</date>
                    <type>Income</type>
                    <amount>2000</amount>
                </transaction>
                <transaction>
                    <date>2025/03/03</date>
                    <type>Expense</type>
                    <amount>500</amount>
                </transaction>
                <transaction>
                    <date>2025/03/04</date>
                    <type>Income</type>
                    <amount>1500</amount>
                </transaction>
                <transaction>
                    <date>2025/03/05</date>
                    <type>Expense</type>
                    <amount>100</amount>
                </transaction>
                <transaction>
                    <date>2025/04/03</date>
                    <type>Expense</type>
                    <amount>500</amount>
                </transaction>
                <transaction>
                    <date>2025/04/04</date>
                    <type>Income</type>
                    <amount>1500</amount>
                </transaction>
                <transaction>
                    <date>2025/04/05</date>
                    <type>Expense</type>
                    <amount>200</amount>
                </transaction>
            </transactions>
            """;
        Files.write(Paths.get(TEST_XML), xmlContent.getBytes());
    }
    // Test 03/03 Net income and expenditure 2000-500=1500
    @Test
    void testDailySummary() {
        var analyzer = new TransactionAnalyzer(TEST_XML);
        var dailySummary = analyzer.getDailySummary();

        LocalDate date1 = LocalDate.parse("2025-03-03");
        assertEquals(new BigDecimal("1500"), dailySummary.get(date1));
    }
    //Test the total income of a certain day
    @Test
    void testDailyIncome() {
        var analyzer = new TransactionAnalyzer(TEST_XML);
        var dailyIncome = analyzer.getIncomeDailySummary();

        LocalDate date1 = LocalDate.parse("2025-03-03");
        assertEquals(new BigDecimal("2000"), dailyIncome.get(date1));
    }
    //Test the total expenditure of a certain day
    @Test
    void testExpenseDailySummary() {
        var analyzer = new TransactionAnalyzer(TEST_XML);
        var dailyExpense = analyzer.getExpenseDailySummary();

        LocalDate date1 = LocalDate.parse("2025-03-03");
        assertEquals(new BigDecimal("500"), dailyExpense.get(date1));
    }
    // Test the total expenditure of a certain month
    @Test
    void testMonthlyBreakdown() {
        var analyzer = new TransactionAnalyzer(TEST_XML);
        var monthlyExpense = analyzer.getMonthlyExpenseSummary();

        assertEquals(new BigDecimal("600"), monthlyExpense.get("2025-03"));
        assertEquals(new BigDecimal("700"), monthlyExpense.get("2025-04"));
    }
    // Test the total income of a certain month
    @Test
    void testMonthlyIncomeSummary() {
        var analyzer = new TransactionAnalyzer(TEST_XML);
        var monthlyIncome = analyzer.getMonthlyIncomeSummary();

        assertEquals(new BigDecimal("3500"), monthlyIncome.get("2025-03"));
        assertEquals(new BigDecimal("1500"), monthlyIncome.get("2025-04"));
    }

    // Test the net income of a certain month
    @Test
    void testMonthlySummary() {
        var analyzer = new TransactionAnalyzer(TEST_XML);
        var monthlySummary = analyzer.getMonthlySummary();

        assertEquals(new BigDecimal("2900"), monthlySummary.get("2025-03"));
        assertEquals(new BigDecimal("800"), monthlySummary.get("2025-04"));
    }
    // Test the total income for one year
    @Test
    void testYearlyAnalysis() {
        var analyzer = new TransactionAnalyzer(TEST_XML);
        var yearlyIncome = analyzer.getYearlyIncomeSummary();

        assertEquals(new BigDecimal("5000"), yearlyIncome.get(2025));
        assertEquals(new BigDecimal("2800"), yearlyIncome.get(2024));
    }
    // Test the total expenditure for one year
    @Test
    void testYearlyExpense(){
        var analyzer = new TransactionAnalyzer(TEST_XML);
        var yearlyTotalExpense = analyzer.getYearlyExpenseSummary();

        assertEquals(new BigDecimal("1300"), yearlyTotalExpense.get(2025));
        assertEquals(new BigDecimal("900"), yearlyTotalExpense.get(2024));
    }

    // Test the total net income for one yea
    @Test
    void testYearlySummary(){
        var analyzer = new TransactionAnalyzer(TEST_XML);
        var yearlySummary = analyzer.getYearlySummary();

        assertEquals(new BigDecimal("3700"), yearlySummary.get(2025));
        assertEquals(new BigDecimal("1900"), yearlySummary.get(2024));
    }
    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_XML));
    }
}
