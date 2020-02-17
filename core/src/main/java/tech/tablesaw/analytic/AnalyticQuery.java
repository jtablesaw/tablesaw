package tech.tablesaw.analytic;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import tech.tablesaw.analytic.AnalyticQuerySteps.AddAggregateFunctions;
import tech.tablesaw.analytic.AnalyticQuerySteps.AddAggregateFunctionsWithExecute;
import tech.tablesaw.analytic.AnalyticQuerySteps.AddNumberingFunction;
import tech.tablesaw.analytic.AnalyticQuerySteps.AddNumberingFunctionWithExecute;
import tech.tablesaw.analytic.AnalyticQuerySteps.DefineWindowFame;
import tech.tablesaw.analytic.AnalyticQuerySteps.FullAnalyticQuerySteps;
import tech.tablesaw.analytic.AnalyticQuerySteps.FullAnalyticQuerySteps.OrderByOptionalStep;
import tech.tablesaw.analytic.AnalyticQuerySteps.NameStepAggregate;
import tech.tablesaw.analytic.AnalyticQuerySteps.NameStepNumbering;
import tech.tablesaw.analytic.AnalyticQuerySteps.NumberingQuerySteps;
import tech.tablesaw.analytic.AnalyticQuerySteps.NumberingQuerySteps.OrderByRequiredStep;
import tech.tablesaw.analytic.AnalyticQuerySteps.NumberingQuerySteps.PartitionByStep;
import tech.tablesaw.analytic.AnalyticQuerySteps.QuickQuerySteps;
import tech.tablesaw.analytic.AnalyticQuerySteps.SetWindowEndOptionOne;
import tech.tablesaw.analytic.AnalyticQuerySteps.SetWindowEndOptionTwo;
import tech.tablesaw.analytic.AnalyticQuerySteps.SetWindowStart;
import tech.tablesaw.api.Table;
import tech.tablesaw.sorting.Sort;

/** A class representing an analytic query similar to the Over or Window clause in SQL. */
public final class AnalyticQuery {

  private final Table table;
  private final WindowSpecification windowSpecification;
  private final WindowFrame windowFrame;
  private final ArgumentList argumentList;

  private AnalyticQuery(
      Table table,
      WindowSpecification windowSpecification,
      WindowFrame windowFrame,
      ArgumentList argumentList) {
    this.table = table;
    this.windowSpecification = windowSpecification;
    this.windowFrame = windowFrame;
    this.argumentList = argumentList;
  }

  /**
   * Entry point for the fluent analytic query builder. Order By and Partition By are optional.
   *
   * <p>An AnalyticQuery performs a calculation across a set of table rows that are somehow related
   * to the current row.
   *
   * <pre>
   * Query includes steps for:
   * FROM table_name
   * [ PARTITION BY co1, col2... ]
   * [ ORDER BY col1, col2...]
   * window_frame_clause {@link DefineWindowFame}
   * argument_list {@link AnalyticQuerySteps.AnalyticFunctions}
   * </pre>
   *
   * @return a fluent analytic query builder.
   */
  @Beta
  public static FullAnalyticQuerySteps.FromStep query() {
    return new FullQueryBuilder();
  }

  /**
   * Entry point for the fluent Numbering Query Builder.
   *
   * <p>A numbering assigns integer values to each row based on their position within the specified
   * partition. Numbering queries require Order By.
   *
   * <pre>
   * Query includes steps for:
   * FROM table_name
   * [ PARTITION BY col1, col2...]
   * ORDER BY
   * argument_list {@link AnalyticQuerySteps.NumberingFunctions}
   * </pre>
   *
   * @return a fluent numbering query builder.
   */
  @Beta
  public static NumberingQuerySteps.FromStep numberingQuery() {
    return new NumberingQueryBuilder();
  }

  /**
   * Entry point for the fluent Analytic Query Builder. Same as the {@link AnalyticQuery#query()}
   * but skips the ORDER BY and PARTITION BY steps.
   *
   * <pre>
   * Query includes steps for:
   * FROM table_name
   * window_frame_clause {@link DefineWindowFame}
   * argument_list {@link AnalyticQuerySteps.AnalyticFunctions}
   * </pre>
   *
   * @return a fluent analytic query builder that will skip the PartitionBy and OrderBy steps.
   */
  @Beta
  public static QuickQuerySteps.FromStep quickQuery() {
    return new QuickQueryBuilder();
  }

  /**
   * The Table behind the query.
   *
   * @return the underlying {@link Table} behind this query.
   */
  public Table getTable() {
    return table;
  }

  ArgumentList getArgumentList() {
    return argumentList;
  }

  Set<String> getPartitionColumns() {
    return windowSpecification.getPartitionColumns();
  }

  Optional<Sort> getSort() {
    return windowSpecification.getSort();
  }

  WindowSpecification getWindowSpecification() {
    return windowSpecification;
  }

  WindowFrame getWindowFrame() {
    return windowFrame;
  }

  /**
   * Creates a SQL like string for documentation purposes. The returned SQL is not meant be executed
   * in SQL database.
   *
   * @return a SQL like string explaining the query.
   */
  public String toSqlLikeString() {
    StringBuilder sb = new StringBuilder();
    if (!argumentList.getNewColumnNames().isEmpty()) {
      sb.append("SELECT")
          .append(System.lineSeparator())
          .append(argumentList.toSqlString(windowSpecification.getWindowName()))
          .append(System.lineSeparator());
    }
    sb.append("FROM ")
        .append(table.name())
        .append(System.lineSeparator())
        .append("Window ")
        .append(windowSpecification.getWindowName())
        .append(" AS (")
        .append(System.lineSeparator());
    if (!windowSpecification.isEmpty()) {
      sb.append(windowSpecification.toSqlString());
    }
    if (windowFrame != null) {
      if (!windowSpecification.isEmpty()) {
        sb.append(System.lineSeparator());
      }
      sb.append(windowFrame.toSqlString());
    }
    sb.append(");");
    return sb.toString();
  }

  @Override
  public String toString() {
    return toSqlLikeString();
  }

  /**
   * Executes the query adding all the calculated columns to a new table. The result columns will
   * have the same order as the from table.
   *
   * @return a new table containing only the result columns.
   */
  public Table execute() {
    return AnalyticQueryEngine.create(this).execute();
  }

  /**
   * Executes the query and adds all the calculated columns directly to the source table.
   *
   * @throws IllegalArgumentException if any of the calculated columns have the same name as one of
   *     the columns in the FROM table.
   */
  public void executeInPlace() {
    Table result = execute();
    table.concat(result);
  }

  static class NumberingQueryBuilder
      implements NumberingQuerySteps.FromStep,
          OrderByRequiredStep,
          PartitionByStep,
          AnalyticQuerySteps.AddNumberingFunction,
          AddNumberingFunctionWithExecute,
          NameStepNumbering {
    private Table table;
    private final WindowSpecification.Builder windowSpecificationBuilder =
        WindowSpecification.builder();
    private final ArgumentList.Builder argumentsListBuilder = ArgumentList.builder();

    @Override
    public PartitionByStep from(Table table) {
      this.table = table;
      return this;
    }

    @Override
    public OrderByRequiredStep partitionBy(String... columnNames) {
      this.windowSpecificationBuilder.setPartitionColumns(Arrays.asList(columnNames));
      return this;
    }

    @Override
    public AddNumberingFunction orderBy(String columnName, String... columnNames) {
      String[] cols = new String[columnNames.length + 1];
      cols[0] = columnName;
      System.arraycopy(columnNames, 0, cols, 1, columnNames.length);
      windowSpecificationBuilder.setSort(Sort.create(this.table, cols));
      return this;
    }

    @Override
    public AddNumberingFunctionWithExecute as(String columnName) {
      argumentsListBuilder.unStageFunction(columnName);
      return this;
    }

    @Override
    public NameStepNumbering rowNumber() {
      argumentsListBuilder.stageFunction(NumberingFunctions.ROW_NUMBER);
      return this;
    }

    @Override
    public NameStepNumbering rank() {
      argumentsListBuilder.stageFunction(NumberingFunctions.RANK);
      return this;
    }

    @Override
    public NameStepNumbering denseRank() {
      argumentsListBuilder.stageFunction(NumberingFunctions.DENSE_RANK);
      return this;
    }

    @Override
    public AnalyticQuery build() {
      return new AnalyticQuery(
          this.table,
          this.windowSpecificationBuilder.build(),
          null,
          this.argumentsListBuilder.build());
    }

    @Override
    public Table execute() {
      return this.build().execute();
    }

    @Override
    public void executeInPlace() {
      this.build().executeInPlace();
    }
  }

  abstract static class AnalyticBuilder
      implements OrderByOptionalStep,
          FullAnalyticQuerySteps.PartitionByStep,
          DefineWindowFame,
          SetWindowStart,
          SetWindowEndOptionOne,
          SetWindowEndOptionTwo,
          NameStepAggregate,
          AddAggregateFunctions,
          AddAggregateFunctionsWithExecute {
    private Table table;
    private final WindowFrame.Builder frameBuilder = WindowFrame.builder();
    private final WindowSpecification.Builder windowSpecificationBuilder =
        WindowSpecification.builder();
    private final ArgumentList.Builder argumentsListBuilder = ArgumentList.builder();

    @Override
    public NameStepAggregate sum(String columnName) {
      argumentsListBuilder.stageFunction(columnName, AggregateFunctions.SUM);
      return this;
    }

    @Override
    public NameStepAggregate mean(String columnName) {
      argumentsListBuilder.stageFunction(columnName, AggregateFunctions.MEAN);
      return this;
    }

    @Override
    public NameStepAggregate max(String columnName) {
      argumentsListBuilder.stageFunction(columnName, AggregateFunctions.MAX);
      return this;
    }

    @Override
    public NameStepAggregate min(String columnName) {
      argumentsListBuilder.stageFunction(columnName, AggregateFunctions.MIN);
      return this;
    }

    @Override
    public NameStepAggregate count(String columnName) {
      argumentsListBuilder.stageFunction(columnName, AggregateFunctions.COUNT);
      return this;
    }

    @Override
    public OrderByOptionalStep partitionBy(String... columns) {
      this.windowSpecificationBuilder.setPartitionColumns(Arrays.asList(columns));
      return this;
    }

    @Override
    public DefineWindowFame orderBy(String... columnNames) {
      windowSpecificationBuilder.setSort(Sort.create(this.table, columnNames));
      return this;
    }

    @Override
    public SetWindowStart rowsBetween() {
      return this;
    }

    @Override
    public SetWindowEndOptionOne unboundedPreceding() {
      // default is unbounded preceding.
      return this;
    }

    @Override
    public SetWindowEndOptionOne preceding(int nRows) {
      this.frameBuilder.setLeftPreceding(nRows);
      return this;
    }

    @Override
    public SetWindowEndOptionTwo currentRow() {
      this.frameBuilder.setLeftCurrentRow();
      return this;
    }

    @Override
    public SetWindowEndOptionTwo following(int nRows) {
      this.frameBuilder.setLeftFollowing(nRows);
      return this;
    }

    @Override
    public AddAggregateFunctions andPreceding(int nRows) {
      this.frameBuilder.setRightPreceding(nRows);
      return this;
    }

    @Override
    public AddAggregateFunctions andCurrentRow() {
      this.frameBuilder.setRightCurrentRow();
      return this;
    }

    @Override
    public AddAggregateFunctions andFollowing(int nRows) {
      this.frameBuilder.setRightFollowing(nRows);
      return this;
    }

    @Override
    public AddAggregateFunctions andUnBoundedFollowing() {
      // Default is unboundedFollowing
      return this;
    }

    @Override
    public AddAggregateFunctionsWithExecute as(String columnName) {
      argumentsListBuilder.unStageFunction(columnName);
      return this;
    }

    @Override
    public AnalyticQuery build() {
      Preconditions.checkNotNull(table);
      return new AnalyticQuery(
          this.table,
          this.windowSpecificationBuilder.build(),
          this.frameBuilder.build(),
          this.argumentsListBuilder.build());
    }

    @Override
    public Table execute() {
      return this.build().execute();
    }

    @Override
    public void executeInPlace() {
      this.build().executeInPlace();
    }
  }

  static class FullQueryBuilder extends AnalyticBuilder implements FullAnalyticQuerySteps.FromStep {

    @Override
    public FullAnalyticQuerySteps.PartitionByStep from(Table table) {
      super.table = table;
      return this;
    }
  }

  static class QuickQueryBuilder extends AnalyticBuilder implements QuickQuerySteps.FromStep {

    @Override
    public DefineWindowFame from(Table table) {
      super.table = table;
      return this;
    }
  }
}
