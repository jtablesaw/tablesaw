package tech.tablesaw.io.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Test;

import com.univocity.parsers.csv.CsvWriterSettings;

public class CsvWriteOptionsTest {

    @Test
    public void testSettingsPropagation() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriteOptions options = CsvWriteOptions.builder(stream)
                .escapeChar('~')
                .header(true)
                .lineEnd("\r\n")
                .quoteChar('"')
                .separator('.')
                .build();
        assertEquals('~', options.escapeChar());
        assertTrue(options.header());
        assertEquals('"', options.quoteChar());
        assertEquals('.', options.separator());
        
        CsvWriterSettings settings = CsvWriter.createSettings(options);

        assertEquals('~', settings.getFormat().getQuoteEscape());
        assertEquals("\r\n", settings.getFormat().getLineSeparatorString());
        assertEquals('"', settings.getFormat().getQuote());
        assertEquals('.', settings.getFormat().getDelimiter());
    }
}
