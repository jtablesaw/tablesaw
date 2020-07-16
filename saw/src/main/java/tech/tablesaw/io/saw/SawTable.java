package tech.tablesaw.io.saw;

import com.google.common.base.Preconditions;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import tech.tablesaw.api.Table;

/**
 * Provides a unified Reader/Writer interface to the Saw storage system, modeled on the
 * DataFrameReader and DataFrameWriter interface. Perhaps it should be part of that system, but as
 * we have complete control over the data format it's quite different in its goals.
 */
public class SawTable {

  public static SawFileReader readFile(String path) {
    Path sawPath = Paths.get(path);
    return readFile(sawPath.toFile());
  }

  public static SawFileReader readFile(File file) {
    return new SawFileReader(file);
  }

  public static SawUrlReader readUrl(URL url) {
    return new SawUrlReader(url);
  }

  public static SawUrlReader readUrl(String url) throws MalformedURLException {
    return readUrl(new URL(url));
  }

  public static String write(String parentFolderName, Table table) {

    Preconditions.checkArgument(
        parentFolderName != null, "The folder name for the saw output cannot be null");
    Preconditions.checkArgument(
        !parentFolderName.isEmpty(), "The folder name for the saw output cannot be empty");

    // creates the containing folder
    Path folderPath = Paths.get(parentFolderName);
    return write(folderPath, table, new WriteOptions());
  }

  public static String write(Path folderPath, Table table, WriteOptions options) {
    return new SawFileWriter(folderPath, table, options).write();
  }
}
