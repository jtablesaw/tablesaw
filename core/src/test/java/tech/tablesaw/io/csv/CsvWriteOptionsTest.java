package tech.tablesaw.io.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.univocity.parsers.csv.CsvWriterSettings;
import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Test;

public class CsvWriteOptionsTest {

  @Test
  public void testSettingsPropagation() {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    CsvWriteOptions options =
        CsvWriteOptions.builder(stream)
            .escapeChar('~')
            .header(true)
            .lineEnd("\r\n")
            .quoteChar('"')
            .separator('.')
            .quoteAllFields(true)
            .ignoreLeadingWhitespaces(true)
            .ignoreTrailingWhitespaces(true)
            .build();
    assertEquals('~', options.escapeChar());
    assertTrue(options.header());
    assertEquals('"', options.quoteChar());
    assertEquals('.', options.separator());
    assertTrue(options.ignoreLeadingWhitespaces());
    assertTrue(options.ignoreTrailingWhitespaces());
    assertTrue(options.quoteAllFields());

    CsvWriterSettings settings = CsvWriter.createSettings(options);
    assertTrue(settings.getQuoteAllFields());
    assertEquals('~', settings.getFormat().getQuoteEscape());
    assertEquals("\r\n", settings.getFormat().getLineSeparatorString());
    assertEquals('"', settings.getFormat().getQuote());
    assertEquals('.', settings.getFormat().getDelimiter());
    assertEquals(options.ignoreLeadingWhitespaces(), settings.getIgnoreLeadingWhitespaces());
    assertEquals(options.ignoreTrailingWhitespaces(), settings.getIgnoreTrailingWhitespaces());
  }
}
