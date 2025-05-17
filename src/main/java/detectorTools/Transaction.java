package detectorTools;

import java.time.LocalDateTime;

class Transaction {
    private LocalDateTime timestamp;
    private double amount;

    public Transaction(LocalDateTime timestamp, double amount) {
        this.timestamp = timestamp;
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public double getAmount() { return amount; }
}
