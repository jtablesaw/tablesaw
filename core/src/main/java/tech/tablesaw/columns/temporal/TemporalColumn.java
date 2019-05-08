package tech.tablesaw.columns.temporal;

import java.time.temporal.Temporal;

import tech.tablesaw.columns.Column;

public interface TemporalColumn<T extends Temporal> extends Column<T> {

    T get(int r);

    long getLongInternal(int r);

    TemporalColumn<T> appendInternal(long value);

}
