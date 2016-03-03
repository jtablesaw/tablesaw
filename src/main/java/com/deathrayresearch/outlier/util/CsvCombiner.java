package com.deathrayresearch.outlier.util;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.deathrayresearch.outlier.io.CsvWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility that takes all CSV files in a folder and combines them into a single file
 */
public class CsvCombiner {

  public static void readAll(String foldername, String newFileName, char columnSeparator, boolean headers)
      throws IOException {

    FileWriter fileWriter = new FileWriter(newFileName);
    CSVWriter writer = new CSVWriter(fileWriter, ',');
    final boolean[] skipHeader = {false};
    Files.walk(Paths.get(foldername)).forEach(filePath -> {

      if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".csv")) {
        CsvCombiner.append(filePath.toString(), writer, columnSeparator, headers && skipHeader[0]);
        skipHeader[0] = true;
      }
    });
    writer.flush();
    writer.close();
  }

  public static void append(String fileName, final CSVWriter writer, char columnSeparator, boolean skipHeader) {

    CSVReader reader = null;
    try {
      reader = new CSVReader(new FileReader(fileName), columnSeparator);
      if (skipHeader) { // skip the header
        reader.readNext();
      }
      String[] nextLine;
      while ((nextLine = reader.readNext()) != null) {
        writer.writeNext(nextLine);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
