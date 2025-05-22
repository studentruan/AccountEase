/**
 * Represents financial transaction with temporal metadata.
 */
package detectorTools;

import java.time.LocalDateTime;

class Transaction {
    private LocalDateTime timestamp;
    private double amount;
    /**
     * Creates transaction record.
     *
     * @param timestamp transaction datetime
     * @param amount transaction value
     */
    public Transaction(LocalDateTime timestamp, double amount) {
        this.timestamp = timestamp;
        this.amount = amount;
    }

    public double getAmount() { return amount; }
}
