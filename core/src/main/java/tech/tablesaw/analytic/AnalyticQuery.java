package tech.tablesaw.analytic;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import tech.tablesaw.analytic.AnalyticQuerySteps.AddAnalyticFunctionWithExecute;
import tech.tablesaw.analytic.AnalyticQuerySteps.AddAnalyticFunctions;
import tech.tablesaw.analytic.AnalyticQuerySteps.DefineWindow;
import tech.tablesaw.analytic.AnalyticQuerySteps.NameStep;
import tech.tablesaw.analytic.AnalyticQuerySteps.OrderStep;
import tech.tablesaw.analytic.AnalyticQuerySteps.PartitionStep;
import tech.tablesaw.analytic.AnalyticQuerySteps.StartStep;
import tech.tablesaw.analytic.AnalyticQuerySteps.WindowEndOptionOne;
import tech.tablesaw.analytic.AnalyticQuerySteps.WindowEndOptionTwo;
import tech.tablesaw.analytic.AnalyticQuerySteps.WindowStart;
import tech.tablesaw.analytic.WindowSpecification.OrderPair;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.sorting.Sort;

final public class AnalyticQuery {

  private final Table table;
  private final WindowSpecification windowSpecification;
  private final WindowFrame windowFrame;
  private final ArgumentList argumentList;
  private final List<Consumer<Iterable<Row>>> consumers;

  private AnalyticQuery(Table table, WindowSpecification windowSpecification, WindowFrame windowFrame,
    ArgumentList argumentList, List<Consumer<Iterable<Row>>> consumers) {
    this.table = table;
    this.windowSpecification = windowSpecification;
    this.windowFrame = windowFrame;
    this.argumentList = argumentList;
    this.consumers = consumers;
  }

  @Beta
  public static StartStep from(Table table) {
    return new Builder(table);
  }

  public enum Order {
    ASC,
    DESC;

    private Sort.Order getSortOrder() {
      switch (this) {
        case ASC:
          return Sort.Order.ASCEND;
        case DESC:
          return Sort.Order.DESCEND;
      }
      throw new RuntimeException("No Enum Match for " + this);
    }
  }

  public Table getTable() {
    return table;
  }

  public List<Consumer<Iterable<Row>>> getConsumers() {
    return consumers;
  }

  public ArgumentList getArgumentList() {
    return  argumentList;
  }

  public LinkedHashSet<String> getPartitionColumns() {
    return windowSpecification.getPartitionColumns();
  }

  public Optional<Sort> getSort() {
    List<OrderPair> ordering = windowSpecification.getOrdering();
    if (ordering.isEmpty()) {
      return Optional.empty();
    }

    OrderPair order = ordering.get(0);
    Sort sort = Sort.on(order.getColumnName(), order.getOrder().getSortOrder());

    for (int i = 1; i < ordering.size(); i++) {
      order = ordering.get(i);
      sort.next(order.getColumnName(), order.getOrder().getSortOrder());
    }
    return Optional.of(sort);
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
      sb.append("SELECT");
      sb.append(System.lineSeparator());
      sb.append(argumentList.toSqlString(windowSpecification.getWindowName()));
      sb.append(System.lineSeparator());
    }
    sb.append("FROM " + table.name());
    sb.append(System.lineSeparator());
    sb.append("Window " + windowSpecification.getWindowName() + " AS (");
    sb.append(System.lineSeparator());
    if (!windowSpecification.isEmpty()) {
      sb.append(windowSpecification.toSqlString());
      sb.append(System.lineSeparator());
    }
    sb.append(windowFrame.toSqlString());
    sb.append(");");
    return sb.toString();
  }

  @Override
  public String toString() {
    return toSqlString();
  }

  static class Builder implements StartStep, PartitionStep, OrderStep, DefineWindow,
    WindowStart, WindowEndOptionOne, WindowEndOptionTwo, NameStep, AddAnalyticFunctions,
    AddAnalyticFunctionWithExecute {

    private final Table table;
    private final WindowFrame.Builder frameBuilder = WindowFrame.builder();
    private final WindowSpecification.Builder windowSpecificationBuilder = WindowSpecification.builder();
    private final ArgumentList.Builder argumentsListBuilder = ArgumentList.builder();
    private final List<Consumer<Iterable<Row>>> consumers = new ArrayList<>();


    private Builder(Table table) {
      this.table = table;
    }

    @Override
    public NameStep rowNumber(String columnName) {
      argumentsListBuilder.stageFunction(columnName, AnalyticNumberingFunctions.ROW_NUMBER);
      return this;
    }

    @Override
    public NameStep rank(String columnName) {
      argumentsListBuilder.stageFunction(columnName, AnalyticNumberingFunctions.RANK);
      return this;
    }

    @Override
    public NameStep denseRank(String columnName) {
      argumentsListBuilder.stageFunction(columnName, AnalyticNumberingFunctions.DENSE_RANK);
      return this;
    }

    @Override
    public NameStep sum(String columnName) {
      argumentsListBuilder.stageFunction(columnName, AnalyticAggregateFunctions.SUM);
      return this;
    }

    @Override
    public NameStep mean(String columnName) {
      argumentsListBuilder.stageFunction(columnName, AnalyticAggregateFunctions.MEAN);
      return this;
    }

    @Override
    public NameStep max(String columnName) {
      argumentsListBuilder.stageFunction(columnName, AnalyticAggregateFunctions.MAX);
      return this;
    }

    @Override
    public NameStep min(String columnName) {
      argumentsListBuilder.stageFunction(columnName, AnalyticAggregateFunctions.MIN);
      return this;
    }

    @Override
    public NameStep count(String columnName) {
      argumentsListBuilder.stageFunction(columnName, AnalyticAggregateFunctions.COUNT);
      return this;
    }

    @Override
    public AddAnalyticFunctionWithExecute apply(Consumer<Iterable<Row>> consumer) {
      this.consumers.add(consumer);
      return this;
    }

    @Override
    public AddAnalyticFunctionWithExecute apply(Iterable<Consumer<Iterable<Row>>> consumers) {
      consumers.forEach(this.consumers::add);
      return this;
    }

    @Override
    public OrderStep partitionBy(String... columns) {
      this.windowSpecificationBuilder.setPartitionColumns(Arrays.asList(columns));
      return this;
    }

    @Override
    public DefineWindow orderBy(String column) {
      windowSpecificationBuilder.setOrderColumns(
        ImmutableList.of(OrderPair.of(column, Order.ASC)));
      return this;
    }

    @Override
    public DefineWindow orderBy(String column, Order strategy) {
      windowSpecificationBuilder.setOrderColumns(
        ImmutableList.of(OrderPair.of(column, strategy)));
      return this;
    }

    @Override
    public DefineWindow orderBy(String c1, Order s1, String c2, Order s2) {
      windowSpecificationBuilder.setOrderColumns(
        ImmutableList.of(
          OrderPair.of(c1, s1), OrderPair.of(c2, s2)));
      return this;
    }

    @Override
    public DefineWindow orderBy(String c1, Order s1, String c2, Order s2, String c3,
      Order s3) {
      windowSpecificationBuilder.setOrderColumns(ImmutableList.of(
        OrderPair.of(c1, s1),
        OrderPair.of(c2, s2),
        OrderPair.of(c3, s3)));
      return this;
    }

    @Override
    public DefineWindow orderBy(String c1, Order s1, String c2, Order s2, String c3,
      Order s3, String c4, Order s4) {
      windowSpecificationBuilder.setOrderColumns(
        ImmutableList.of(
          OrderPair.of(c1, s1),
          OrderPair.of(c2, s2),
          OrderPair.of(c3, s3),
          OrderPair.of(c4, s4)));
      return this;
    }

    @Override
    public DefineWindow orderBy(String c1, Order s1, String c2, Order s2, String c3,
      Order s3, String c4, Order s4, String c5, Order s5) {
      windowSpecificationBuilder.setOrderColumns(ImmutableList.of(OrderPair.of(c1, s1),
        OrderPair.of(c2, s2),
        OrderPair.of(c3, s3),
        OrderPair.of(c4, s4),
        OrderPair.of(c4, s5)));
      return this;
    }

    @Override
    public DefineWindow orderBy(OrderPair... orderPairs) {
      windowSpecificationBuilder.setOrderColumns(Arrays.asList(orderPairs));
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
    public AddAnalyticFunctions andPreceding(int nRows) {
      this.frameBuilder.setEndPreceding(nRows);
      return this;
    }

    @Override
    public AddAnalyticFunctions andCurrentRow() {
      this.frameBuilder.setEnndCurrentRow();
      return this;
    }

    @Override
    public AddAnalyticFunctions andFollowing(int nRows) {
      this.frameBuilder.setEndFollowing(nRows);
      return this;
    }

    @Override
    public AddAnalyticFunctions andUnBoundedFollowing() {
      // Default is unboundedFollowing
      return this;
    }

    @Override
    public AddAnalyticFunctionWithExecute as(String columnName) {
      argumentsListBuilder.unStageFunction(columnName);
      return this;
    }

    @Override
    public AnalyticQuery build() {
      return new AnalyticQuery(
        this.table,
        this.windowSpecificationBuilder.build(),
        this.frameBuilder.build(),
        this.argumentsListBuilder.build(),
        this.consumers
      );
    }

    @Override
    public Table execute() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void executeInPlace() {
      throw new UnsupportedOperationException();
    }
  }
}
