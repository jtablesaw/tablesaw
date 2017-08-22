package tech.tablesaw.io;

import java.io.IOException;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvWriter;

public class DataFrameWriter {

  private final Table table;

  public DataFrameWriter(Table table) {
      this.table = table;
  }

  public void csv(String file) throws IOException {
    CsvWriter.write(file, table);
  }

}
