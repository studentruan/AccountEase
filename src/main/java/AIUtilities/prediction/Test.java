package AIUtilities.prediction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import DataProcessor.DailyTransactionProcessor;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.colors.XChartSeriesColors;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class Test {
    public static void main(String[] args) {

        // 1. 生成测试数据（60天的销售数据，周期为7天）
        double[] testData = generateTestData();
        System.out.println("Generated test data:");
        System.out.println(Arrays.toString(Arrays.copyOf(testData, 20)));

        // 2. 创建模型并预测
        ARIMAModel model = new ARIMAModel(testData, 3, 4, 5);

        // 3. 预测未来5天
        int steps = 20;
        int[] forecasts = model.predict(steps);

        // 4. 输出结果
        System.out.println("未来" + steps + "天的预测结果:");
        for (int i = 0; i < forecasts.length; i++) {
            double forecast = Math.pow(forecasts[i],2); // 逆变换
            forecast = Math.max(0, forecast); // 确保非负（可选）
            System.out.println("第" + (i+1) + "天: " + forecast);
        }

        plotResults(testData, forecasts);
    }

    private static double[] generateTestData() {
        DailyTransactionProcessor processor = new DailyTransactionProcessor();

        int start = 1;
        int end = 31;

        LocalDate startDate = LocalDate.of(2025, 3, start);
        LocalDate endDate = LocalDate.of(2025, 3, end);

        double[] expense_history = new double[end-start+1];
        int index = 0;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dateStr = date.format(DateTimeFormatter.ofPattern("MM/dd"));
            expense_history[index] = Math.sqrt(processor.getTotalExpenses("2025/" + dateStr));
            index++;
        }

        return expense_history;
    }

    private static void plotResults(double[] history, int[] forecasts) {
        // 创建图表
        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("ARIMA预测结果")
                .xAxisTitle("时间")
                .yAxisTitle("数值")
                .theme(Styler.ChartTheme.Matlab)
                .build();

        // 准备历史数据点
        double[] xHistory = new double[history.length];
        double[] yHistory = history;
        for (int i = 0; i < xHistory.length; i++) {
            xHistory[i] = i;
        }

        // 准备预测数据点
        double[] xForecast = new double[forecasts.length];
        double[] yForecast = new double[forecasts.length];
        for (int i = 0; i < forecasts.length; i++) {
            xForecast[i] = history.length + i;
            yForecast[i] = forecasts[i];
        }

        // 添加系列
        chart.addSeries("历史数据", xHistory, yHistory)
                .setMarker(SeriesMarkers.NONE);
        chart.addSeries("预测数据", xForecast, yForecast)
                .setMarker(SeriesMarkers.CIRCLE)
                .setLineColor(XChartSeriesColors.RED);

        // 显示图表
        new SwingWrapper<>(chart).displayChart();
    }
}