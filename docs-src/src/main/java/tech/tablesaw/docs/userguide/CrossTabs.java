package tech.tablesaw.docs.userguide;

import java.io.IOException;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.docs.DocsSourceFile;
import tech.tablesaw.docs.OutputWriter;
import tech.tablesaw.docs.OutputWriter.System;

public class CrossTabs implements DocsSourceFile {

  public static final OutputWriter outputWriter = new OutputWriter(CrossTabs.class);

  @Override
  public void run() throws IOException {

    // @@ intro_block
    // preparation: load the data, and add a string column to hold the months in the date col
    Table table = Table.read().csv("../data/bush.csv");
    StringColumn month = table.dateColumn("date").month();
    month.setName("month");
    table.addColumns(month);

    // perform the crossTab operation
    Table counts = table.xTabCounts("month", "who");
    System.out.println(counts);
    // @@ intro_block

    outputWriter.write(counts, "intro_block");

    // @@ who_counts
    Table whoCounts = table.xTabCounts("who");
    // @@ who_counts
    outputWriter.write(whoCounts, "who_counts");

    // @@ who_percents
    Table whoPercents = table.xTabPercents("who");
    // @@ who_percents

    // @@ who_percents_format
    whoPercents
        .columnsOfType(ColumnType.DOUBLE) // format to display as percents
        .forEach(x -> ((NumberColumn) x).setPrintFormatter(NumberColumnFormatter.percent(0)));
    // @@ who_percents_format
    outputWriter.write(whoPercents, "who_percents_format");

    // @@ table_percents
    Table tablePercents = table.xTabTablePercents("month", "who");
    tablePercents
        .columnsOfType(ColumnType.DOUBLE)
        .forEach(x -> ((NumberColumn) x).setPrintFormatter(NumberColumnFormatter.percent(1)));
    // @@ table_percents
    outputWriter.write(tablePercents, "table_percents");

    // @@ column_percents
    Table columnPercents = table.xTabColumnPercents("month", "who");
    // @@ column_percents
    columnPercents
        .columnsOfType(ColumnType.DOUBLE)
        .forEach(x -> ((NumberColumn) x).setPrintFormatter(NumberColumnFormatter.percent(0)));

    outputWriter.write(columnPercents, "column_percents");

    // @@ row_percents
    Table rowPercents = table.xTabRowPercents("month", "who");
    // @@ row_percents
    rowPercents
        .columnsOfType(ColumnType.DOUBLE)
        .forEach(x -> ((NumberColumn) x).setPrintFormatter(NumberColumnFormatter.percent(0)));
    outputWriter.write(rowPercents, "row_percents");
  }
}
