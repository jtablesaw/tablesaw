package tech.tablesaw.examples;

import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

/**
 * Implementation of example from
 * https://medium.com/@thijser/doing-cool-data-science-in-java-how-3-dataframe-libraries-stack-up-5e6ccb7b437
 *
 * <p>This is currently the top result if you google java dataframe. It demonstrates
 * missing-variable handling, filtering, string manipulation, pivoting, mapping, sorting, etc.
 */
public class PopulationExample {

  public static void main(String[] args) throws Exception {

    Table data =
        Table.read()
            .csv(
                CsvReadOptions.builderFromFile("../data/urb_cpop1_1_Data.csv")
                    .missingValueIndicator(":"));
    Table filtered = data.dropWhere(data.column("Value").isMissing());
    filtered.addColumns(
        (filtered.stringColumn("Cities").join(":", filtered.column("INDIC_UR"))).setName("key"));
    Table cities = filtered.pivot("key", "time", "value", AggregateFunctions.mean);

    // Top 10 cities by pop in 2017:
    System.out.println(
        cities
            .where(
                cities
                    .stringColumn("key")
                    .containsString("January")
                    .and(cities.stringColumn("key").containsString("total"))
                    .and(cities.nCol("2017").isNotMissing()))
            .sortDescendingOn("2017")
            .first(10));

    // Highest growth cities:
    cities.addColumns(
        (cities.nCol("2016").divide(cities.nCol("2010").subtract(1)).multiply(100))
            .setName("growth"));
    System.out.println(
        cities
            .where(
                cities
                    .stringColumn("key")
                    .containsString("January")
                    .and(cities.stringColumn("key").containsString("total"))
                    .and(cities.nCol("growth").isNotMissing()))
            .sortDescendingOn("growth")
            .first(10));
  }
}
