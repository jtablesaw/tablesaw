package tech.tablesaw.io.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Test;

import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class CsvWriteOptionsTest {

    @Test
    public void testSettingsPropagation() {

        Table test = Table.create("test", StringColumn.create("t"));
        test.stringColumn(0).appendCell("testing");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CsvWriteOptions options = new CsvWriteOptions.Builder(stream)
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

        CsvWriter writer = new CsvWriter(test, options);
        assertEquals('~', writer.getEscapeChar());
        assertTrue(writer.getHeader());
        assertEquals("\r\n", writer.getLineEnd());
        assertEquals('"', writer.getQuoteCharacter());
        assertEquals('.', writer.getSeparator());
    }
}
