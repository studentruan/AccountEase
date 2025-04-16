package org.classification;

public class Transaction {
    private String id;
    private String counterparty;
    private String product;
    private String description;
    private String amount;

    // Constructor
    public Transaction(String id, String counterparty, String product) {
        this.id = id;
        this.counterparty = counterparty;
        this.product = product;
    }

    // Getters
    public String getId() { return id; }
    public String getCounterparty() { return counterparty; }
    public String getProduct() { return product; }

    @Override
    public String toString() {
        return "\nCounterparty: " + counterparty +
                "\nProduct: " + product;
    }

    // Optional: equals() and hashCode() if you need to compare Transaction objects
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id.equals(that.id) ;
    }
}