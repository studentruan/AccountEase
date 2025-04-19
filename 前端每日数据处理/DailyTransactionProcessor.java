import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

public class DailyTransactionProcessor {
    private static final String FILE_NAME = "account.xml"; // XML文件名

    /**
     * 方法1：根据给定日期获取当天所有交易记录，返回按收入（Income）和支出（Expense）分类的字典列表。
     *
     * @param date 日期，格式为YYYY/MM/DD
     * @return 包含当天所有交易记录的字典列表
     */
    public Map<String, List<Map<String, String>>> getTransactionsByDate(String date) {
        Map<String, List<Map<String, String>>> result = new HashMap<>();
        List<Map<String, String>> incomeList = new ArrayList<>();
        List<Map<String, String>> expenseList = new ArrayList<>();

        try {
            File file = new File(FILE_NAME);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("transaction");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // 检查日期是否匹配
                    if (element.getElementsByTagName("date").item(0).getTextContent().equals(date)) {
                        Map<String, String> transaction = new HashMap<>();
                        transaction.put("id", element.getElementsByTagName("id").item(0).getTextContent());
                        transaction.put("date", element.getElementsByTagName("date").item(0).getTextContent());
                        transaction.put("counterparty", element.getElementsByTagName("counterparty").item(0).getTextContent());
                        transaction.put("product", element.getElementsByTagName("product").item(0).getTextContent());
                        transaction.put("type", element.getElementsByTagName("type").item(0).getTextContent());
                        transaction.put("amount", element.getElementsByTagName("amount").item(0).getTextContent());

                        // 分类存储到收入或支出列表
                        if (transaction.get("type").equals("Income")) {
                            incomeList.add(transaction);
                        } else if (transaction.get("type").equals("Expense")) {
                            expenseList.add(transaction);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        result.put("Income", incomeList);
        result.put("Expense", expenseList);
        return result;
    }

    /**
     * 方法2：计算指定日期的支出总和。
     *
     * @param date 日期
     * @return 支出总和
     */
    public double getTotalExpenses(String date) {
        Map<String, List<Map<String, String>>> transactions = getTransactionsByDate(date);
        List<Map<String, String>> expenseList = transactions.getOrDefault("Expense", new ArrayList<>());
        double total = 0;
        for (Map<String, String> expense : expenseList) {
            total += Double.parseDouble(expense.get("amount"));
        }
        return total;
    }

    /**
     * 方法3：计算指定日期的收入总和。
     *
     * @param date 日期
     * @return 收入总和
     */
    public double getTotalIncome(String date) {
        Map<String, List<Map<String, String>>> transactions = getTransactionsByDate(date);
        List<Map<String, String>> incomeList = transactions.getOrDefault("Income", new ArrayList<>());
        double total = 0;
        for (Map<String, String> income : incomeList) {
            total += Double.parseDouble(income.get("amount"));
        }
        return total;
    }

    /**
     * 方法4：计算指定日期的收入和支出总和。
     *
     * @param date 日期
     * @return 收入和支出总和
     */
    public double getTotalTransactions(String date) {
        return getTotalIncome(date) - getTotalExpenses(date);
    }

    /**
     * 方法5：计算指定日期每种支出的总金额及其占总支出的百分比，并按百分比降序排列。
     *
     * @param date 日期
     * @return 包含支出种类、总金额、百分比的字典
     */
    public Map<Integer, Map<String, String>> getExpenseBreakdown(String date) {
        Map<String, List<Map<String, String>>> transactions = getTransactionsByDate(date);
        List<Map<String, String>> expenseList = transactions.getOrDefault("Expense", new ArrayList<>());
        double totalExpenses = getTotalExpenses(date);

        Map<String, Double> productTotals = new HashMap<>();
        for (Map<String, String> expense : expenseList) {
            String product = expense.get("product");
            double amount = Double.parseDouble(expense.get("amount"));
            productTotals.put(product, productTotals.getOrDefault(product, 0.0) + amount);
        }

        List<Map.Entry<String, Double>> sortedExpenses = new ArrayList<>(productTotals.entrySet());
        sortedExpenses.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

        Map<Integer, Map<String, String>> result = new LinkedHashMap<>();
        DecimalFormat df = new DecimalFormat("#.##");
        int index = 1;
        for (Map.Entry<String, Double> entry : sortedExpenses) {
            Map<String, String> breakdown = new HashMap<>();
            breakdown.put("Product", entry.getKey());
            breakdown.put("Total Amount", df.format(entry.getValue()));
            breakdown.put("Percentage", df.format((entry.getValue() / totalExpenses) * 100) + "%");
            result.put(index++, breakdown);
        }
        return result;
    }

    /**
     * 方法6：计算指定日期每种收入的总金额及其占总收入的百分比，并按百分比降序排列。
     *
     * @param date 日期
     * @return 包含收入种类、总金额、百分比的字典
     */
    public Map<Integer, Map<String, String>> getIncomeBreakdown(String date) {
        Map<String, List<Map<String, String>>> transactions = getTransactionsByDate(date);
        List<Map<String, String>> incomeList = transactions.getOrDefault("Income", new ArrayList<>());
        double totalIncome = getTotalIncome(date);

        Map<String, Double> productTotals = new HashMap<>();
        for (Map<String, String> income : incomeList) {
            String product = income.get("product");
            double amount = Double.parseDouble(income.get("amount"));
            productTotals.put(product, productTotals.getOrDefault(product, 0.0) + amount);
        }

        List<Map.Entry<String, Double>> sortedIncomes = new ArrayList<>(productTotals.entrySet());
        sortedIncomes.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

        Map<Integer, Map<String, String>> result = new LinkedHashMap<>();
        DecimalFormat df = new DecimalFormat("#.##");
        int index = 1;
        for (Map.Entry<String, Double> entry : sortedIncomes) {
            Map<String, String> breakdown = new HashMap<>();
            breakdown.put("Product", entry.getKey());
            breakdown.put("Total Amount", df.format(entry.getValue()));
            breakdown.put("Percentage", df.format((entry.getValue() / totalIncome) * 100) + "%");
            result.put(index++, breakdown);
        }
        return result;
    }

    public static void main(String[] args) {
        DailyTransactionProcessor processor = new DailyTransactionProcessor();

        // 示例调用方法
        String date = "2025/03/03";

        System.out.println("当天交易记录：" + processor.getTransactionsByDate(date));
        System.out.println("当天支出总和：" + processor.getTotalExpenses(date));
        System.out.println("当天收入总和：" + processor.getTotalIncome(date));
        System.out.println("当天收入支出总和：" + processor.getTotalTransactions(date));
        System.out.println("当天支出分布：" + processor.getExpenseBreakdown(date));
        System.out.println("当天收入分布：" + processor.getIncomeBreakdown(date));
    }
}