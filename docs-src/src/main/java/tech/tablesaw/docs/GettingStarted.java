package tech.tablesaw.docs;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.docs.OutputWriter.System;
import tech.tablesaw.selection.Selection;
// @@ static_boolean_operators_import
import static tech.tablesaw.api.QuerySupport.and;
import static tech.tablesaw.api.QuerySupport.or;
import static tech.tablesaw.api.QuerySupport.not;
// @@ static_boolean_operators_import
// @@ static_aggregate_function_import
// import aggregate functions.
import static tech.tablesaw.aggregate.AggregateFunctions.*;
// @@ static_aggregate_function_import

public class GettingStarted implements DocsSourceFile {

  public static final OutputWriter outputWriter = new OutputWriter(GettingStarted.class);

  @Override
  public void run() throws IOException {
    firstFewDocsSections();
    workingWithTablesColumns();
    workingwWithRows();
    sorting();
    filtering();
    summarizing();
    crossTab();
  }

  private void firstFewDocsSections() throws IOException {
    // @@ create_column
    double[] numbers = {1, 2, 3, 4};
    DoubleColumn nc = DoubleColumn.create("nc", numbers);
    System.out.println(nc.print());
    // @@ create_column
    outputWriter.write(nc.print(), "create_column");

    // @@ column_index
    double three = nc.get(2);
    // @@ column_index

    // @@ column_multiply
    DoubleColumn nc2 = nc.multiply(4);
    System.out.println(nc2.print());
    // @@ column_multiply
    outputWriter.write(nc2.print(), "column_multiply");

    // @@ selection_one
    nc.isLessThan(3);
    // @@ selection_one

    // @@ selection_two
    DoubleColumn filtered = nc.where(nc.isLessThan(3));
    // @@ selection_two
    outputWriter.write(filtered.print(), "selection_two");

    // @@ selection_three
    DoubleColumn filteredPositive = nc.where(nc.isLessThan(3).and(nc.isPositive()));
    // @@ selection_three

    // @@ selection_by_index
    nc.where(Selection.with(0, 2)); // returns 2 rows with the given indexes
    nc.where(Selection.withRange(1, 3)); // returns rows 1-3 inclusive
    // @@ selection_by_index

    // @@ selection_other_col
    StringColumn sc = StringColumn.create("sc", new String[] {"foo", "bar", "baz", "foobar"});
    DoubleColumn result = nc.where(sc.startsWith("foo"));
    // @@ selection_other_col

    // @@ multiply_two_columns
    DoubleColumn other = DoubleColumn.create("other", new Double[] {10.0, 20.0, 30.0, 40.0});
    DoubleColumn newColumn = nc2.multiply(other);
    System.out.println(newColumn.print());
    // @@ multiply_two_columns
    outputWriter.write(newColumn.print(), "multiply_two_columns");

    // @@ string_map_examples
    StringColumn s = StringColumn.create("sc", new String[] {"foo", "bar", "baz", "foobarbaz"});
    StringColumn s2 = s.copy();
    s2 = s2.replaceFirst("foo", "bar");
    s2 = s2.upperCase();
    s2 = s2.padEnd(5, 'x'); // put 4 x chars at the end of each string
    s2 = s2.substring(1, 5);

    // this returns a measure of the similarity (levenshtein distance) between two columns
    DoubleColumn distance = s.distance(s2);
    // @@ string_map_examples

    // @@ stdev
    double stdDev = nc.standardDeviation();
    // @@stdev

    // @@ tables
    String[] animals = {"bear", "cat", "giraffe"};
    double[] cuteness = {90.1, 84.3, 99.7};

    Table cuteAnimals =
        Table.create("Cute Animals")
            .addColumns(
                StringColumn.create("Animal types", animals),
                DoubleColumn.create("rating", cuteness));
    // @@ tables

    // @@ read_bush_table
    Table bushTable = Table.read().csv("../data/bush.csv");
    // @@ read_bush_table

    outputWriter.write(
        // @@ table_structure
        System.out.println(bushTable.structure())
        // @@ table_structure
        ,
        "table_structure");

    outputWriter.write(
        // @@ table_shape
        System.out.println(bushTable.shape())
        // @@ table_shape
        ,
        "table_shape");

    outputWriter.write(
        // @@ first_three
        System.out.println(bushTable.first(3))
        // @@ first_three
        ,
        "first_three");

    outputWriter.write(
        // @@ last_three
        System.out.println(bushTable.last(3))
        // @@ last_three
        ,
        "last_three");

    workingWithTablesColumns();
  }

  private void workingWithTablesColumns() {

    // Setup code.
    Table table = Table.create();
    StringColumn column1 = StringColumn.create("col1");
    StringColumn column2 = StringColumn.create("col2");
    StringColumn column3 = StringColumn.create("col3");

    try {
      // @@ working_with_columns_examples
      List<String> columnNames = table.columnNames(); // returns all column names
      List<Column<?>> columns = table.columns(); // returns all the columns in the table

      // removing columns
      table.removeColumns("Foo"); // keep everything but "foo"
      table.retainColumns("Foo", "Bar"); // only keep foo and bar
      table.removeColumnsWithMissingValues();

      // adding columns
      table.addColumns(column1, column2, column3);
      // @@ working_with_columns_examples

      // @@ get_column_case_insensitive
      table.column("FOO");
      table.column("foo");
      table.column("foO");
      // @@ get_column_case_insensitive

      // @@ get_column
      table.column("Foo"); // returns the column named 'Foo' if it's in the table.
      // or
      table.column(0); // returns the first column
      // @@ get_column

      // @@ get_column_cast
      StringColumn sc = (StringColumn) table.column(0);
      // @@ get_column_cast

      // @@ get_typed_columns
      StringColumn strings = table.stringColumn(0);
      DateColumn dates = table.dateColumn("start date");
      DoubleColumn doubles = table.doubleColumn("doubles");
      // @@ get_typed_columns

    } catch (Exception e) {
    }
  }

  private void workingwWithRows() {
    // Setup code.
    Table table = Table.create();
    Table destinationTable = Table.create();
    Table sourceTable = Table.create();
    try {
      // @@ row_wise_examples
      Table result = table.dropDuplicateRows();
      result = table.dropRowsWithMissingValues();

      // drop rows using Selections
      result = table.dropWhere(table.numberColumn(0).isLessThan(100));

      // add rows
      destinationTable.addRow(43, sourceTable); // adds row 43 from sourceTable to the receiver

      // sampling
      table.sampleN(200); // select 200 rows at random from table
      // @@ row_wise_examples

      // @@ for_loop
      for (Row row : table) {
        System.out.println("On " + row.getDate("date") + ": " + row.getDouble("approval"));
      }
      // @@ for_loop

      // @@ stream
      table.stream()
          .forEach(
              row -> {
                System.out.println("On " + row.getDate("date") + ": " + row.getDouble("approval"));
              });
      // @@ stream

      // @@ stepping_rolling_stream
      // Consumer prints out the max of a window.
      Consumer<Row[]> consumer =
          rows ->
              System.out.println(Arrays.stream(rows).mapToDouble(row -> row.getDouble(0)).max());

      // Streams over rolling sets of rows. I.e. 0 to n-1, 1 to n, 2 to n+1, etc.
      table.rollingStream(3).forEach(consumer);

      // Streams over stepped sets of rows. I.e. 0 to n-1, n to 2n-1, 2n to 3n-1, etc. Only returns
      // full sets of rows.
      table.steppingStream(5).forEach(consumer);
      // @@ stepping_rolling_stream
    } catch (Exception e) {
    }
  }

  private void sorting() {
    Table table = Table.create("table");
    try {
      // @@ sort
      Table sorted = table.sortOn("foo", "bar", "bam"); // Sorts Ascending by Default
      sorted = table.sortAscendingOn("bar"); // just like sortOn(), but makes the order explicit.
      sorted = table.sortDescendingOn("foo");

      // sort on foo ascending, then bar descending. Note the minus sign preceding the name of
      // column bar.
      sorted = table.sortOn("foo", "-bar");
      // @@ sort
    } catch (Exception e) {
    }
  }

  private void filtering() {
    Table table = Table.create("table");
    try {
      // @@ filtering
      Table result =
          table.where(
              and(
                  or(
                      t -> t.doubleColumn("nc1").isGreaterThan(4),
                      t -> t.doubleColumn("nc1").isNegative()
                      ),
                  not(t -> t.doubleColumn("nc2").isLessThanOrEqualTo(5))));
      // @@ filtering
    } catch (Exception e) {
    }
  }

  private void summarizing() {
    Table table = Table.create("table");

    try {
      // @@ summarize_basic
      Table summary = table.summarize("sales", mean, sum, min, max).by("province", "status");
      // @@ summarize_basic

      // @@ summarize_calculated_column
      summary = table.summarize("sales", mean, median)
          .by(table.dateColumn("sales date").dayOfWeek());
      // @@ summarize_calculated_column

    } catch (Exception e){}
  }

  private void crossTab() throws IOException {
    Table table = Table.read().csv("../data/bush.csv");
    StringColumn month = table.dateColumn("date").month();
    month.setName("month");
    table.addColumns(month);

    // @@ crosstab
    Table percents = table.xTabTablePercents("month", "who");
    // make table print as percents with no decimals instead of the raw doubles it holds
    percents.columnsOfType(ColumnType.DOUBLE)
        .forEach(x -> ((DoubleColumn)x).setPrintFormatter(NumberColumnFormatter.percent(0)));
    System.out.println(percents);
    // @@ crosstab
    outputWriter.write(percents, "crosstab");
  }
}