package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.columns.FloatColumn;
import com.deathrayresearch.outlier.columns.IntColumn;
import com.deathrayresearch.outlier.io.CsvWriter;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class ViewTest {

  /**
   * Repeatedly applies filters to a view, reducing the number of records, and prints the row count.
   */
  @Test
  public void testRowCount() {
    int lastRowCount;
    Relation t = new Table("Test");
    FloatColumn c = new FloatColumn("fc");
    t.addColumn(c);
    for (int i = 0; i < 10_000_000; i++) {
      c.add((float) Math.random());
    }
    View v = new View(t, c.name());
    lastRowCount = v.rowCount();
    System.out.println(lastRowCount);

    v.where(c.isLessThan(.5f));
    assertTrue(v.rowCount() < lastRowCount && v.rowCount() > 0);
    lastRowCount = v.rowCount();
    System.out.println(lastRowCount);

    v.where(c.isGreaterThan(.25f));
    assertTrue(v.rowCount() < lastRowCount && v.rowCount() > 0);
    lastRowCount = v.rowCount();
    System.out.println(lastRowCount);

    v.where(c.isLessThanOrEqualTo(.4f));
    assertTrue(v.rowCount() < lastRowCount && v.rowCount() > 0);
    lastRowCount = v.rowCount();
    System.out.println(lastRowCount);

    v.where(c.isGreaterThanOrEqualTo(.33f));
    assertTrue(v.rowCount() < lastRowCount && v.rowCount() > 0);
    lastRowCount = v.rowCount();
    System.out.println(lastRowCount);

    v.where(c.isLessThanOrEqualTo(.36f));
    assertTrue(v.rowCount() < lastRowCount && v.rowCount() > 0);
    lastRowCount = v.rowCount();
    System.out.println(lastRowCount);

    v.where(c.isGreaterThanOrEqualTo(.35222f));
    assertTrue(v.rowCount() < lastRowCount && v.rowCount() > 0);
    lastRowCount = v.rowCount();
    System.out.println(lastRowCount);

    v.print();
  }

  @Test
  public void testPrint() {
    Relation t = new Table("Test");
    FloatColumn c = new FloatColumn("fc");
    t.addColumn(c);
    IntColumn c1 = new IntColumn("ic1");
    t.addColumn(c1);

    for (int i = 0; i < 100; i++) {
      c.add((float) Math.random());
      c1.add(i);
    }
    View v = new View(t, c1.name());
    System.out.print(v.print());
    System.out.println();

    View v1 = v.where(c1.isLessThan(50));
    System.out.print(v1.print());
  }

  @Test
  public void testWriteAsCsv() throws Exception {
    Relation t = new Table("Test");
    FloatColumn c = new FloatColumn("fc");
    t.addColumn(c);
    IntColumn c1 = new IntColumn("ic1");
    t.addColumn(c1);

    for (int i = 0; i < 100; i++) {
      c.add((float) Math.random());
      c1.add(i);
    }
    View v = new View(t, c.name(), c1.name());
    CsvWriter.write("testfolder/v.csv", v);
    System.out.println(v.print());
    v.where(c.isLessThan(.50f));
    System.out.println(v.print());
    CsvWriter.write("testfolder/v1.csv", v);
  }

  @Test
  public void testStructure() {
    Relation t = new Table("Test");
    FloatColumn c = new FloatColumn("fc");
    t.addColumn(c);
    IntColumn ic1 = new IntColumn("ic1");
    t.addColumn(ic1);
    for (int i = 0; i < 1_000; i++) {
      c.add((float) Math.random());
      ic1.add(i);
    }
    View v = new View(t, ic1.name());
    System.out.println(v.structure().print());
  }
}