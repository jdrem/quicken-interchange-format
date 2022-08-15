package net.remgant.qif;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionBuilderTest {
    @Test
    public void testBuilder() {
        Transaction transaction =
                new Transaction.Builder()
                        .addDate(LocalDate.of(2022,1,20))
                        .addAmount(new BigDecimal("1000.00"))
                        .addPayee("Payee")
                        .build();
        assertEquals(LocalDate.of(2022,1, 20), transaction.getDate());
        assertEquals(new BigDecimal("1000.00"), transaction.getAmount());
        assertEquals("Payee", transaction.getPayee());
    }
}
