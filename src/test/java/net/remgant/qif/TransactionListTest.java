package net.remgant.qif;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TransactionListTest {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void testOutOfBoundsException() {
        TransactionList list = new TransactionList("Bank", new Transaction[]{});
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
    }
}
