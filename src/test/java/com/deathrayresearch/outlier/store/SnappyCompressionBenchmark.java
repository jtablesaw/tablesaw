package com.deathrayresearch.outlier.store;

import com.deathrayresearch.outlier.*;
import com.deathrayresearch.outlier.testutil.DirectoryUtils;
import com.deathrayresearch.outlier.testutil.NanoBench;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 */
public class SnappyCompressionBenchmark {

  @Test
  public void testFloat() {

    File TEST_FOLDER = Paths.get("testfolder").toFile();
    Relation t = new Table("Test");
    final FloatColumn c = new FloatColumn("fc");
    t.addColumn(c);

    Path path = Paths.get("testfolder");
    if (!Files.exists(path)) {
      try {
        Files.createDirectories(path);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }


    NanoBench nanoBench = NanoBench.create();
    nanoBench.warmUps(5).measurements(20).cpuAndMemory().measure("Compression",

        new Runnable() {
          @Override
          public void run() {
            try {
/*
              for (int i = 0; i < 1_000_000; i++) {
                c.add((float) Math.random());
              }
*/

              StorageManager.writeColumn(TEST_FOLDER + File.separator + "foo", c);
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        });
    System.out.println("Compressed size: " + DirectoryUtils.folderSize(TEST_FOLDER));
  }

  @Test
  public void testInt() {

    File TEST_FOLDER = Paths.get("testfolder").toFile();
    Relation t = new Table("Test");
    final IntColumn c = new IntColumn("fc");
    t.addColumn(c);
    RandomDataGenerator randomDataGenerator = new RandomDataGenerator();

    for (int i = 0; i < 1_000_000; i++) {
      c.add(randomDataGenerator.nextInt(0, 1_000_000));
    }


    Path path = Paths.get("testfolder");
    if (!Files.exists(path)) {
      try {
        Files.createDirectories(path);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    NanoBench nanoBench = NanoBench.create();

    nanoBench.warmUps(5).measurements(20).cpuAndMemory().measure("Compression",
        new Runnable() {
          @Override
          public void run() {
            try {
              StorageManager.writeColumn(TEST_FOLDER + File.separator + "foo", c);
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        });
    System.out.println("Compressed size: " + DirectoryUtils.folderSize(TEST_FOLDER));
  }
}
