package tech.tablesaw.io.csv;

import java.io.Writer;

import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvWriterSettings;

public class UnivocityCsvWriterAdapter implements CsvWriterAdapter {

    private com.univocity.parsers.csv.CsvWriter csvWriter;

    public UnivocityCsvWriterAdapter(
        Writer writer, char separator, char quoteChar, char escapeChar, String lineEnd) {

        CsvWriterSettings settings = new CsvWriterSettings();
        CsvFormat csvFormat = new CsvFormat();
        csvFormat.setDelimiter(separator);
        csvFormat.setQuote(quoteChar);
        csvFormat.setQuoteEscape(escapeChar);
        csvFormat.setLineSeparator(lineEnd);
        settings.setFormat(csvFormat);
        csvWriter = new com.univocity.parsers.csv.CsvWriter(writer, settings);
        
    }

    public UnivocityCsvWriterAdapter(Writer writer) {
        CsvWriterSettings settings = new CsvWriterSettings();
        csvWriter = new com.univocity.parsers.csv.CsvWriter(writer, settings);
    }

    @Override
    public void writeNext(String[] record) {
        csvWriter.writeRow(record);
    }
    
}
