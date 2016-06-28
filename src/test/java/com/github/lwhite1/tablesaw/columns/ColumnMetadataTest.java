package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.api.FloatColumn;
import com.github.lwhite1.tablesaw.store.ColumnMetadata;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class ColumnMetadataTest {

  private final Column d = new FloatColumn("Float col1");

  @Test
  public void testToFromJson() {
    String meta = d.metadata();
    ColumnMetadata d2 = ColumnMetadata.fromJson(meta);
    assertEquals(d2, ColumnMetadata.fromJson(meta));
  }
}