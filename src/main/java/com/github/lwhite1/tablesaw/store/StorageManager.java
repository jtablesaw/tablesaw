package com.github.lwhite1.tablesaw.store;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.table.Relation;

import java.io.IOException;

/**
 * A controller for reading and writing data in Tablesaw's own compressed, column-oriented file format
 */
public class StorageManager {
  /**
   * Reads a tablesaw table into memory
   *
   * @param path The location of the table. It is interpreted as relative to the working directory if not fully
   *             specified. The path will typically end in ".saw", as in "mytables/nasdaq-2015.saw"
   * @throws IOException if the file cannot be read
   */
  public static Table readTable(String path) throws IOException {
    return SawReader.readTable(path);
  }

  /**
   * Saves the data from the given table in the location specified by folderName. Within that folder each table has
   * its own sub-folder, whose name is based on the name of the table.
   * <p>
   * NOTE: If you store a table with the same name in the same folder. The data in that folder will be over-written.
   * <p>
   * The storage format is the tablesaw compressed column-oriented format, which consists of a set of file in a folder.
   * The name of the folder is based on the name of the table.
   *
   * @param folderName The location of the table (for example: "mytables")
   * @param table      The table to be saved
   * @return The path and name of the table
   * @throws IOException
   */
  public static String saveTable(String folderName, Relation table) throws IOException {
    return SawWriter.saveTable(folderName, table);
  }

}
