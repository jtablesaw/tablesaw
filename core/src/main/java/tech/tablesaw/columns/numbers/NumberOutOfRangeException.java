package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.ColumnType;

public class NumberOutOfRangeException extends RuntimeException {

    private final String inputValue;
    private final Long parsedValue;
    private ColumnType failingType;

    public NumberOutOfRangeException(String inputValue, Long parsedValue, ColumnType columnType) {
        this.inputValue = inputValue;
        this.parsedValue = parsedValue;
        this.failingType = columnType;
    }

    public NumberOutOfRangeException(String inputValue, Long parsedValue) {
        this.inputValue = inputValue;
        this.parsedValue = parsedValue;
    }

    public String getInputValue() {
        return inputValue;
    }

    public Long getParsedValue() {
        return parsedValue;
    }

    public ColumnType getFailingType() {
        return failingType;
    }
}
