package tech.tablesaw.io.saw;

import java.net.URL;
import java.util.List;
import tech.tablesaw.api.Table;

public class SawUrlReader extends SawReader {

  private final URL url;

  public SawUrlReader(URL url) {
    super(null);
    this.url = url;
  }

  @Override
  public Table read() {
    return null;
  }

  @Override
  public Table read(ReadOptions options) {
    return null;
  }

  @Override
  public String shape() {
    return null;
  }

  @Override
  public Table structure() {
    return null;
  }

  @Override
  public int columnCount() {
    return 0;
  }

  @Override
  public int rowCount() {
    return 0;
  }

  @Override
  public List<String> columnNames() {
    return null;
  }
}
