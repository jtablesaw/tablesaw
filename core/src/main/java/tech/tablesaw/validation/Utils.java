package tech.tablesaw.validation;

import java.util.Set;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;

public class Utils {

  public static Table summaryResultsTable() {
    return Table.create(
        "Validation Results",
        StringColumn.create("Column name"),
        StringColumn.create("Column type"),
        StringColumn.create("Validator name"),
        IntColumn.create("Validation Failures"));
  }

  public static Table detailedResultsTable() {
    return Table.create(
        "Validation Results",
        StringColumn.create("Column name"),
        StringColumn.create("Column type"),
        StringColumn.create("Validator name"),
        IntColumn.create("Row number"),
        StringColumn.create("Failing value"));
  }

  public static Table intermediateResultsTable() {
    Table t =
        Table.create(
            "Validation Results",
            StringColumn.create("Column name"),
            StringColumn.create("Column type"),
            StringColumn.create("Validator name"),
            StringColumn.create("Failing value"),
            DoubleColumn.create("Count"));
    t.doubleColumn("Count").setPrintFormatter(NumberColumnFormatter.ints());
    return t;
  }

  public static Table mergeResults(Table sourceData, Table validationData) {
    Table copy = sourceData.copy();
    Set<String> validationNames =
        validationData
            .stringColumn("Column name")
            .concatenate(" [")
            .concatenate(validationData.stringColumn("Validator name"))
            .concatenate("]")
            .asSet();
    boolean[] falseValues = new boolean[copy.rowCount()];
    for (String name : validationNames) {
      copy.addColumns(BooleanColumn.create(name, falseValues));
    }
    Table sortedValidationData = validationData.sortAscendingOn("Row number");
    for (Row validationRow : sortedValidationData) {
      Row srcRow = copy.row(validationRow.getInt("Row number"));
      String validationCategory =
          validationRow
              .getString("Column name")
              .concat(" [")
              .concat(validationRow.getString("Validator name"))
              .concat("]");
      srcRow.setBoolean(validationCategory, true);
    }
    return copy;
  }
}
