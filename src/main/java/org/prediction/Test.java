package org.prediction;

import java.io.IOException;
import java.util.Arrays;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.colors.XChartSeriesColors;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class Test {
    public static void main(String[] args) {

        // 1. 生成测试数据（60天的销售数据，周期为7天）
        double[] testData = generateTestData(30, 7);
        System.out.println("Generated test data:");
        System.out.println(Arrays.toString(Arrays.copyOf(testData, 20)));

        // 2. 创建模型并预测
        ARIMAModel model = new ARIMAModel(testData, 7, 4, 5);

        // 3. 预测未来5天
        int steps = 20;
        int[] forecasts = model.predict(steps);

        // 4. 输出结果
        System.out.println("未来" + steps + "天的预测结果:");
        for (int i = 0; i < forecasts.length; i++) {
            System.out.println("第" + (i+1) + "天: " + forecasts[i]);
        }

        plotResults(testData, forecasts);
    }

    private static double[] generateTestData(int days, int period) {
        double[] data = new double[days];
        double baseValue = 100; // 基础值
        double noiseAmplitude = 15; // 噪声幅度

        // 生成周期性数据（每周模式）
        double[] weeklyPattern = {0.8, 1.2, 1.5, 1.3, 1.6, 2.0, 0.6};

        // 趋势分量（每天增加0.2）
        double trend = 0.2;

        // 生成数据
        for (int i = 0; i < days; i++) {
            int dayOfWeek = i % period;
            double noise = (Math.random() - 0.5) * noiseAmplitude;
            data[i] = baseValue + i * trend + weeklyPattern[dayOfWeek] * 30 + noise;
        }

        return data;
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