package tech.tablesaw.io.saw;

import java.net.URL;
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
}
