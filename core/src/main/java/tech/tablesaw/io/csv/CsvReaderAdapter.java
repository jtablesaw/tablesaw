package tech.tablesaw.io.csv;

import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import tech.tablesaw.io.UnicodeBOMInputStream;

public interface CsvReaderAdapter {

    String[] readNext();

    void skipLines(int linesToSkip);

    static CsvReaderAdapter createWithOptions(UnicodeBOMInputStream ubis, char separator) {
        
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setLineSeparatorDetectionEnabled(true); // TODO discuss
        CsvFormat csvFormat = new CsvFormat();
        csvFormat.setDelimiter(separator);
        parserSettings.setFormat(csvFormat);

        CsvParser csvParser = new CsvParser(parserSettings);
        
        IterableResult<String[], ParsingContext> csvIterator = csvParser.iterate(ubis);
        
        return new UnivocityReaderCsvWrapper(csvIterator);
    }

}
