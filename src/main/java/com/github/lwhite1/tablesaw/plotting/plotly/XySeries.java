package com.github.lwhite1.tablesaw.plotting.plotly;

import com.github.lwhite1.tablesaw.api.DateColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;
import com.github.lwhite1.tablesaw.columns.Column;
import com.google.common.annotations.VisibleForTesting;

import static com.github.lwhite1.tablesaw.api.ColumnType.LOCAL_DATE;
import static com.github.lwhite1.tablesaw.api.ColumnType.SHORT_INT;

/**
 * Represents a single seriesList on an XY Plot
 */
public class XySeries<X extends Column, Y extends Column> {

  protected X x;
  protected Y y;
  protected String[] labels;
  protected String name;
  protected Mode mode = Mode.MARKERS;

  public XySeries(X x, Y y) {
    this.x = x;
    this.y = y;
  }

  public void name(String name) {
    this.name = name;
  }

  public String asString(int seriesNumber) {
    return new StringBuilder()
        .append("var ")
        .append("series")
        .append(seriesNumber)
        .append(" = {")
        .append("\n")

        .append("  x: [ ")
        .append(xAsString())
        .append(" ],")
        .append("\n")

        .append("  y: [ ")
        .append(yAsString())
        .append(" ],")
        .append("\n")

        .append("  mode: '")
        .append("line")  //TODO
        .append("',")
        .append("\n")

        .append("  type: '")
        .append("scatter")
        .append("'")
        .append("\n")

        .append("}")
        .append("\n")

        .toString();
  }

  private String yAsString() {
    if (y.type() == SHORT_INT) {
      return arrayStringBuilder((ShortColumn) y);
    }
    return arrayStringBuilder(y);
  }

  private String xAsString() {
    if (x.type() == LOCAL_DATE) {
      return arrayStringBuilder((DateColumn) x);
    }
    return arrayStringBuilder(x);
  }


  @VisibleForTesting
  static String arrayStringBuilder(IntColumn intColumn) {
    StringBuilder xValuesBuilder = new StringBuilder();
    for (int i = 0; i < intColumn.size(); i++) {
      int xVal = intColumn.get(i);
      xValuesBuilder.append(xVal);
      if (i < intColumn.size() -1) {
        xValuesBuilder.append(", ");
      }
    }
    return xValuesBuilder.toString();
  }

  @VisibleForTesting
  static String arrayStringBuilder(Column column) {
    throw new UnsupportedOperationException("Column type " + column.type() + " not supported for plotting");
  }

  @VisibleForTesting
  static String arrayStringBuilder(DateColumn dateColumn) {
    StringBuilder xValuesBuilder = new StringBuilder();
    for (int i = 0; i < dateColumn.size(); i++) {
      String xVal = dateColumn.getString(i);
      xValuesBuilder.append("'");
      xValuesBuilder.append(xVal);
      if (i < dateColumn.size() -1) {
        xValuesBuilder.append("', ");
      }
    }
    return xValuesBuilder.toString();
  }

  @VisibleForTesting
  static String arrayStringBuilder(ShortColumn intColumn) {
    StringBuilder xValuesBuilder = new StringBuilder();
    for (int i = 0; i < intColumn.size(); i++) {
      int xVal = intColumn.get(i);
      xValuesBuilder.append(xVal);
      if (i < intColumn.size() -1) {
        xValuesBuilder.append(", ");
      }
    }
    return xValuesBuilder.toString();
  }


  enum Mode {
    MARKERS,
    LINE,
    LINE_AND_MARKERS,
    Fill
  }
}

