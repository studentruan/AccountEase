package detectorTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatisticalThresholdCalculatorTest {
    private StatisticalThresholdCalculator calculator;

    @BeforeEach
    void setup() {
        List<Double> amounts = List.of(10.0, 20.0, 30.0, 40.0, 50.0);
        calculator = new StatisticalThresholdCalculator(amounts);
    }

    @Test
    void should_calculate_correct_threshold() {
        // Q1=20, Q3=40, IQR=20 → 40 + 1.5 * 20 = 70
        assertEquals(70.0, calculator.calculateDynamicThreshold(), 0.001);
    }

    @Test
    void should_handle_edge_cases() {
        // 测试全相同值的情况
        List<Double> edgeData = Collections.nCopies(5, 100.0);
        StatisticalThresholdCalculator edgeCalc = new StatisticalThresholdCalculator(edgeData);
        assertEquals(100.0, edgeCalc.calculateDynamicThreshold());
    }
}