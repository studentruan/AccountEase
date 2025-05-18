package detectorTools;

import DataProcessor.TransactionAnalyzer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static detectorTools.OutlierDetector.detectAnomalies;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class detectorTest {
    private void verifyAnomalies(Map<String, Double> actual, String[] expectedDates, double[] expectedValues) {
        // 1. 数量验证（网页6的断言基础）
        assertEquals(expectedDates.length, actual.size(), "异常数量不匹配");

        // 2. 键值对验证（网页8数组断言）
        for (int i = 0; i < expectedDates.length; i++) {
            final int index = i; // 显式声明final变量
            assertTrue(actual.containsKey(expectedDates[index]), "缺失日期：" + expectedDates[index]);
            assertEquals(expectedValues[index], actual.get(expectedDates[index]), 0.01,
                    () -> String.format("%s金额偏差超过阈值", expectedDates[index])); // 使用index替代i
        }
    }
    @Test
    void TestDetect() {
        TransactionAnalyzer analyzer = new TransactionAnalyzer("src/test/java/detectorTools/testTransactions.xml");
        Map<String, Double> anomalies = detectAnomalies(analyzer);

        // 预期结果（网页5的测试用例设计原则）
        String[] expectedDates = {"2025-03-21", "2025-03-11", "2025-03-13", "2025-03-16"};
        double[] expectedValues = {2.31, 1.62, 1.30, 1.81};

        verifyAnomalies(anomalies, expectedDates, expectedValues);
    }
    @Test
    void TestDetect_holidy(){
        TransactionAnalyzer analyzer = new TransactionAnalyzer("src/test/java/detectorTools/testTransactions.xml");
        Map<String, Double> anomalies = detectAnomalies(analyzer, List.of("03-13"));

        // 特殊节假日验证（网页4的测试套件思想）
        String[] expectedDates = {"2025-03-21", "2025-03-11", "2025-03-16"};
        double[] expectedValues = {2.31, 1.62, 1.81};

        verifyAnomalies(anomalies, expectedDates, expectedValues);
    }
    @Test
    void TestDetect_overholidy(){
        TransactionAnalyzer analyzer = new TransactionAnalyzer("src/test/java/detectorTools/testTransactions.xml");
        Map<String, Double> anomalies = detectAnomalies(analyzer, List.of("03-21"));

        // 边界值验证（网页6的非法输入测试）
        String[] expectedDates = {"2025-03-21", "2025-03-11", "2025-03-13", "2025-03-16"};
        double[] expectedValues = {2.31, 1.62, 1.30, 1.81};

        verifyAnomalies(anomalies, expectedDates, expectedValues);
    }
}
