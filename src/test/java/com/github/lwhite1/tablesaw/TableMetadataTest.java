package com.github.lwhite1.tablesaw;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.api.FloatColumn;
import com.github.lwhite1.tablesaw.store.TableMetadata;
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
    table = Table.create("t");
    table.addColumn(column1);
    table.addColumn(column2);
  }

  @Test
  public void testToJson() {
    TableMetadata tableMetadata = new TableMetadata(table);
    assertEquals(tableMetadata, TableMetadata.fromJson(tableMetadata.toJson()));
  }
}