package tech.tablesaw.io.csv;

import java.io.StringWriter;
import java.io.Writer;

public interface CsvWriterAdapter {

    static UnivocityCsvWriterAdapter create(
        Writer writer, char separator, char quoteChar, char escapeChar, String lineEnd) {
        return new UnivocityCsvWriterAdapter(
            writer, separator, quoteChar, escapeChar, lineEnd);
    }

    static CsvWriterAdapter create(Writer writer) {
        return new UnivocityCsvWriterAdapter(writer);
    }

    void writeNext(String[] record);

}
