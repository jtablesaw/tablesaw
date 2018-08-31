package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.ColumnType;

public class NumberOutOfRangeException extends Exception {

    private final String inputValue;
    private final Number parsedValue;
    private ColumnType failingType;

    public NumberOutOfRangeException(String inputValue, Number parsedValue, ColumnType columnType) {
        this.inputValue = inputValue;
        this.parsedValue = parsedValue;
        this.failingType = columnType;
    }

    public NumberOutOfRangeException(String inputValue, Number parsedValue) {
        this.inputValue = inputValue;
        this.parsedValue = parsedValue;
    }

    public String getInputValue() {
        return inputValue;
    }

    public Number getParsedValue() {
        return parsedValue;
    }

    public ColumnType getFailingType() {
        return failingType;
    }
}
