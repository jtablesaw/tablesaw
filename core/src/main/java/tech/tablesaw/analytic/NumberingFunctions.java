package tech.tablesaw.analytic;

import java.util.function.Supplier;
import tech.tablesaw.api.ColumnType;

/**
 * Analytic numbering functions give every value in the window a number based on some ordering.
 *
 * <p>Rank denseRank and rowNumber are examples of analytic mapping functions.
 */
enum NumberingFunctions implements FunctionMetaData {
  ROW_NUMBER(Implementations::rowNumber),
  RANK(Implementations::rank),
  DENSE_RANK(Implementations::denseRank);

  private final Supplier<NumberingFunction> supplier;

  NumberingFunctions(Supplier<NumberingFunction> supplier) {
    this.supplier = supplier;
  }

  public NumberingFunction getImplementation() {
    return supplier.get();
  }

  public @Override String toString() {
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
    // True for call tables that can be ordered.
    return true;
  }

  /** Implementations. */
  static class Implementations {

    static NumberingFunction rowNumber() {

      return new NumberingFunction() {
        private int count = 0;

        @Override
        void addEqualRow() {
          count++;
        }

        @Override
        void addNextRow() {
          count++;
        }

        @Override
        int getValue() {
          return count;
        }
      };
    }

    static NumberingFunction denseRank() {
      return new NumberingFunction() {
        private int rank = 0;

        @Override
        void addNextRow() {
          rank++;
        }

        @Override
        void addEqualRow() {}

        @Override
        int getValue() {
          return rank;
        }
      };
    }

    static NumberingFunction rank() {

      return new NumberingFunction() {
        private int rank = 0;
        private int numInPrevRank = 1;

        @Override
        void addEqualRow() {
          numInPrevRank++;
        }

        @Override
        void addNextRow() {
          rank = rank + numInPrevRank;
          numInPrevRank = 1;
        }

        @Override
        int getValue() {
          return rank;
        }
      };
    }
  }
}
