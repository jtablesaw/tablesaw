package tech.tablesaw.aggregate;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;

abstract class BooleanCountFunction extends AggregateFunction<Integer, BooleanColumn> {

    public BooleanCountFunction(String functionName) {
        super(functionName);
    }

    @Override
    abstract public Integer summarize(BooleanColumn column);

    @Override
    public boolean isCompatableColumn(ColumnType type) {
        return type.equals(ColumnType.BOOLEAN);
    }

    @Override
    public ColumnType returnType() {
        return ColumnType.DOUBLE;
    }
}
