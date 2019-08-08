package tech.tablesaw.analytic;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.columns.Column;

/**
 * Implementations of analytic numbering functions.
 *
 *
 * Numbering functions assign integer values to each row based on their position within the specified window.
 *
 * // TODO nulls first when ASC, nulls last when desc
 */
class NumberingFunctionsImplementations {

  public static final NumberingFunction rowNumber = new NumberingFunction() {

    @Override
    public String functionName() {
      return "rowNumber";
    }

    @Override
    public boolean isCompatibleColumn(ColumnType type) {
      return true;
    }

    @Override
    public NumericColumn<Integer> apply(Column<? extends Comparable<?>> inputWindow) {
      IntColumn destination = IntColumn.create("destination", inputWindow.size());
      for (int i = 0; i < inputWindow.size(); i++) {
        destination.set(i, i + 1);
      }
      return destination;
    }
  };

  public static final NumberingFunction rank = new NumberingFunction() {

    @Override
    public String functionName() {
      return "rank";
    }

    @Override
    public boolean isCompatibleColumn(ColumnType type) {
      return true;
    }

    @Override
    public NumericColumn<Integer> apply(Column<? extends Comparable<?>> inputWindow) {
      throw new UnsupportedOperationException();
    }
  };

  public static final NumberingFunction denseRank = new NumberingFunction() {

    @Override
    public String functionName() {
      return "denseRank";
    }

    @Override
    public boolean isCompatibleColumn(ColumnType type) {
      return true;
    }

    @Override
    public NumericColumn<Integer> apply(Column<? extends Comparable<?>> inputWindow) {
      throw new UnsupportedOperationException();
    }
  };
}
