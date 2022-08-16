package net.remgant.qif;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QIFReader {
    final private ThreadLocal<NumberFormat> numberFormat = ThreadLocal.withInitial(() -> NumberFormat.getNumberInstance(Locale.US));

    protected FileSystem fileSystem = FileSystems.getDefault();
    public TransactionList readTransactions(String fileName) throws IOException {
        Path path = fileSystem.getPath(fileName);
        BufferedReader bufferedReader = Files.newBufferedReader(path);

        return readTransactions(bufferedReader);
    }
    public TransactionList readTransactions(java.io.Reader reader) throws IOException {
        BufferedReader bufferedReader;
        if (reader instanceof BufferedReader)
            bufferedReader = (BufferedReader)reader;
        else
            bufferedReader = new BufferedReader(reader);
        List<Transaction> list = new LinkedList<>();
        String fileType = "";
        String line = bufferedReader.readLine();
        Transaction.Builder builder = new Transaction.Builder();
        while (line != null) {
            if (line.startsWith("!Type:")) {
                fileType = line.substring("!Type:".length());
                if (!fileType.equalsIgnoreCase("Bank"))
                    throw new RuntimeException("file type not supported: " + fileType);
                line = bufferedReader.readLine();
                continue;
            }
            @SuppressWarnings("RegExpSingleCharAlternation")
            Pattern pattern = Pattern.compile("(D|P|T|\\^)(.*)");
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                switch (matcher.group(1)) {
                    case "D":
                        LocalDate localDate = parseDate(matcher.group(2));
                        builder.addDate(localDate);
                        break;
                    case "P":
                        builder.addPayee(matcher.group(2));
                        break;
                    case "T":
                        Number number;
                        try {
                            number = numberFormat.get().parse(matcher.group(2));
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        builder.addAmount(new BigDecimal(number.toString()));
                        break;
                    case "^":
                        list.add(builder.build());
                        builder.reset();
                        break;
                }
            }
            if (line.length() == 0) {
                list.add(builder.build());
                builder.reset();
            }
            line = bufferedReader.readLine();
        }
        return new TransactionList(fileType, list.toArray(new Transaction[0]));
    }

    final private Pattern datePattern = Pattern.compile("(\\d{1,2})/(\\d{1,2})[/'](\\d{1,4})");
    private LocalDate parseDate(String dateString) {
        Matcher matcher = datePattern.matcher(dateString);
        if (!matcher.matches())
            throw new DateTimeException("Parse error: "+dateString);
        int m = Integer.parseInt(matcher.group(1));
        int d = Integer.parseInt(matcher.group(2));
        int y = Integer.parseInt(matcher.group(3));
        // We'll just arbitrarily decide two digits years 90 or greater are
        // in the 20th century and ones less are in the 21st
        if (y < 90)
            y += 2000;
        else if (y < 100)
            y += 1900;
        return LocalDate.of(y, m, d);
    }
}
