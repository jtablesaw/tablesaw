package tech.tablesaw.io;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;

public class TableBuildingUtils {

  public static Table build(
      List<String> columnNames, List<String[]> dataRows, ReadOptions options) {
    Table table = Table.create(options.tableName());

    if (dataRows.isEmpty()) {
      return table;
    }

    ColumnTypeDetector detector = new ColumnTypeDetector(options.columnTypesToDetect());
    Iterator<String[]> iterator = dataRows.iterator();
    ColumnType[] types = detector.detectColumnTypes(iterator, options);

    // If there are columnTypes configured by the user use them
    for (int i = 0; i < types.length; i++) {
      boolean hasColumnName = i < columnNames.size();
      Optional<ColumnType> configuredColumnType =
          options.columnTypeReadOptions().columnType(i, hasColumnName ? columnNames.get(i) : null);
      if (configuredColumnType.isPresent()) types[i] = configuredColumnType.get();
    }

    for (int i = 0; i < columnNames.size(); i++) {
      table.addColumns(types[i].create(columnNames.get(i)));
    }

    for (int i = 0; i < dataRows.size(); i++) {
      for (int j = 0; j < table.columnCount(); j++) {
        table.column(j).appendCell(dataRows.get(i)[j]);
      }
    }

    return table;
  }
}
