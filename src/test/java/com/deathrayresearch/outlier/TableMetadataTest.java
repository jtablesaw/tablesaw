package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.FloatColumn;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class TableMetadataTest {

  private Table table;
  private Column column1 = new FloatColumn("f1");
  private Column column2 = new FloatColumn("i1");

  @Before
  public void setUp() throws Exception {
    table = new Table("t");
    table.addColumn(column1);
    table.addColumn(column2);
  }


  @Test
  public void testToJson() {
    TableMetadata tableMetadata = new TableMetadata(table);
    assertEquals(tableMetadata, TableMetadata.fromJson(tableMetadata.toJson()));
  }
}