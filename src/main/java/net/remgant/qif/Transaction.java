package net.remgant.qif;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * <p>
 * This class represents a Quicken Interchange Format Transactions.
 * <p>
 * Currently, only Bank type transaction lists (or qif) files are supported. The only
 * fields support are:
 * <ul>
 * <li>Date
 * <li>Payee</li>
 * <li>Amount</li>
 *</ul>
 */
public class Transaction {
    private final LocalDate date;
    private final String payee;
    private final BigDecimal amount;

    /**
     * Creates a QIF transaction.
     *
     * @param date the date the transaction occurred
     * @param payee the payee of the transaction
     * @param amount the amount of the transaction (negative for debit, positive for credit)
     */
    public Transaction(LocalDate date, String payee, BigDecimal amount) {
        this.date = date;
        this.payee = payee;
        this.amount = amount;
    }

    /**
     * The date of the transaction as a LocalDate.
     * @return date of the transaction
     */
    public Optional<LocalDate> getDate() {
        if (date == null)
            return Optional.empty();
        return Optional.of(date);
    }

    /**
     * The payee of the transaction
     * @return payee of the transaction
     */
    public Optional<String> getPayee() {
        if (payee == null)
            return Optional.empty();
        return Optional.of(payee);
    }

    /**
     * The amount of the transaction as a BigDecimal. Positive is a credit, negative is a debit.
     * @return amount of the transaction
     */
    public Optional<BigDecimal> getAmount() {
        if (amount == null)
            return Optional.empty();
        return Optional.of(amount);
    }

    /**
     * Shows the transaction as readable string.
     * @return the transaction as a String
     */
    @Override
    public String toString() {
        return "Transaction{" +
                "date=" + date +
                ", payee='" + payee + '\'' +
                ", amount=" + amount +
                '}';
    }

    static class Builder {
        private LocalDate date;
        private BigDecimal amount;
        private String payee;

        Builder addDate(LocalDate date) {
            this.date = date;
            return this;
        }
        Builder addAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }
        Builder addPayee(String payee) {
            this.payee = payee;
            return this;
        }

        Transaction build() {
            return new Transaction(date, payee, amount);
        }

        void reset() {
           date = null;
           payee = null;
           amount = null;
        }
    }
}
