package tech.tablesaw.io.saw;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import tech.tablesaw.api.Table;

/**
 * Provides a unified Reader/Writer interface to the Saw storage system, modeled on the
 * DataFrameReader and DataFrameWriter interface. Perhaps it should be part of that system, but as
 * we have complete control over the data format it's quite different in its goals.
 */
@Beta
public class SawTable {

  public static Table read(String path) {
    return read(path, ReadOptions.defaultOptions());
  }

  public static Table read(String path, ReadOptions options) {
    Path sawPath = Paths.get(path);
    return read(sawPath, options);
  }

  public static Table read(File file) {
    return read(file, ReadOptions.defaultOptions());
  }

  public static Table read(File file, ReadOptions options) {
    return read(file.toPath(), options);
  }

  /**
   * Reads a tablesaw table into memory
   *
   * @param sawPath The location of the table data. If not fully specified, it is interpreted as
   *     relative to the working directory. The path will typically end in ".saw", as in
   *     "mytables/nasdaq-2015.saw"
   */
  public static Table read(Path sawPath) {
    return new SawReader(sawPath, ReadOptions.defaultOptions()).read();
  }

  /**
   * Reads a tablesaw table into memory
   *
   * @param sawPath The location of the table data. If not fully specified, it is interpreted as
   *     relative to the working directory. The path will typically end in ".saw", as in
   *     "mytables/nasdaq-2015.saw"
   * @param readOptions Options that determine how the data should be read
   */
  public static Table read(Path sawPath, ReadOptions readOptions) {
    return new SawReader(sawPath, readOptions).read();
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
    return new SawWriter(folderPath, table, options).write();
  }
}
