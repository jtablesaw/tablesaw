package tech.tablesaw.analytic;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.columns.Column;

enum AnalyticNumberingFunctions implements AnalyticFunctionMetaData {

  ROW_NUMBER(Implementations.rowNumber),
  RANK(Implementations.rank),
  DENSE_RANK(Implementations.denseRank);

  private final NumberingFunction implementation;

  AnalyticNumberingFunctions(NumberingFunction implementation) {
    this.implementation = implementation;
  }

  public NumberingFunction getImplementation() {
    return implementation;
  }

  public @Override
  String toString() {
    return name();
  }

  @Override
  public String functionName() {
    return name();
  }

  @Override
  public ColumnType returnType() {
    return ColumnType.INTEGER;
  }

  @Override
  public boolean isCompatibleColumn(ColumnType type) {
    // TODO. Hard code this list to functions that implement comparable and equals.
    return true;
  }

  /**
   * Implementations.
   */
  static class Implementations {

    static final NumberingFunction rowNumber = new NumberingFunction() {

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
      public NumericColumn<Integer> apply(Column<? extends Comparable<?>> inputWindow) {
        throw new UnsupportedOperationException();
      }
    };

    public static final NumberingFunction denseRank = new NumberingFunction() {

      @Override
      public NumericColumn<Integer> apply(Column<? extends Comparable<?>> inputWindow) {
        throw new UnsupportedOperationException();
      }
    };
  }
}
