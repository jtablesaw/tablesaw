package tech.tablesaw.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.csv.CsvReadOptions.CsvReadOptionsBuilder;
import tech.tablesaw.io.jdbc.SqlResultSetReader;
import tech.tablesaw.io.csv.CsvReader;

public class DataFrameReader {

  public Table csv(String file) throws IOException {
    return csv(CsvReadOptions.builder(file));
  }

  public Table csv(File file) throws IOException {
    return csv(CsvReadOptions.builder(file));
  }

  public Table csv(InputStream stream, String tableName) throws IOException {
    return csv(CsvReadOptions.builder(stream, tableName));
  }

  public Table csv(CsvReadOptionsBuilder options) throws IOException {
    return csv(options.build());
  }

  public Table csv(CsvReadOptions options) throws IOException {
    return CsvReader.read(options);
  }

  public static Table db(ResultSet resultSet, String tableName) throws SQLException {
    return SqlResultSetReader.read(resultSet, tableName);
  }

}
