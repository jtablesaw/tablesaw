package com.deathrayresearch.outlier.splitter;

import java.time.LocalDate;

/**
 *
 */
public interface LocalDateSplitter {

  /**
   * When applied to a record in a table, returns a String used to group records
   */
  String groupKey(LocalDate date);


  String groupKey(int packedLocalDate);

}
