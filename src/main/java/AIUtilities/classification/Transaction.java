package AIUtilities.classification;

/**
 * Represents a transaction record with basic identifying and descriptive information.
 * <p>
 * Each transaction contains an ID, a counterparty (e.g. merchant or entity),
 * a product or service involved, and optionally a description and amount.
 */
public class Transaction {
    private String id;
    private String counterparty;
    private String product;
    private String description;
    private String amount;

    /**
     * Constructs a new Transaction with the specified ID, counterparty, and product.
     *
     * @param id           Unique identifier for the transaction
     * @param counterparty The other party involved in the transaction (e.g. merchant)
     * @param product      The product or service purchased or exchanged
     */
    public Transaction(String id, String counterparty, String product) {
        this.id = id;
        this.counterparty = counterparty;
        this.product = product;
    }

    /**
     * Returns the transaction ID.
     *
     * @return transaction ID
     */
    public String getId() { return id; }

    /**
     * Returns the counterparty name or identifier.
     *
     * @return counterparty name
     */
    public String getCounterparty() { return counterparty; }

    /**
     * Returns the product or service involved in the transaction.
     *
     * @return product name
     */
    public String getProduct() { return product; }

    /**
     * Returns a string representation of the transaction, mainly for display purposes.
     *
     * @return a descriptive string of the transaction
     */
    @Override
    public String toString() {
        return "\nIn " + counterparty +
                "\nBuy/get the : " + product;
    }

    /**
     * Compares this transaction to another object for equality based on ID.
     *
     * @param o the object to compare with
     * @return true if the other object is a Transaction with the same ID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id.equals(that.id);
    }
}