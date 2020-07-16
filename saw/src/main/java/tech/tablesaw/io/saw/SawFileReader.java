package tech.tablesaw.io.saw;

import java.io.File;
import tech.tablesaw.api.Table;

public class SawFileReader extends SawReader {

  private final File sawFile;

  public SawFileReader(File sawFile) {
    super(TableMetadata.readTableMetadata(sawFile.toPath()));
    this.sawFile = sawFile;
  }

  @Override
  public Table read() {
    return readTable(sawFile, new ReadOptions());
  }

  @Override
  public Table read(ReadOptions options) {
    return readTable(sawFile, options);
  }
}
