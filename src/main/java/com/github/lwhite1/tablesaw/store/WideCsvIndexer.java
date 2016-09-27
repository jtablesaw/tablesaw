package com.github.lwhite1.tablesaw.store;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.io.csv.CsvReader;
import com.google.common.base.Stopwatch;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.*;

public final class WideCsvIndexer {
  private final CSVParserConfig params;
  private final String targetDir;
  private final Path storageDir;
  private final Stopwatch stopwatch = Stopwatch.createUnstarted();

  public WideCsvIndexer(CSVParserConfig params, String targetDir) {
    this.params = params;
    this.targetDir = targetDir;
    this.storageDir = Paths.get(targetDir);
  }

  public void saveInSawFormat() throws IOException {
    stopwatch.start();
    ColumnType[] columnTypes =
        CsvReader.detectColumnTypes(params.csvFile(), params.hasHeader(), params.fieldDelimiter());
    stopwatch.stop();
    System.out.println("Time taken to infer column types: " + stopwatch.toString());

    if (!Files.exists(storageDir)) {
      try {
        Files.createDirectories(storageDir);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    List<ColumnMetadata> cMetas = persistColumns(columnTypes);
    writeMetadataJson(cMetas);
  }

  private void writeMetadataJson(List<ColumnMetadata> cMetas) throws IOException {
    String tableName = Paths.get(params.csvFile()).getFileName().toString();
    int rowCount = cMetas.get(0).getSize();
    TableMetadata tableMetadata = new TableMetadata(tableName, rowCount, cMetas);
    String json = tableMetadata.toJson();
    Path path = Paths.get(targetDir).resolve(TableMetadata.fileName);
    Files.write(path, json.getBytes(StandardCharsets.UTF_8), CREATE, TRUNCATE_EXISTING, WRITE);
  }

  private List<ColumnMetadata> persistColumns(ColumnType[] columnTypes) throws IOException {
    ColumnType[] dummy = new ColumnType[columnTypes.length];
    Arrays.fill(dummy, ColumnType.SKIP);
    int columnBatch = params.columnBatch();

    List<ColumnMetadata> result = new ArrayList<>();

    for (int i = 0; i < columnTypes.length; i += columnBatch) {
      int len = Math.min(columnBatch, columnTypes.length - i);
      System.arraycopy(columnTypes, i, dummy, i, len);

      stopwatch.reset().start();
      Table singleColTable = CsvReader.read(dummy, params.hasHeader(), params.fieldDelimiter(), params.csvFile());
      stopwatch.stop();
      List<Column> columns = singleColTable.columns();
      List<String> colNames = columns.stream().map(Column::name).collect(Collectors.toList());
      System.out.printf("Time taken to read columns: [ %s ] ==> %s%n", colNames, stopwatch.toString());


      columns.forEach(column -> {
        Path path = Paths.get(targetDir, column.id());
        stopwatch.reset().start();
        SawWriter.writeColumn(path.toString(), column);
        stopwatch.stop();
        System.out.printf("Time taken to save column: [ %s ] ==> %s%n", column.name(), stopwatch.toString());
        result.add(column.columnMetadata());
      });

      int endIndex = Math.min(i + columnBatch, columnTypes.length);
      Arrays.fill(dummy, i, endIndex, ColumnType.SKIP);
    }

    return result;
  }

}
