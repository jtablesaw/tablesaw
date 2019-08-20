package tech.tablesaw.analytic;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import tech.tablesaw.analytic.AnalyticQuerySteps.AddAggregateFunctions;
import tech.tablesaw.analytic.AnalyticQuerySteps.AddAggregateFunctionsWithExecute;
import tech.tablesaw.analytic.AnalyticQuerySteps.AddNumberingFunction;
import tech.tablesaw.analytic.AnalyticQuerySteps.AddNumberingFunctionWithExecute;
import tech.tablesaw.analytic.AnalyticQuerySteps.DefineWindow;
import tech.tablesaw.analytic.AnalyticQuerySteps.FullAnalyticQuerySteps;
import tech.tablesaw.analytic.AnalyticQuerySteps.NameStepAggregate;
import tech.tablesaw.analytic.AnalyticQuerySteps.NameStepNumbering;
import tech.tablesaw.analytic.AnalyticQuerySteps.NumberingQuerySteps;
import tech.tablesaw.analytic.AnalyticQuerySteps.NumberingQuerySteps.OrderRequiredStep;
import tech.tablesaw.analytic.AnalyticQuerySteps.NumberingQuerySteps.PartitionStep;
import tech.tablesaw.analytic.AnalyticQuerySteps.QuickQuerySteps;
import tech.tablesaw.analytic.AnalyticQuerySteps.WindowEndOptionOne;
import tech.tablesaw.analytic.AnalyticQuerySteps.WindowEndOptionTwo;
import tech.tablesaw.analytic.AnalyticQuerySteps.WindowStart;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.sorting.Sort;

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
   * Entry point for Full Analytic Query.
   *
   * @return Full Analytic Query Builder.
   */
  @Beta
  public static FullAnalyticQuerySteps.FromStep query() {
    return new FullQueryBuilder();
  }

  /**
   * Entry point for Numbering Query.
   *
   * @return Numbering Analytic Query Builder.
   */
  @Beta
  public static NumberingQuerySteps.FromStep numberingQuery() {
    return new NumberingQueryBuilder();
  }

  /**
   * Entry point for Quick Analytic Query.
   *
   * @return Quick Analytic Query Builder.
   */
  @Beta
  public static QuickQuerySteps.FromStep quickQuery() {
    return new QuickQueryBuilder();
  }

  public Table getTable() {
    return table;
  }

  public ArgumentList getArgumentList() {
    return argumentList;
  }

  public LinkedHashSet<String> getPartitionColumns() {
    return windowSpecification.getPartitionColumns();
  }

  public Optional<Sort> getSort() {
    return windowSpecification.getSort();
  }

  public WindowSpecification getWindowSpecification() {
    return windowSpecification;
  }

  public WindowFrame getWindowFrame() {
    return windowFrame;
  }

  public String toSqlString() {
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
    return toSqlString();
  }

  public Table execute() {
    return AnalyticQueryEngine.create(this).execute();
  }

  static class NumberingQueryBuilder
      implements NumberingQuerySteps.FromStep,
          NumberingQuerySteps.OrderRequiredStep,
          NumberingQuerySteps.PartitionStep,
          AnalyticQuerySteps.AddNumberingFunction,
          AddNumberingFunctionWithExecute,
          NameStepNumbering {
    private Table table;
    private final WindowFrame.Builder frameBuilder = WindowFrame.builder();
    private final WindowSpecification.Builder windowSpecificationBuilder =
        WindowSpecification.builder();
    private final ArgumentList.Builder argumentsListBuilder = ArgumentList.builder();
    private final List<Consumer<Iterable<Row>>> consumers = new ArrayList<>();

    @Override
    public PartitionStep from(Table table) {
      this.table = table;
      return this;
    }

    @Override
    public OrderRequiredStep partitionBy(String... columnNames) {
      this.windowSpecificationBuilder.setPartitionColumns(Arrays.asList(columnNames));
      return this;
    }

    @Override
    public AddNumberingFunction orderBy(String columnOne, String... rest) {
      String[] cols = new String[rest.length + 1];
      cols[0] = columnOne;
      System.arraycopy(rest, 0, cols, 1, rest.length);
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
      throw new UnsupportedOperationException();
    }
  }

  abstract static class AnalyticBuilder
      implements FullAnalyticQuerySteps.OrderOptionalStep,
          FullAnalyticQuerySteps.PartitionStep,
          DefineWindow,
          WindowStart,
          WindowEndOptionOne,
          WindowEndOptionTwo,
          NameStepAggregate,
          AddAggregateFunctions,
          AddAggregateFunctionsWithExecute {
    private Table table;
    private final WindowFrame.Builder frameBuilder = WindowFrame.builder();
    private final WindowSpecification.Builder windowSpecificationBuilder =
        WindowSpecification.builder();
    private final ArgumentList.Builder argumentsListBuilder = ArgumentList.builder();
    private final List<Consumer<Iterable<Row>>> consumers = new ArrayList<>();

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
    public FullAnalyticQuerySteps.OrderOptionalStep partitionBy(String... columns) {
      this.windowSpecificationBuilder.setPartitionColumns(Arrays.asList(columns));
      return this;
    }

    @Override
    public DefineWindow orderBy(String... columnNames) {
      windowSpecificationBuilder.setSort(Sort.create(this.table, columnNames));
      return this;
    }

    @Override
    public WindowStart rowsBetween() {
      return this;
    }

    @Override
    public WindowEndOptionOne unboundedPreceding() {
      // default is unbounded preceding.
      return this;
    }

    @Override
    public WindowEndOptionOne preceding(int nRows) {
      this.frameBuilder.setStartPreceding(nRows);
      return this;
    }

    @Override
    public WindowEndOptionTwo currentRow() {
      this.frameBuilder.setStartCurrentRow();
      return this;
    }

    @Override
    public WindowEndOptionTwo following(int nRows) {
      this.frameBuilder.setStartFollowing(nRows);
      return this;
    }

    @Override
    public AddAggregateFunctions andPreceding(int nRows) {
      this.frameBuilder.setEndPreceding(nRows);
      return this;
    }

    @Override
    public AddAggregateFunctions andCurrentRow() {
      this.frameBuilder.setEnndCurrentRow();
      return this;
    }

    @Override
    public AddAggregateFunctions andFollowing(int nRows) {
      this.frameBuilder.setEndFollowing(nRows);
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
      throw new UnsupportedOperationException();
    }
  }

  static class FullQueryBuilder extends AnalyticBuilder implements FullAnalyticQuerySteps.FromStep {

    @Override
    public FullAnalyticQuerySteps.PartitionStep from(Table table) {
      super.table = table;
      return this;
    }
  }

  static class QuickQueryBuilder extends AnalyticBuilder implements QuickQuerySteps.FromStep {

    @Override
    public DefineWindow from(Table table) {
      super.table = table;
      return this;
    }
  }
}
