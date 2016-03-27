package com.deathrayresearch.outlier.integration;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.View;
import com.deathrayresearch.outlier.columns.ColumnType;
import com.deathrayresearch.outlier.io.CsvReader;
import com.deathrayresearch.outlier.io.CsvWriter;
import org.junit.Test;

import static com.deathrayresearch.outlier.QueryUtil.valueOf;
import static com.deathrayresearch.outlier.columns.ColumnType.*;

/**
 * Some example code using the API
 */
public class ExamplesTest  {

  @Test
  public void simpleExample() throws Exception {

    print("");
    print("Some Examples: ");

    // Read the CSV file
    ColumnType[] types = {INTEGER, TEXT, CAT, FLOAT, FLOAT};
    Table table = CsvReader.read(types, "data/bus_stop_test.csv");

    // Look at the column names
    print(table.columnNames());

    // Peak at the data
    print(table.head(5).print());

    // Remove the description column
    table.removeColumns("stop_desc");

    // Check the column names to see that it's gone
    print(table.columnNames());

    // Take a look at some data
    print("In 'examples. Printing head(5)");
    print(table.head(5).print());

    // Lets take a look at the latitude and longitude columns
    // print(table.realColumn("stop_lat").rowSummary().print());
    print(table.floatColumn("stop_lat").describe());

    // Now lets fill a column based on data in the existing columns

    // Apply the map function and fill the resulting column to the original table

    // Lets filter out some of the rows. We're only interested in records with IDs between 524-624

    View filtered = table.select().where(valueOf("stop_id").isBetween(524, 624)).run();
    print(filtered.head(5).print());

    // Write out the new CSV file
    CsvWriter.write("data/filtered_bus_stops.csv", filtered);
  }

  private void print(Object o) {
    System.out.println(o);
  }
}
