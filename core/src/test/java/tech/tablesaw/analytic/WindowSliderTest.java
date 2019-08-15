package tech.tablesaw.analytic;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;

class WindowSliderTest {

  private Table source;

  @BeforeEach
  public void setUp() throws Exception {
    // Contains columns calculated by a SQL engine that our implementation should match exactly.
    source = Table.read().csv("../data/bush_sql_partitionby_who_orderby_date_sum_approval.csv");
  }

  private Table getFox() {
    return source.where(source.stringColumn("who").isEqualTo("fox"));
  }

  private double[] getSourceColumn(String columnName) {
    return getFox().intColumn(columnName).asDoubleArray();
  }

  @Test
  public void testSliding() {
    WindowFrame windowFrame = WindowFrame.builder()
      .setStartPreceding(5)
      .setEndPreceding(3)
      .build();

    DoubleColumn destination = DoubleColumn.create("sum", getFox().rowCount());

    WindowSlider slider = new WindowSlider();
    slider.windowFrame = windowFrame;
    slider.function = new AnalyticAggregateFunctions.SumFunctions().slidingFunction();
    slider.table = getFox();
    slider.sourceColumnName = "approval";
    slider.destination = destination;

    slider.process();

    Table result = Table.create();
    result.addColumns(slider.destination);

    double[] expected = getSourceColumn("5preceding_and_3preceding");
    double[] actual = destination.asDoubleArray();

    assertArrayEquals(expected, actual);
  }

  @Test
  public void testFixedEnd() {
    WindowFrame windowFrame = WindowFrame.builder()
      .setStartPreceding(5)
      .build();

    DoubleColumn destination = DoubleColumn.create("sum", getFox().rowCount());

    WindowSlider slider = new WindowSlider();
    slider.windowFrame = windowFrame;
    slider.function = new AnalyticAggregateFunctions.SumFunctions().slidingFunction();
    slider.table = getFox();
    slider.sourceColumnName = "approval";
    slider.destination = destination;

    slider.process();

    Table result = Table.create();
    result.addColumns(slider.destination);

    double[] expected = getSourceColumn("5preceding_and_unboundedfollowing");
    double[] actual = destination.asDoubleArray();

    assertArrayEquals(expected, actual);
  }
}