package tech.tablesaw.columns.instant;

import java.time.Instant;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractColumnParser;

/**
 * Dummy implementation
 * Create an InstantColumn using SqlReader which doesn't require string parsing or convert from another column type
 */
public class InstantParser extends AbstractColumnParser<Instant> {

    public InstantParser(ColumnType columnType) {
        super(columnType);
    }

    @Override
    public boolean canParse(String s) {
        return false;
    }

    @Override
    public Instant parse(String value) {
        throw new IllegalArgumentException("Cannot parse " + value);
    }
}
