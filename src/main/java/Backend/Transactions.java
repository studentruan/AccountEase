package Backend;

import java.time.LocalDate;
//Designed by Zhou Fang
public class Transactions {
    private String id;
    private LocalDate date;
    private String counterparty;
    private String product;
    private String type; // Expense / Income
    private double amount;
    private String classification;

    public Transactions(String id, LocalDate date, String counterparty, String product,
                        String type, double amount, String classification) {
        this.id = id;
        this.date = date;
        this.counterparty = counterparty;
        this.product = product;
        this.type = type;
        this.amount = amount;
        this.classification = classification;
    }

    // Getter & Setter methods
    public String getDescription() {
        return product + " (" + counterparty + ")";
    }




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(String counterparty) {
        this.counterparty = counterparty;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", counterparty='" + counterparty + '\'' +
                ", product='" + product + '\'' +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", classification='" + classification + '\'' +
                '}';
    }
}
