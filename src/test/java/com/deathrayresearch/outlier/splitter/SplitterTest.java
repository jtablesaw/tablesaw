package com.deathrayresearch.outlier.splitter;

import com.deathrayresearch.outlier.columns.LocalDateColumn;
import com.deathrayresearch.outlier.columns.LocalDateColumnGroup;
import com.deathrayresearch.outlier.columns.PackedLocalDate;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class SplitterTest {

  LocalDateSplitter monthSplitter = new LocalDateSplitter() {

    @Override
    public String groupKey(LocalDate date) {
      return groupKey(PackedLocalDate.pack(date));
    }

    @Override
    public String groupKey(int packedLocalDate) {
      return PackedLocalDate.getMonth(packedLocalDate).toString();
    }
  };

  @Test
  public void testLocalDateSplit() throws Exception {
    LocalDateColumn column = LocalDateColumn.create("dates");
    column.add(LocalDate.now());
    column.add(LocalDate.now().plusMonths(1));
    column.add(LocalDate.now().plusMonths(1));
    column.add(LocalDate.now().plusMonths(2));

    LocalDateColumnGroup group = new LocalDateColumnGroup(column, monthSplitter);
    List<LocalDateColumn> columns = group.getSubColumns();
    assertEquals(3, columns.size());
    assertEquals(3, group.groups());
    assertEquals(4, group.size());
  }
}