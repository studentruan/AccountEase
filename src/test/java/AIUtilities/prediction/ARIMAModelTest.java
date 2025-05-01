package AIUtilities.prediction;

import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class ARIMAModelTest {

    @Test
    public void testExpenseForecastingWithValidData() {
        int iterations = 10; // 循环测试次数
        int historyLength = 30; // 模拟历史数据长度
        int forecastSteps = 20; // 预测天数
        Random random = new Random();

        for (int i = 0; i < iterations; i++) {
            // 1. 生成模拟历史支出数据
            double[] testData = new double[historyLength];
            for (int j = 0; j < historyLength; j++) {
                testData[j] = 10 + random.nextDouble() * 50; // 支出值范围 [10, 60)
            }

            // 2. 创建 ARIMA 模型（假设周期为 3）
            ARIMAModel model = new ARIMAModel(testData, 3, 4, 5);

            // 3. 执行预测
            int[] forecasts = model.predict(forecastSteps);

            // 4. 断言预测结果合法
            assertNotNull(forecasts, "Forecast result should not be null [iteration " + i + "]");
            assertEquals(forecastSteps, forecasts.length, "Forecast length should match steps [iteration " + i + "]");
            for (int val : forecasts) {
                assertTrue(val >= 0, "Predicted value should be non-negative [iteration " + i + "]");
            }
        }
    }

    @Test
    public void testExpenseForecastingWithIllegalData() {
        double[] emptyData = new double[0];
        ARIMAModel model = new ARIMAModel(emptyData, 3, 4, 5);

        Exception exception1 = assertThrows(IllegalArgumentException.class, model::predict);

        assertTrue(exception1.getMessage().contains("input data"));

        Exception exception2 = assertThrows(IllegalArgumentException.class, ()-> {
            model.predict(-1);
        });

        assertTrue(exception2.getMessage().contains("Steps"));
    }
}
