package com.github.lwhite1.tablesaw.api.plot.swing;

import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.reducing.NumericSummaryTable;
import com.github.lwhite1.tablesaw.reducing.functions.SummaryFunction;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.SwingWrapper;

/**
 *
 */
@Deprecated
public class Bar {

  /**
   * Displays a JFrame containing a bar plot of observations counts grouped by the given CategoryColumn
   */
  public static void show(Table table, CategoryColumn column) {
    show(table, column, 600, 800);
  }

  public static void show(SummaryFunction summary, String ... columnNames) {
    show(summary.by(columnNames), summary, 600, 800);
  }

  public static void show(NumericSummaryTable summary) {
    show(summary, 600, 800);
  }

  /**
   * Displays a JFrame of the given width and height, and containing a bar plot of observations counts
   * grouped by the given CategoryColumn
   */
  public static void show(Table table, CategoryColumn column, int width, int height) {

    Table counts = table.countBy(column);

    CategoryChart chart =
        new CategoryChartBuilder()
            .width(width)
            .height(height)
            .title("Counts by " + column.name())
            .xAxisTitle(column.name())
            .yAxisTitle("Count").build();

    chart.getStyler().setTheme(new TablesawTheme());
    chart.addSeries("counts",
        counts.categoryColumn(0).toList(),
        counts.intColumn(1).data());

    new SwingWrapper<>(chart).displayChart();
  }

  public static void show(Table table, SummaryFunction summaryFunction, int width, int height) {

    CategoryChart chart =
        new CategoryChartBuilder()
            .width(width)
            .height(height)
            .title(summaryFunction.summarizedColumnName() + " by categories")
            .xAxisTitle("Categories")
            .yAxisTitle(summaryFunction.function().functionName()).build();

    chart.getStyler().setTheme(new TablesawTheme());
    chart.addSeries(summaryFunction.function().functionName(),
        table.categoryColumn(0).toList(),
        table.floatColumn(1).data());

    new SwingWrapper<>(chart).displayChart();
  }

  public static void show(NumericSummaryTable table, int width, int height) {

    CategoryChart chart =
        new CategoryChartBuilder()
            .width(width)
            .height(height)
            .title(table.name())
            .xAxisTitle("Categories")
            .yAxisTitle(table.column(1).name())
            .build();

    chart.getStyler().setTheme(new TablesawTheme());
    chart.addSeries(table.name(),
        table.categoryColumn(0).toList(),
        table.floatColumn(1).data());

    new SwingWrapper<>(chart).displayChart();
  }
}
