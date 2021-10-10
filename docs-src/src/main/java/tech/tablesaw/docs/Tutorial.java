package tech.tablesaw.docs;

import static tech.tablesaw.aggregate.AggregateFunctions.count;
import static tech.tablesaw.aggregate.AggregateFunctions.mean;
import static tech.tablesaw.aggregate.AggregateFunctions.median;

import java.io.IOException;
import tech.tablesaw.aggregate.CrossTab;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.QuerySupport;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.docs.OutputWriter.System;

public class Tutorial implements DocsSourceFile {

  public static final OutputWriter outputWriter = new OutputWriter(Tutorial.class);

  public void run() throws IOException {
    // @@ table_read
    Table tornadoes = Table.read().csv("../data/tornadoes_1950-2014.csv");
    // @@ table_read

    outputWriter.write(
        // @@ table_columns
        tornadoes.columnNames()
        // @@ table_columns
        ,
        "table_columns");

    outputWriter.write(
        // @@ table_shape
        tornadoes.shape()
        // @@ table_shape
        ,
        "table_shape");

    outputWriter.write(
        // @@ table_structure
        tornadoes.structure().printAll()
        // @@ table_structure
        ,
        "table_structure");

    // @@ print_table
    System.out.println(tornadoes);
    // @@ print_table
    outputWriter.write(tornadoes, "print_table");

    outputWriter.write(
        // @@ filter_structure
        tornadoes
            .structure()
            .where(tornadoes.structure().stringColumn("Column Type").isEqualTo("DOUBLE"))
        // @@ filter_structure
        ,
        "filter_structure");

    outputWriter.write(
        // @@ first_n
        tornadoes.first(3)
        // @@ first_n
        ,
        "first_n");

    // @@ date_col
    StringColumn month = tornadoes.dateColumn("Date").month();
    // @@ date_col

    // @@ add_date_col
    tornadoes.addColumns(month);
    // @@ add_date_col

    // @@ remove_col
    tornadoes.removeColumns("State No");
    // @@ remove_col

    // @@ sort_on
    tornadoes.sortOn("-Fatalities");
    // @@ sort_on

    outputWriter.write(
        // @@ summary
        tornadoes.column("Fatalities").summary().print()
        // @@ summary
        ,
        "summary");

    // @@ filtering
    Table result = tornadoes.where(tornadoes.intColumn("Fatalities").isGreaterThan(0));
    result = tornadoes.where(result.dateColumn("Date").isInApril());
    result =
        tornadoes.where(
            result
                .intColumn("Width")
                .isGreaterThan(300) // 300 yards
                .or(result.doubleColumn("Length").isGreaterThan(10))); // 10 miles

    result = result.selectColumns("State", "Date");

    // @@ filtering
    outputWriter.write(result.first(3), "filtering");

    // @@ totals
    Table injuriesByScale = tornadoes.summarize("Injuries", median).by("Scale").sortOn("Scale");
    injuriesByScale.setName("Median injuries by Tornado Scale");
    // @@ totals
    outputWriter.write(injuriesByScale.first(10), "totals");

    outputWriter.write(
        // @@ crosstabs
        CrossTab.counts(tornadoes, tornadoes.stringColumn("State"), tornadoes.intColumn("Scale"))
            .first(10)
        // @@ crosstabs
        ,
        "crosstabs");

    // Putting it all togeather.

    // @@ all_together_where
    Table summer =
        tornadoes.where(
            QuerySupport.or(
                // In June
                QuerySupport.and(
                    t -> t.dateColumn("Date").month().isEqualTo("JUNE"),
                    t -> t.dateColumn("Date").dayOfMonth().isGreaterThanOrEqualTo(21)),
                // In July or August
                t -> t.dateColumn("Date").month().isIn("JULY", "AUGUST"),
                // In September
                QuerySupport.or(
                    t -> t.dateColumn("Date").month().isEqualTo("SEPTEMBER"),
                    t -> t.dateColumn("Date").dayOfMonth().isLessThan(22))));
    // @@ all_together_where

    // @@ all_together_lag
    summer = summer.sortAscendingOn("Date", "Time");
    summer.addColumns(summer.dateColumn("Date").lag(1));

    DateColumn summerDate = summer.dateColumn("Date");
    DateColumn laggedDate = summer.dateColumn("Date lag(1)");

    IntColumn delta = laggedDate.daysUntil(summerDate);
    summer.addColumns(delta);
    // @@ all_together_lag

    // @@ all_together_summarize
    Table summary = summer.summarize(delta, mean, count).by(summerDate.year());
    // @@ all_together_summarize
    outputWriter.write(summary.first(5), "all_together_summarize");

    outputWriter.write(
        // @@ all_together_single_col_summary
        summary.nCol(1).mean()
        // @@ all_together_single_col_summary
        ,
        "all_together_single_col_summary");

    // @@ write_csv
    tornadoes.write().csv("rev_tornadoes_1950-2014.csv");
    // @@ write_csv
  }
}
