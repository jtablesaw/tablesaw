package com.github.lwhite1.tablesaw.columns.packeddata;

import com.github.lwhite1.tablesaw.columns.DateIntervalColumn;

/**
 *
 */
public abstract class PackedDateInterval {

  // boolean operations
  abstract boolean equals(DateIntervalColumn interval);
  abstract boolean before(DateIntervalColumn interval);
  abstract boolean after(DateIntervalColumn interval);

}
