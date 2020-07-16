package tech.tablesaw.io.saw;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides a unified Reader/Writer interface to the Saw storage system, modeled on the
 * DataFrameReader and DataFrameWriter interface. Perhaps it should be part of that system, but as
 * we have complete control over the data format it's quite different in its goals.
 */
public class SawTable {

  public static SawFileReader file(String path) {
    Path sawPath = Paths.get(path);
    return file(sawPath.toFile());
  }

  public static SawFileReader file(File file) {
    return new SawFileReader(file);
  }

  public static SawUrlReader url(URL url) {
    return new SawUrlReader(url);
  }

  public static SawUrlReader url(String url) throws MalformedURLException {
    return url(new URL(url));
  }

  public static SawWriter file() {
    return new SawWriter();
  }
}
