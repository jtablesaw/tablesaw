package tech.tablesaw.analytic;

import tech.tablesaw.api.Table;

public interface AnalyticQuerySteps {

  interface FullAnalyticQuerySteps {

    interface FromStep {
      /**
       * Set the From/Source Table name to use in the query.
       *
       * @param table to query.
       * @return the Partition By step in the fluent analytic query builder.
       */
      PartitionByStep from(Table table);
    }

    interface PartitionByStep {
      /**
       * Set the partition columns. The query will partition the table into slices using the given
       * column names.
       *
       * @param columnNames the column names to partition the table by. If no column names are
       *     supplied the query will skip this step.
       * @return the Order By step in the fluent query builder.
       */
      OrderByOptionalStep partitionBy(String... columnNames);
    }

    interface OrderByOptionalStep {
      /**
       * Set the Order by Columns. The query will order each partition by the given column names,
       * applied in order.
       *
       * <p>if column name starts with - then sort that column descending otherwise sort ascending
       *
       * @param columnNames column names to order by. If no column names are supplied the query will
       *     skip this step.
       * @return the define window frame step in the fluent analytic query builder.
       */
      DefineWindowFame orderBy(String... columnNames);
    }
  }

  interface NumberingQuerySteps {

    interface FromStep {
      /**
       * Set the From/Source Table name to use in the query.
       *
       * @param table to query.
       * @return the Partition By Step in the fluent analytic query builder.
       */
      PartitionByStep from(Table table);
    }

    interface PartitionByStep {
      /**
       * Set the partition columns. The query will partition the table into slices using the given
       * column names.
       *
       * @param columnNames the column names to partition the table by. If no column names are
       *     supplied the query will skip this step.
       * @return the Order By step in the fluent query builder.
       */
      OrderByRequiredStep partitionBy(String... columnNames);
    }

    interface OrderByRequiredStep {

      /**
       * Set the Order by Columns. The query will order each partition by the given column names,
       * applied in order.
       *
       * <p>if column name starts with - then sort that column descending otherwise sort ascending
       *
       * @param columnName the first column name to sort by. Required.
       * @param columnNames other column names to sort by. Optional.
       * @return the add numbering functions step in the fluent query builder.
       */
      AddNumberingFunction orderBy(String columnName, String... columnNames);
    }
  }

  interface QuickQuerySteps {
    interface FromStep {
      /**
       * Set the From/Source Table name to use in the query.
       *
       * @param table to query.
       * @return the define window frame step in the fluent analytic query builder.
       */
      DefineWindowFame from(Table table);
    }
  }

  /**
   * An AnalyticFunction performs a calculation across a set of table rows that are somehow related
   * to the current row. The related rows are defined by the window frame clause. See {@link
   * DefineWindowFame} .
   *
   * <p>Tablesaw only supports a subset of the analytic functions you might find in a SQL database.
   * If you would like to see a new numbering function implemented please file an issue on GitHub.
   */
  interface AnalyticFunctions {
    NameStepAggregate sum(String columnName);

    NameStepAggregate mean(String columnName);

    NameStepAggregate max(String columnName);

    NameStepAggregate min(String columnName);

    NameStepAggregate count(String columnName);
  }

  /**
   * Numbering functions assign integer values to each row based on their position within the
   * specified partition. Numbering functions require OrderBy.
   *
   * <p>Tablesaw only supports a subset of the numbering functions you might find in a SQL database.
   * If you would like to see a new numbering function implemented please file an issue on GitHub.
   */
  interface NumberingFunctions {
    /**
     * Calculates the sequential row ordinal (1-based) of each row for each ordered partition.
     *
     * @return the name step in the fluent analytic query builder.
     */
    NameStepNumbering rowNumber();

    /**
     * Calculates the ordinal (1-based) rank of each row within the ordered partition. All peer rows
     * receive the same rank value. The next row or set of peer rows receives a rank value which
     * increments by the number of peers with the previous rank value, instead of {@link
     * NumberingFunctions#denseRank()} , which always increments by 1.
     *
     * @return the name step in the fluent analytic query builder.
     */
    NameStepNumbering rank();

    /**
     * Calculates the ordinal (1-based) rank of each row within the window partition. All peer rows
     * receive the same rank value, and the subsequent rank value is incremented by one.
     *
     * @return the name step in the fluent analytic query builder.
     */
    NameStepNumbering denseRank();
  }

  /**
   * First step in defining a window frame. The window frame clause here matches a window frame
   * clause in SQL.
   *
   * <p>SQL Grammar for the window frame clause:
   *
   * <pre>
   * { ROWS }
   * {
   *   { UNBOUNDED PRECEDING | numeric_expression PRECEDING | CURRENT ROW }
   *   |
   *   { BETWEEN window_frame_boundary_start AND window_frame_boundary_end }
   * }
   *
   * window_frame_boundary_start:
   * { UNBOUNDED PRECEDING | numeric_expression { PRECEDING | FOLLOWING } | CURRENT ROW }
   *
   * window_frame_boundary_end:
   * { UNBOUNDED FOLLOWING | numeric_expression { PRECEDING | FOLLOWING } | CURRENT ROW }
   * </pre>
   */
  interface DefineWindowFame {
    /**
     * The first step in defining a window frame.
     *
     * @return the set window start step in the fluent query builder.
     */
    SetWindowStart rowsBetween();
  }

  /** Set the window frame boundary start. */
  interface SetWindowStart {
    /**
     * Set the bound to the first partition row.
     *
     * @return the set window frame end step in the fluent analytic query builder.
     */
    SetWindowEndOptionOne unboundedPreceding();

    /**
     * Set the bound to a number of rows preceding the current row.
     *
     * @param nRows number of rows before the current row to include in the window.
     * @return the set window frame end step in the fluent analytic query builder.
     */
    SetWindowEndOptionOne preceding(int nRows);

    /**
     * Set the bound to the current row.
     *
     * @return the set window frame end step in the fluent analytic query builder.
     */
    SetWindowEndOptionTwo currentRow();

    /**
     * Set the bound to a number of rows following the current row.
     *
     * @param nRows number of rows after the current row to include in the window.
     * @return the set window frame end step in the fluent analytic query builder.
     */
    SetWindowEndOptionTwo following(int nRows);
  }

  /** Set the window frame boundary end. */
  interface SetWindowEndOptionOne {
    /**
     * Set the bound to a number of rows preceding the current row.
     *
     * @param nRows number of rows before the current row to include in the window.
     * @return the add aggregate functions step in the fluent analytic query builder.
     */
    AddAggregateFunctions andPreceding(int nRows);

    /**
     * Set the bound to the current row.
     *
     * @return the add aggregate functions step in the fluent analytic query builder.
     */
    AddAggregateFunctions andCurrentRow();

    /**
     * Set the bound to a number of rows following the current row.
     *
     * @param nRows number of rows after the current row to include in the window.
     * @return the add aggregate functions step in the fluent analytic query builder.
     */
    AddAggregateFunctions andFollowing(int nRows);

    /**
     * Set the bound to the last partition row.
     *
     * @return the add aggregate functions step in the fluent analytic query builder.
     */
    AddAggregateFunctions andUnBoundedFollowing();
  }

  /** Set the window frame boundary end. */
  interface SetWindowEndOptionTwo {
    AddAggregateFunctions andFollowing(int nRows);

    AddAggregateFunctions andUnBoundedFollowing();
  }

  interface NameStepAggregate {

    /**
     * Add alias/name to the calculated column.
     *
     * @param columnName the name to give the calculated column.
     * @return the next step in the fluent query builder.
     */
    AddAggregateFunctionsWithExecute as(String columnName);
  }

  interface NameStepNumbering {
    /**
     * Add alias/name to the calculated column.
     *
     * @param columnName the name to give the calculated column.
     * @return the next step in the fluent query builder.
     */
    AddNumberingFunctionWithExecute as(String columnName);
  }

  interface Execute {

    /**
     * Build the Query object without executing it.
     *
     * @return a query object that can be executed.
     */
    AnalyticQuery build();

    /**
     * Executes the query adding all the calculated columns to a new table. The result columns will
     * have the same order as the from table.
     *
     * @return a new table containing only the result columns.
     */
    Table execute();

    /**
     * Executes the query and adds all the calculated columns directly to the source table.
     *
     * @throws IllegalArgumentException if any of the calculated columns have the same name as one
     *     of the columns in the FROM table.
     */
    void executeInPlace();
  }

  /** Step to add the first Analytic Aggregate Function. Cannot execute the query yet. */
  interface AddAggregateFunctions extends AnalyticFunctions {}

  /** Step to add the another Analytic Aggregate Function. Can execute the query. */
  interface AddAggregateFunctionsWithExecute extends AnalyticFunctions, Execute {}

  /** Step to add the first Numbering Function. Cannot execute the query yet. */
  interface AddNumberingFunction extends NumberingFunctions {}

  /** Step to add the another Numbering Function. Can execute the query. */
  interface AddNumberingFunctionWithExecute extends NumberingFunctions, Execute {}
}
