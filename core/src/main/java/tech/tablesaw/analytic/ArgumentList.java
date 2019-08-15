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
import java.util.stream.Stream;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.Column;

final public class ArgumentList {
  // Throws if a column with the same name is registered
  private final Map<String, FunctionCall<AnalyticAggregateFunctions>> aggregateFunctions;
  private final Map<String, FunctionCall<AnalyticNumberingFunctions>> numberingFunctions;

  // Used to determine the order in which to add new columns.
  private final LinkedHashSet<String> newColumnNames;

  public ArgumentList(Map<String, FunctionCall<AnalyticAggregateFunctions>> aggregateFunctions, Map<String,
    FunctionCall<AnalyticNumberingFunctions>> numberingFunctions, LinkedHashSet<String> newColumnNames) {
    this.aggregateFunctions = aggregateFunctions;
    this.numberingFunctions = numberingFunctions;
    this.newColumnNames = newColumnNames;
  }

  static Builder builder() {
    return new Builder();
  }

  public Map<String, FunctionCall<AnalyticAggregateFunctions>> getAggregateFunctions() {
    return aggregateFunctions;
  }

  public Map<String, FunctionCall<AnalyticNumberingFunctions>> getNumberingFunctions() {
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
        Optional.ofNullable(numberingFunctions.get(newColName)).map(f -> f.toSqlString(windowName)).orElse("");
      String optionalAggregateCol =
        Optional.ofNullable(aggregateFunctions.get(newColName)).map(f -> f.toSqlString(windowName)).orElse("");
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

  /**
   * @return an ordered list of new columns this analytic query will generate.
   */
  List<Column<?>> createEmptyDestinationColumns(int rowCount) {
    List<Column<?>> newColumns = new ArrayList<>();
    for(String toColumn : newColumnNames) {
      FunctionCall<? extends AnalyticFunctionMetaData> functionCall = Stream.of(
        aggregateFunctions.get(toColumn),
        numberingFunctions.get(toColumn)
      ).filter(java.util.Objects::nonNull).findFirst().get();
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

  static class FunctionCall<T extends AnalyticFunctionMetaData> {
    private final String fromColumn;
    private final String toColumn;
    private final T function;

    public FunctionCall(String fromColumn, String toColumn, T function) {
      this.fromColumn = fromColumn;
      this.toColumn = toColumn;
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
      return function.toString() + '(' + fromColumn + ")" + over + " AS " + toColumn;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      FunctionCall<?> that = (FunctionCall<?>) o;
      return Objects.equal(fromColumn, that.fromColumn) &&
        Objects.equal(toColumn, that.toColumn) &&
        Objects.equal(function, that.function);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(fromColumn, toColumn, function);
    }
  }

  static class Builder {

    // maps new column to aggregate function.
    private final Map<String, FunctionCall<AnalyticAggregateFunctions>> aggregateFunctions = new HashMap<>();
    private final Map<String, FunctionCall<AnalyticNumberingFunctions>> numberingFunctions = new HashMap<>();

    // Throws if a column with the same name is registered twice.
    private final LinkedHashSet<String> newColumnNames = new LinkedHashSet<>();

    //Temporarily store analytic function data until the user calls as to give the new coumn a name
    // and save all the metadata.
    private String stagedFromColumn;
    private AnalyticAggregateFunctions stagedAggregateFunction;
    private AnalyticNumberingFunctions stagedNumberingFunction;

    private Builder() {
    }

    Builder stageFunction(String fromColumn, AnalyticAggregateFunctions function) {
      checkNothingStaged();
      Preconditions.checkNotNull(fromColumn);
      Preconditions.checkNotNull(function);
      this.stagedFromColumn = fromColumn;
      this.stagedAggregateFunction = function;
      return this;
    }

    Builder stageFunction(String fromColumn, AnalyticNumberingFunctions function) {
      checkNothingStaged();
      Preconditions.checkNotNull(fromColumn);
      Preconditions.checkNotNull(function);
      this.stagedFromColumn = fromColumn;
      this.stagedNumberingFunction = function;
      return this;
    }

    private void checkNothingStaged() {
      if (this.stagedFromColumn != null) {
        throw new IllegalArgumentException("Cannot stage a column while another is staged. Must call unstage first");
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
        this.numberingFunctions.put(toColumn, new FunctionCall<>(
            this.stagedFromColumn, toColumn, this.stagedNumberingFunction
          )
        );
      } else {
        Preconditions.checkNotNull(stagedAggregateFunction);
        this.aggregateFunctions.put(toColumn, new FunctionCall<>(
            this.stagedFromColumn, toColumn, this.stagedAggregateFunction
          )
        );
      }
      this.stagedNumberingFunction = null;
      this.stagedAggregateFunction = null;
      this.stagedFromColumn = null;

      return this;
    }

    ArgumentList build() {
      if (this.stagedFromColumn != null) {
        throw new IllegalArgumentException("Cannot build when a column is staged");
      }
      return new ArgumentList(
        aggregateFunctions,
        numberingFunctions,
        newColumnNames
      );
    }

  }
}
