package AIUtilities.prediction;

import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ARIMAModel} class.
 * These tests cover basic functionality, including normal forecasting
 * and handling of invalid input data.
 */
public class ARIMAModelTest {

    /**
     * Tests forecasting expenses with valid randomly generated data.
     * This test runs multiple iterations to ensure consistency.
     */
    @Test
    public void testExpenseForecastingWithValidData() {
        int iterations = 10; // Number of test iterations
        int historyLength = 30; // Length of simulated historical data
        int forecastSteps = 20; // Number of forecast days
        Random random = new Random();

        for (int i = 0; i < iterations; i++) {
            // 1. Generate simulated historical expense data
            double[] testData = new double[historyLength];
            for (int j = 0; j < historyLength; j++) {
                testData[j] = 10 + random.nextDouble() * 50; // Expense range [10, 60)
            }

            // 2. Create ARIMA model (assuming seasonal period = 3)
            ARIMAModel model = new ARIMAModel(testData, 3, 4, 5);

            // 3. Perform forecast
            int[] forecasts = model.predict(forecastSteps);

            // 4. Assert forecast results are valid
            assertNotNull(forecasts, "Forecast result should not be null [iteration " + i + "]");
            assertEquals(forecastSteps, forecasts.length, "Forecast length should match steps [iteration " + i + "]");
            for (int val : forecasts) {
                assertTrue(val >= 0, "Predicted value should be non-negative [iteration " + i + "]");
            }
        }
    }

    /**
     * Tests forecasting behavior when given illegal (invalid) input data.
     * This includes empty input data and negative forecast step count.
     */
    @Test
    public void testExpenseForecastingWithIllegalData() {
        double[] emptyData = new double[0];
        ARIMAModel model = new ARIMAModel(emptyData, 3, 4, 5);

        // Expect an exception when attempting to forecast with empty data
        Exception exception1 = assertThrows(IllegalArgumentException.class, model::predict);
        assertTrue(exception1.getMessage().contains("input data"));

        // Expect an exception when steps are negative
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            model.predict(-1);
        });
        assertTrue(exception2.getMessage().contains("Steps"));
    }
}