package tech.tablesaw.io.csv;

import org.junit.Test;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

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
