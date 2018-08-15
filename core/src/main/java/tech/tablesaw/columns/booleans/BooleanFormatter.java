package tech.tablesaw.columns.booleans;

import tech.tablesaw.api.BooleanColumn;

public class BooleanFormatter {
    private String trueString = "true";
    private String falseString = "false";

    private String missingString = "";

    public BooleanFormatter(String trueString, String falseString, String missingString) {
        this.trueString = trueString;
        this.falseString = falseString;
        this.missingString = missingString;
    }

    public BooleanFormatter(String trueString, String falseString) {
        this.trueString = trueString;
        this.falseString = falseString;
        this.missingString = "";
    }

    public BooleanFormatter(String missingString) {
        this.missingString = missingString;
    }

    public String format(Boolean value) {
        if (value == null) {
            return missingString;
        }
        if (value) {
            return trueString;
        }
        return falseString;
    }

    public String format(byte value) {
        if (value == BooleanColumn.MISSING_VALUE) {
            return missingString;
        }
        if (value == (byte) 1) {
            return trueString;
        }
        return falseString;
    }

    @Override
    public String toString() {
        return "BooleanFormatter{" +
                "trueString='" + trueString + '\'' +
                ", falseString='" + falseString + '\'' +
                ", missingString='" + missingString + '\'' +
                '}';
    }
}
