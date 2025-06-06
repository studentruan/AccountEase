package DataProcessor;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
//Designed By Zhou Fang
public class TransactionAnalyzer {

    public List<Transaction> transactions = new ArrayList<>();

    public TransactionAnalyzer(String xmlPath) {
        loadTransactions(xmlPath);
    }

    private void loadTransactions(String filePath) {
        try {
            File file = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("transaction");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;
                    String type = elem.getElementsByTagName("type").item(0).getTextContent();
                    BigDecimal amount = new BigDecimal(elem.getElementsByTagName("amount").item(0).getTextContent());
                    LocalDate date = LocalDate.parse(elem.getElementsByTagName("date").item(0).getTextContent(), formatter);
                    if (type.equalsIgnoreCase("Expense")) {
                        amount = amount.negate(); // 支出为负
                    }
                    transactions.add(new Transaction(date, amount));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
       //
    public Map<LocalDate, BigDecimal> getDailySummary() {
        return transactions.stream()
                .collect(Collectors.groupingBy(t -> t.date,
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, t -> t.amount, BigDecimal::add)));
    }
      //每月净收支
    public Map<String, BigDecimal> getMonthlySummary() {
        return transactions.stream()
                .collect(Collectors.groupingBy(t -> t.date.getYear() + "-" + String.format("%02d", t.date.getMonthValue()),
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, t -> t.amount, BigDecimal::add)));
    }
       //每年净收支
    public Map<Integer, BigDecimal> getYearlySummary() {
        return transactions.stream()
                .collect(Collectors.groupingBy(t -> t.date.getYear(),
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, t -> t.amount, BigDecimal::add)));
    }
        //每日支出
    public Map<LocalDate, BigDecimal> getExpenseDailySummary() {
        return transactions.stream()
                .filter(t -> t.amount.compareTo(BigDecimal.ZERO) < 0)
                .collect(Collectors.groupingBy(t -> t.date,
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO,
                                t -> t.amount.abs(),
                                BigDecimal::add)));
    }

        // 每日收入
    public Map<LocalDate, BigDecimal> getIncomeDailySummary() {
        return transactions.stream()
                .filter(t -> t.amount.compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.groupingBy(t -> t.date,
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO,
                                t -> t.amount,
                                BigDecimal::add)));
    }

    // 每月收入
    public Map<String, BigDecimal> getMonthlyIncomeSummary() {
        return transactions.stream()
                .filter(t -> t.amount.compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.groupingBy(t -> t.date.getYear() + "-" + String.format("%02d", t.date.getMonthValue()),
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO,
                                t -> t.amount,
                                BigDecimal::add)));
    }

    // 每月支出
    public Map<String, BigDecimal> getMonthlyExpenseSummary() {
        return transactions.stream()
                .filter(t -> t.amount.compareTo(BigDecimal.ZERO) < 0)
                .collect(Collectors.groupingBy(t -> t.date.getYear() + "-" + String.format("%02d", t.date.getMonthValue()),
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO,
                                t -> t.amount.abs(),
                                BigDecimal::add)));
    }

    // 每年收入
    public Map<Integer, BigDecimal> getYearlyIncomeSummary() {
        return transactions.stream()
                .filter(t -> t.amount.compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.groupingBy(t -> t.date.getYear(),
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO,
                                t -> t.amount,
                                BigDecimal::add)));
    }

    // 每年支出
    public Map<Integer, BigDecimal> getYearlyExpenseSummary() {
        return transactions.stream()
                .filter(t -> t.amount.compareTo(BigDecimal.ZERO) < 0)
                .collect(Collectors.groupingBy(t -> t.date.getYear(),
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO,
                                t -> t.amount.abs(),
                                BigDecimal::add)));
    }


    private static class Transaction {
        LocalDate date;
        BigDecimal amount;

        public Transaction(LocalDate date, BigDecimal amount) {
            this.date = date;
            this.amount = amount;
        }
    }
}
