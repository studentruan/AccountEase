package AIUtilities;

import AIUtilities.classification.ClassificationXmlWriter;
import AIUtilities.classification.Transaction;
import AIUtilities.classification.TransactionClassifier;
import AIUtilities.classification.TransactionXmlParser;

import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import AIUtilities.prediction.ARIMAModel;
import DataProcessor.DailyTransactionProcessor;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.colors.XChartSeriesColors;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.Map;

/**
 * Just a class to debug and visualize the result of classification and prediction.
 * Just for my own testing(This will not replace the normal JUnit test).
 */
public class AIendTest {
    public static void main(String[] args) {
        try {
            // 1. 初始化分类器
            Path tokenizerDir = Paths.get("src/main/resources/Tokenizer");
            String modelPath = "src/main/resources/bert_transaction_categorization.onnx";
            Path descriptionPath = Paths.get("src/main/resources/counterparty_description.json");
            TransactionClassifier classifier = new TransactionClassifier(tokenizerDir, modelPath, descriptionPath);

            // 2. 解析 XML 文件
            String xmlFilePath = "src/main/resources/transactions.xml";
            List<Transaction> transactions = TransactionXmlParser.parse(xmlFilePath);

            // 3.1 批量分类
            Map<Transaction, Map<String,String>> categorized = classifier.classifyBatch(transactions);
            // 4. 将分类结果写回XML文件
            ClassificationXmlWriter.writeClassifications(xmlFilePath, "src/main/output/classified_transactions.xml" ,categorized);
            // 3.2 单个分类（比如手动输入数据）
//            String transaction = "";
//            String category = classifier.classify(transaction);

            // 4. 打印结果
            categorized.forEach((tx, cate) -> {
                System.out.println("--- Transaction ---");
                System.out.println(tx);

                String enrichedText = cate.keySet().iterator().next();   // enriched text
                String predictedCategory = cate.get(enrichedText);       // category

                System.out.println("Input text: " + enrichedText);
                System.out.println("Predicted Category: " + predictedCategory + "\n");
            });

            // 6. 关闭分类器
            classifier.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
            int forecast = forecasts[i];
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
            expense_history[index] = processor.getTotalExpenses("2025/" + dateStr);
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

