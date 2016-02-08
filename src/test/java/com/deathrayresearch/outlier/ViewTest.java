package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.columns.FloatColumn;
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
    for (int i = 0; i < 1_000_000; i++) {
      c.add((float) Math.random());
    }
    View v = new View(t, c.name());
    View v1 = v.where(c.isLessThan(.5f));
    v1.print();
  }
}