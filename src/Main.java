import java.math.BigDecimal;
import java.math.RoundingMode;

public class Main {
    public static void main(String[] args) {
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
