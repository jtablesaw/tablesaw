package com.github.lwhite1.tablesaw.testutil;

import java.io.File;


public class DirectoryUtils {

  public static boolean deleteDirectory(File directory) {
    if (directory.exists()) {
      File[] files = directory.listFiles();
      if (null != files) {
        for (int i = 0; i < files.length; i++) {
          if (files[i].isDirectory()) {
            deleteDirectory(files[i]);
          } else {
            files[i].delete();
          }
        }
      }
    }
    return (directory.delete());
  }

  public static long folderSize(File directory) {
    long length = 0;
    File[] files = directory.listFiles();
    if (files == null || files.length == 0) { return 0;}

    for (File file : files) {
      if (file.isFile()) {
        length += file.length();
      } else {
        length += folderSize(file);
      }
    }
    return length;
  }
}