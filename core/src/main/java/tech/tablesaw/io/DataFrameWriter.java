package tech.tablesaw.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvWriter;
import tech.tablesaw.io.html.HtmlTableWriter;

public class DataFrameWriter {

  private final Table table;

  public DataFrameWriter(Table table) {
      this.table = table;
  }

  public void csv(String file) throws IOException {
      CsvWriter.write(table, file);
  }

  public void csv(File file) throws IOException {
      CsvWriter.write(table, file);
  }

  public void csv(OutputStream stream) throws IOException {
    CsvWriter.write(table, stream);
  }

  public void csv(Writer writer) throws IOException {
    CsvWriter.write(table, writer);
  }

  public void html(OutputStream stream) throws IOException {
      HtmlTableWriter.write(table, stream);
  }

}
