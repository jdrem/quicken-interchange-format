package net.remgant.qif;

import java.util.AbstractList;

public class TransactionList extends AbstractList<Transaction> {
    String type;
    Transaction[] transactions;

    TransactionList(String type, Transaction[] transactions) {
        this.type = type;
        this.transactions = transactions;
    }

    @Override
    public Transaction get(int index) {
        if (index < 0 || index >= transactions.length)
            throw new IndexOutOfBoundsException();
        return transactions[index];
    }

    @Override
    public int size() {
        return transactions.length;
    }

    public String getType() {
        return type;
    }
}
