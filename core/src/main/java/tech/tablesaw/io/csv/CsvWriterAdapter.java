package tech.tablesaw.io.csv;

import java.io.Writer;

public interface CsvWriterAdapter {

    static UnivocityCsvWriterAdapter create(Writer writer) {

        return new UnivocityCsvWriterAdapter(writer);
    }

}
