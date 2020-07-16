package tech.tablesaw.io.saw;

import java.nio.file.Path;
import tech.tablesaw.api.Table;

class SawFileWriter extends SawWriter {

  public SawFileWriter(Path path, Table table, WriteOptions options) {
    super(path, table, options);
  }
}
