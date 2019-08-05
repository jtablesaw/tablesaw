package tech.tablesaw.examples;

import static tech.tablesaw.aggregate.AggregateFunctions.mean;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

public class PivotTableExample {

  public static void main(String[] args) throws Exception {

    Table table =
        Table.read()
            .csv(
                CsvReadOptions.builder("../data/urb_cpop1_1_Data.csv")
                    .missingValueIndicator(":")
                    .build());

    Table filtered = table.dropWhere(table.intColumn("value").isMissing());

    StringColumn key = filtered.stringColumn("CITIES").join(":", filtered.stringColumn("INDIC_UR"));
    key.setName("key");
    filtered.addColumns(key);

    Table finalTable = filtered.pivot("key", "TIME", "Value", mean);
    // sortDescendingOn puts N/A values first unfortunately, so let's remove them before determining
    // and printing.
    Table existing2017 = finalTable.dropWhere(finalTable.column("2017").isMissing());

    System.out.println(
        existing2017
            .where(existing2017.stringColumn("key").endsWith("January, total"))
            .sortDescendingOn("2017")
            .print(20));

    // Add growth column
    DoubleColumn growthColumn =
        finalTable
            .doubleColumn("2016")
            .divide(finalTable.doubleColumn("2010"))
            .subtract(1)
            .multiply(100); // .subtract(1).multiply(100));

    growthColumn.setName("growth");
    finalTable.addColumns(growthColumn);

    Table temp = finalTable.dropWhere(finalTable.column("growth").isMissing());
    Table highestGrowthTable =
        temp.where(temp.stringColumn("key").endsWith("January, total")).sortDescendingOn("growth");

    System.out.println(highestGrowthTable.print(20));
  }
}
