package tech.tablesaw.examples;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;

public class CrossTabsExample {

  public static void main(String[] args) throws Exception {

    Table table = Table.read().csv("../data/bush.csv");
    StringColumn month = table.dateColumn("date").month();
    table.addColumns(month.setName("month"));

    // two variable counts
    Table counts = table.xTabCounts("month", "who");

    // make table print as integers with no decimals instead of the raw doubles it holds
    counts
        .columnsOfType(ColumnType.DOUBLE)
        .forEach(x -> ((NumberColumn<?, ?>) x).setPrintFormatter(NumberColumnFormatter.ints()));

    // single variable counts
    Table whoCounts = table.xTabCounts("who");
    whoCounts
        .columnsOfType(ColumnType.DOUBLE)
        .forEach(x -> ((NumberColumn<?, ?>) x).setPrintFormatter(NumberColumnFormatter.ints()));

    // single variable percents
    Table whoPercents = table.xTabPercents("who");
    whoPercents
        .columnsOfType(ColumnType.DOUBLE)
        .forEach(x -> ((NumberColumn<?, ?>) x).setPrintFormatter(NumberColumnFormatter.percent(0)));

    // table percents
    Table tablePercents = table.xTabTablePercents("month", "who");
    tablePercents
        .columnsOfType(ColumnType.DOUBLE)
        .forEach(x -> ((NumberColumn<?, ?>) x).setPrintFormatter(NumberColumnFormatter.percent(1)));

    // column percents
    Table columnPercents = table.xTabColumnPercents("month", "who");
    columnPercents
        .columnsOfType(ColumnType.DOUBLE)
        .forEach(x -> ((NumberColumn<?, ?>) x).setPrintFormatter(NumberColumnFormatter.percent(0)));

    // row percents
    Table rowPercents = table.xTabRowPercents("month", "who");
    rowPercents
        .columnsOfType(ColumnType.DOUBLE)
        .forEach(x -> ((NumberColumn<?, ?>) x).setPrintFormatter(NumberColumnFormatter.percent(0)));
  }
}
