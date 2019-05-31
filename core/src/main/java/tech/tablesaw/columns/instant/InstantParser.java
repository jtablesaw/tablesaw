package tech.tablesaw.columns.instant;

import java.time.Instant;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractColumnParser;

public class InstantParser extends AbstractColumnParser<Instant> {

    public InstantParser(ColumnType columnType) {
        super(columnType);
    }

    @Override
    public boolean canParse(String s) {
        if (isMissing(s)) {
            return true;
        }
        try {
            parse(s);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public Instant parse(String value) {
        return Instant.parse(value);
    }
}
