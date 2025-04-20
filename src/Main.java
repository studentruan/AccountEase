import java.math.BigDecimal;
import java.math.RoundingMode;

public class Main {
    public static void main(String[] args) {

        // 示例 CSV 文件路径（你可以手动设置或通过 args 传入）
        String csvPath = "C:/Users/ASUS/Desktop/CSVXML/detailed_monthly_statement.csv"; // 修改为你自己的 CSV 路径
        String xmlPath = "C:/Users/ASUS/Desktop/CSVXML/transactions1.xml"; // 输出的 XML 路径

        // 调用转换器
        CsvToXmlConverter.convert(csvPath, xmlPath);

        TransactionAnalyzer analyzer = new TransactionAnalyzer("C:/Users/ASUS/Desktop/CSVXML/transactions.xml");
        //每日净收支
        analyzer.getDailySummary().forEach((date, amount) ->
                System.out.println(date + ": " + amount));

        //每月净收支
        analyzer.getMonthlySummary().forEach((month, amount) ->
                System.out.println(month + ": " + amount));

        //每年净收支
        analyzer.getYearlySummary().forEach((year, amount) ->
                System.out.println(year + ": " + amount));
    }
}