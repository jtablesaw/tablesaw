package tech.tablesaw.io.csv;

import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.ResultIterator;

public class UnivocityReaderCsvWrapper implements CsvReaderAdapter {

    private ResultIterator<String[], ParsingContext> iterator;

    public UnivocityReaderCsvWrapper(IterableResult<String[], ParsingContext> result) {
        this.iterator = result.iterator();
    }

    @Override
    public String[] readNext() {
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    @Override
    public void skipLines(int linesToSkip) {
        
        for (int i=0; i < linesToSkip; i++) {
            readNext();
        }
        
    }
    
}
