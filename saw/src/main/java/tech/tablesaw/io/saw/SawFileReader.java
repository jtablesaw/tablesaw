package tech.tablesaw.io.saw;

import java.io.File;
import java.util.List;
import tech.tablesaw.api.Table;

public class SawFileReader extends SawReader {

  private final File sawFile;

  public SawFileReader(File sawFile) {
    super(TableMetadata.readTableMetadata(sawFile.toPath()));
    this.sawFile = sawFile;
  }

  @Override
  public Table read() {
    return readTable(sawFile, 10);
  }

  @Override
  public Table read(ReadOptions options) {
    return readTable(sawFile, options.getThreadPoolSize());
  }

  @Override
  public String shape() {
    return tableMetadata.shape();
  }

  @Override
  public int columnCount() {
    return tableMetadata.columnCount();
  }

  @Override
  public int rowCount() {
    return tableMetadata.getRowCount();
  }

  @Override
  public List<String> columnNames() {
    return tableMetadata.columnNames();
  }

  @Override
  public Table structure() {
    return tableMetadata.structure();
  }
}
