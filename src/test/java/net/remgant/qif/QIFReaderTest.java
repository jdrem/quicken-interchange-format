package net.remgant.qif;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class QIFReaderTest {
    @Test
    public void testDate1() throws IOException {
        StringReader stringReader = new StringReader(
                "!Type:Bank\n" +
                        "D01/16/2022\n" +
                        "PPayee\n" +
                        "T123.56\n" +
                        "C*\n" +
                        "^\n");
        QIFReader reader = new QIFReader();
        TransactionList list = reader.readTransactions(stringReader);
        assertEquals("Bank", list.getType());
        assertEquals(1, list.size());
        assertNotNull(list.get(0));
        Transaction transaction = list.get(0);
        assertEquals(Optional.of(LocalDate.of(2022, 1, 16)), transaction.getDate());
    }

    @Test
    public void testDate2() throws IOException {
        StringReader stringReader = new StringReader(
                "!Type:Bank\n" +
                        "D01/16/22\n" +
                        "PPayee\n" +
                        "T123.56\n" +
                        "C*\n" +
                        "^\n");
        QIFReader reader = new QIFReader();
        TransactionList list = reader.readTransactions(stringReader);
        assertEquals("Bank", list.getType());
        assertEquals(1, list.size());
        assertNotNull(list.get(0));
        Transaction transaction = list.get(0);
        assertEquals(Optional.of(LocalDate.of(2022, 1, 16)), transaction.getDate());
    }

    @Test
    public void testDate3() throws IOException {
        StringReader stringReader = new StringReader(
                "!Type:Bank\n" +
                        "D01/16/99\n" +
                        "PPayee\n" +
                        "T123.56\n" +
                        "C*\n" +
                        "^\n");
        QIFReader reader = new QIFReader();
        TransactionList list = reader.readTransactions(stringReader);
        assertEquals("Bank", list.getType());
        assertEquals(1, list.size());
        assertNotNull(list.get(0));
        Transaction transaction = list.get(0);
        assertEquals(Optional.of(LocalDate.of(1999, 1, 16)), transaction.getDate());
    }

    @Test
    public void testDate5() throws IOException {
        StringReader stringReader = new StringReader(
                "!Type:Bank\n" +
                        "D1/2/2022\n" +
                        "PPayee\n" +
                        "T123.56\n" +
                        "C*\n" +
                        "^\n");
        QIFReader reader = new QIFReader();
        TransactionList list = reader.readTransactions(stringReader);
        assertEquals("Bank", list.getType());
        assertEquals(1, list.size());
        assertNotNull(list.get(0));
        Transaction transaction = list.get(0);
        assertEquals(Optional.of(LocalDate.of(2022, 1, 2)), transaction.getDate());
    }

    @Test
    public void testDate6() throws IOException {
        StringReader stringReader = new StringReader(
                "!Type:Bank\n" +
                        "D01/16'2022\n" +
                        "PPayee\n" +
                        "T123.56\n" +
                        "C*\n" +
                        "^\n");
        QIFReader reader = new QIFReader();

        TransactionList list = reader.readTransactions(stringReader);
        assertEquals("Bank", list.getType());
        assertEquals(1, list.size());
        assertNotNull(list.get(0));
        Transaction transaction = list.get(0);
        assertEquals(Optional.of(LocalDate.of(2022, 1, 16)), transaction.getDate());
    }

    @Test
    public void testAmount1() throws IOException {
        StringReader stringReader = new StringReader(
                "!Type:Bank\n" +
                        "D01/16/2022\n" +
                        "PPayee\n" +
                        "T123\n" +
                        "C*\n" +
                        "^\n");
        QIFReader reader = new QIFReader();
        TransactionList list = reader.readTransactions(stringReader);
        assertEquals("Bank", list.getType());
        assertEquals(1, list.size());
        assertNotNull(list.get(0));
        Transaction transaction = list.get(0);
        assertEquals(Optional.of(new BigDecimal(123)), transaction.getAmount());
    }

    @Test
    public void testAmount2() throws IOException {
        StringReader stringReader = new StringReader(
                "!Type:Bank\n" +
                        "D01/16/2022\n" +
                        "PPayee\n" +
                        "T123.00\n" +
                        "C*\n" +
                        "^\n");
        QIFReader reader = new QIFReader();
        TransactionList list = reader.readTransactions(stringReader);
        assertEquals("Bank", list.getType());
        assertEquals(1, list.size());
        assertNotNull(list.get(0));
        Transaction transaction = list.get(0);
        assertEquals(Optional.of(new BigDecimal(123)), transaction.getAmount());
    }

    @Test
    public void testAmount3() throws IOException {
        StringReader stringReader = new StringReader(
                "!Type:Bank\n" +
                        "D01/16/2022\n" +
                        "PPayee\n" +
                        "T123.45\n" +
                        "C*\n" +
                        "^\n");
        QIFReader reader = new QIFReader();
        TransactionList list = reader.readTransactions(stringReader);
        assertEquals("Bank", list.getType());
        assertEquals(1, list.size());
        assertNotNull(list.get(0));
        Transaction transaction = list.get(0);
        assertEquals(Optional.of(new BigDecimal("123.45")), transaction.getAmount());
    }

    @Test
    public void testAmount4() throws IOException {
        StringReader stringReader = new StringReader(
                "!Type:Bank\n" +
                        "D01/16/2022\n" +
                        "PPayee\n" +
                        "T1,234,567.89\n" +
                        "C*\n" +
                        "^\n");
        QIFReader reader = new QIFReader();
        TransactionList list = reader.readTransactions(stringReader);
        assertEquals("Bank", list.getType());
        assertEquals(1, list.size());
        assertNotNull(list.get(0));
        Transaction transaction = list.get(0);
        assertEquals(Optional.of(new BigDecimal("1234567.89")), transaction.getAmount());
    }

    @Test
    public void testAmount5() throws IOException {
        StringReader stringReader = new StringReader(
                "!Type:Bank\n" +
                        "D01/16/2022\n" +
                        "PPayee\n" +
                        "T-123.45\n" +
                        "C*\n" +
                        "^\n");
        QIFReader reader = new QIFReader();
        TransactionList list = reader.readTransactions(stringReader);
        assertEquals("Bank", list.getType());
        assertEquals(1, list.size());
        assertNotNull(list.get(0));
        Transaction transaction = list.get(0);
        assertEquals(Optional.of(new BigDecimal("-123.45")), transaction.getAmount());
    }

    @Test
    public void testPayee() throws IOException {
        StringReader stringReader = new StringReader(
                "!Type:Bank\n" +
                        "D01/16/2022\n" +
                        "PPayee\n" +
                        "T-123.45\n" +
                        "C*\n" +
                        "^\n");
        QIFReader reader = new QIFReader();
        TransactionList list = reader.readTransactions(stringReader);
        assertEquals("Bank", list.getType());
        assertEquals(1, list.size());
        assertNotNull(list.get(0));
        Transaction transaction = list.get(0);
        assertEquals(Optional.of("Payee"), transaction.getPayee());
    }

    @Test
    public void testBadFileType() {
        StringReader stringReader = new StringReader(
                "!Type:Unknown\n" +
                        "D01/16/2022\n" +
                        "PPayee\n" +
                        "T-123.45\n" +
                        "C*\n" +
                        "^\n");
        QIFReader reader = new QIFReader();
        Assertions.assertThrows(RuntimeException.class, () -> reader.readTransactions(stringReader));
    }

    @Test
    public void testBadDate1() {
        StringReader stringReader = new StringReader(
                "!Type:Bank\n" +
                        "D13/16/2022\n" +
                        "PPayee\n" +
                        "T-123.45\n" +
                        "C*\n" +
                        "^\n");
        QIFReader reader = new QIFReader();
        Assertions.assertThrows(DateTimeException.class, () -> reader.readTransactions(stringReader));
    }

    @Test
    public void testBadDate2() {
        StringReader stringReader = new StringReader(
                "!Type:Bank\n" +
                        "DABCDEFG\n" +
                        "PPayee\n" +
                        "T-123.45\n" +
                        "C*\n" +
                        "^\n");
        QIFReader reader = new QIFReader();
        Assertions.assertThrows(DateTimeException.class, () -> reader.readTransactions(stringReader));
    }

    @Test
    public void testEmptyOptionals() throws IOException {
        StringReader stringReader = new StringReader(
                "!Type:Bank\n" +
                        "^\n");
        QIFReader reader = new QIFReader();
        TransactionList list = reader.readTransactions(stringReader);
        assertEquals("Bank", list.getType());
        assertEquals(1, list.size());
        assertNotNull(list.get(0));
        Transaction transaction = list.get(0);
        assertFalse(transaction.getDate().isPresent());
        assertFalse(transaction.getPayee().isPresent());
        assertFalse(transaction.getAmount().isPresent());
    }

    @Test
    public void testBadNumber() {
        StringReader stringReader = new StringReader(
                "!Type:Bank\n" +
                        "D01/16/2022\n" +
                        "PPayee\n" +
                        "TABDEF\n" +
                        "C*\n" +
                        "^\n");
        QIFReader reader = new QIFReader();
        Assertions.assertThrows(RuntimeException.class, () -> reader.readTransactions(stringReader));
    }

    @Test
    public void testList1() throws IOException {
        StringReader stringReader = new StringReader(
                "!Type:Bank\n" +
                        "D01/02/2022\n" +
                        "PPayee\n" +
                        "T123.56\n" +
                        "C*\n" +
                        "^\n" +
                        "D01/02/2022\n" +
                        "PAAAAA\n" +
                        "T-100.00\n" +
                        "C*\n" +
                        "^\n" +
                        "D02/16/2022\n" +
                        "PCCCC\n" +
                        "T1,897.44\n" +
                        "C*\n" +
                        "^\n" +
                        "D02/16/2022\n" +
                        "PBBBB\n" +
                        "T897\n" +
                        "C*\n" +
                        "^\n");
        QIFReader reader = new QIFReader();
        TransactionList list = reader.readTransactions(stringReader);
        assertEquals("Bank", list.getType());
        assertEquals(4, list.size());
        System.out.println(list);
    }

    @Test
    public void testList2() throws IOException {
        StringReader stringReader = new StringReader(
                "!Type:Bank\n" +
                        "D01/02/2022\n" +
                        "PPayee\n" +
                        "T123.56\n" +
                        "C*\n" +
                        "\n" +
                        "D01/02/2022\n" +
                        "PAAAAA\n" +
                        "T-100.00\n" +
                        "C*\n" +
                        "\n" +
                        "D02/16/2022\n" +
                        "PCCCC\n" +
                        "T1,897.44\n" +
                        "C*\n" +
                        "\n" +
                        "D02/16/2022\n" +
                        "PBBBB\n" +
                        "T897\n" +
                        "C*\n" +
                        "\n");
        QIFReader reader = new QIFReader();
        TransactionList list = reader.readTransactions(stringReader);
        assertEquals("Bank", list.getType());
        assertEquals(4, list.size());
        System.out.println(list);
    }

    @Test
    public void testReadFromFile() throws IOException {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path dir = fileSystem.getPath("/data");
        Files.createDirectory(dir);
        Path qifFile = dir.resolve("test.qif");
        Files.write(qifFile, ("!Type:Bank\n" +
                "D01/02/2022\n" +
                "PPayee\n" +
                "T123.56\n" +
                "C*\n" +
                "^\n" +
                "D01/02/2022\n" +
                "PAAAAA\n" +
                "T-100.00\n" +
                "C*\n" +
                "^\n" +
                "D02/16/2022\n" +
                "PCCCC\n" +
                "T1,897.44\n" +
                "C*\n" +
                "^\n" +
                "D02/16/2022\n" +
                "PBBBB\n" +
                "T897\n" +
                "C*\n" +
                "^\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        TestQIFReader qifReader = new TestQIFReader(fileSystem);
        TransactionList list = qifReader.readTransactions("/data/test.qif");
        assertEquals("Bank", list.getType());
        assertEquals(4, list.size());
    }

    static class TestQIFReader extends QIFReader {
        TestQIFReader(FileSystem fileSystem) {
            super();
            this.fileSystem = fileSystem;
        }
    }
}
