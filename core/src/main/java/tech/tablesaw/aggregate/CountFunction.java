package tech.tablesaw.aggregate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.Column;

abstract class CountFunction extends AggregateFunction<Integer, Column<?>> {

    public CountFunction(String functionName) {
        super(functionName);
    }

    @Override
    abstract public Integer summarize(Column<?> column);

    @Override
    public boolean isCompatibleColumn(ColumnType type) {
        return true;
    }

    @Override
    public ColumnType returnType() {
        return ColumnType.DOUBLE;
    }
}
