package Backend;

import java.time.LocalDate;
import java.util.Objects;
//Designed by Zhou Fang
public class Transaction_FZ {
    private String id;
    private LocalDate date;
    private String counterparty;
    private String product;
    private String type;
    private double amount;
    private String classification; // Optional field

    public Transaction_FZ(String id, LocalDate date, String counterparty, String product, String type, double amount) {
        this.id = id;
        this.date = date;
        this.counterparty = counterparty;
        this.product = product;
        this.type = type;
        this.amount = amount;

    }

    // === Getters and Setters ===
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getCounterparty() { return counterparty; }
    public void setCounterparty(String counterparty) { this.counterparty = counterparty; }

    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }

    // === equals() and hashCode() based on id field ===
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // same object reference
        if (obj == null || getClass() != obj.getClass()) return false;
        Transaction_FZ that = (Transaction_FZ) obj;
        return Objects.equals(id, that.id); // compare IDs
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // consistent with equals
    }

    // === toString() for debugging
    @Override
    public String toString() {
        return String.format("Transaction[id=%s, date=%s, product=%s, amount=%.2f]",
                              id, date, product, amount);
    }
}
