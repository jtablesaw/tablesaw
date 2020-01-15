package tech.tablesaw.analytic;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.Column;

/** This class holds data on what aggregate and numbering functions to execute in the query. */
final class ArgumentList {
  // Throws if a column with the same name is registered
  private final Map<String, FunctionCall<AggregateFunctions>> aggregateFunctions;
  private final Map<String, FunctionCall<NumberingFunctions>> numberingFunctions;

  // Used to determine the order in which to add new columns.
  private final Set<String> newColumnNames;

  private ArgumentList(
      Map<String, FunctionCall<AggregateFunctions>> aggregateFunctions,
      Map<String, FunctionCall<NumberingFunctions>> numberingFunctions,
      Set<String> newColumnNames) {
    this.aggregateFunctions = aggregateFunctions;
    this.numberingFunctions = numberingFunctions;
    this.newColumnNames = newColumnNames;
  }

  static Builder builder() {
    return new Builder();
  }

  public Map<String, FunctionCall<AggregateFunctions>> getAggregateFunctions() {
    return aggregateFunctions;
  }

  public Map<String, FunctionCall<NumberingFunctions>> getNumberingFunctions() {
    return numberingFunctions;
  }

  public List<String> getNewColumnNames() {
    return ImmutableList.copyOf(newColumnNames);
  }

  public String toSqlString(String windowName) {
    StringBuilder sb = new StringBuilder();
    int colCount = 0;
    for (String newColName : newColumnNames) {
      String optionalNumberingCol =
          Optional.ofNullable(numberingFunctions.get(newColName))
              .map(f -> f.toSqlString(windowName))
              .orElse("");
      String optionalAggregateCol =
          Optional.ofNullable(aggregateFunctions.get(newColName))
              .map(f -> f.toSqlString(windowName))
              .orElse("");
      sb.append(optionalNumberingCol);
      sb.append(optionalAggregateCol);
      colCount++;
      if (colCount < newColumnNames.size()) {
        sb.append(",");
        sb.append(System.lineSeparator());
      }
    }
    return sb.toString();
  }

  /** @return an ordered list of new columns this analytic query will generate. */
  List<Column<?>> createEmptyDestinationColumns(int rowCount) {
    List<Column<?>> newColumns = new ArrayList<>();
    for (String toColumn : newColumnNames) {
      FunctionCall<? extends FunctionMetaData> functionCall =
          Stream.of(aggregateFunctions.get(toColumn), numberingFunctions.get(toColumn))
              .filter(java.util.Objects::nonNull)
              .findFirst()
              .get();
      ColumnType type = functionCall.function.returnType();
      Column<?> resultColumn = type.create(toColumn);
      newColumns.add(resultColumn);

      for (int i = 0; i < rowCount; i++) {
        resultColumn.appendMissing();
      }
    }
    return newColumns;
  }

  @Override
  public String toString() {
    return toSqlString("?");
  }

  static class FunctionCall<T extends FunctionMetaData> {
    private final String sourceColumnName;
    private final String destinationColumnName;
    private final T function;

    public String getSourceColumnName() {
      return sourceColumnName;
    }

    public String getDestinationColumnName() {
      return destinationColumnName;
    }

    public T getFunction() {
      return function;
    }

    public FunctionCall(String sourceColumnName, String destinationColumnName, T function) {
      this.sourceColumnName = sourceColumnName;
      this.destinationColumnName = destinationColumnName;
      this.function = function;
    }

    @Override
    public String toString() {
      return toSqlString("");
    }

    public String toSqlString(String windowName) {
      String over = "";
      if (!windowName.isEmpty()) {
        over = " OVER " + windowName;
      }
      return function.toString()
          + '('
          + sourceColumnName
          + ")"
          + over
          + " AS "
          + destinationColumnName;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      FunctionCall<?> that = (FunctionCall<?>) o;
      return Objects.equal(sourceColumnName, that.sourceColumnName)
          && Objects.equal(destinationColumnName, that.destinationColumnName)
          && Objects.equal(function, that.function);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(sourceColumnName, destinationColumnName, function);
    }
  }

  static class Builder {

    // Maps the destination column name to aggregate function.
    private final Map<String, FunctionCall<AggregateFunctions>> aggregateFunctions =
        new HashMap<>();
    // Maps the destination column name to aggregate function.
    private final Map<String, FunctionCall<NumberingFunctions>> numberingFunctions =
        new HashMap<>();

    // Throws if a column with the same name is registered twice.
    private final Set<String> newColumnNames = new LinkedHashSet<>();

    // Temporarily store analytic function data until the user calls 'as' to give the new column a
    // name
    // and save all the metadata.
    private String stagedFromColumn;
    private AggregateFunctions stagedAggregateFunction;
    private NumberingFunctions stagedNumberingFunction;

    private Builder() {}

    Builder stageFunction(String fromColumn, AggregateFunctions function) {
      checkNothingStaged();
      Preconditions.checkNotNull(fromColumn);
      Preconditions.checkNotNull(function);
      this.stagedFromColumn = fromColumn;
      this.stagedAggregateFunction = function;
      return this;
    }

    Builder stageFunction(NumberingFunctions function) {
      checkNothingStaged();
      Preconditions.checkNotNull(function);
      // Numbering functions do not have a from column. Use a placeholder instead.
      this.stagedFromColumn = "NUMBERING_FUNCTION_PLACEHOLDER";
      this.stagedNumberingFunction = function;
      return this;
    }

    private void checkNothingStaged() {
      if (this.stagedFromColumn != null) {
        throw new IllegalArgumentException(
            "Cannot stage a column while another is staged. Must call unstage first");
      }
    }

    private void checkForDuplicateAlias(String toColumn) {
      Preconditions.checkArgument(
          !newColumnNames.contains(toColumn), "Cannot add duplicate column name: " + toColumn);
      newColumnNames.add(toColumn);
    }

    Builder unStageFunction(String toColumn) {
      Preconditions.checkNotNull(stagedFromColumn);
      checkForDuplicateAlias(toColumn);

      if (stagedNumberingFunction != null) {
        Preconditions.checkNotNull(stagedNumberingFunction);
        this.numberingFunctions.put(
            toColumn, new FunctionCall<>("", toColumn, this.stagedNumberingFunction));
      } else {
        Preconditions.checkNotNull(stagedAggregateFunction);
        this.aggregateFunctions.put(
            toColumn,
            new FunctionCall<>(this.stagedFromColumn, toColumn, this.stagedAggregateFunction));
      }
      this.stagedNumberingFunction = null;
      this.stagedAggregateFunction = null;
      this.stagedFromColumn = null;

      return this;
    }

    ArgumentList build() {
      if (this.stagedFromColumn != null) {
        throw new IllegalStateException("Cannot build when a column is staged");
      }
      return new ArgumentList(aggregateFunctions, numberingFunctions, newColumnNames);
    }
  }
}
