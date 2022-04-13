package tech.tablesaw.api;

import java.util.function.Function;
import tech.tablesaw.filtering.And;
import tech.tablesaw.filtering.DeferredBooleanColumn;
import tech.tablesaw.filtering.DeferredColumn;
import tech.tablesaw.filtering.DeferredDateColumn;
import tech.tablesaw.filtering.DeferredDateTimeColumn;
import tech.tablesaw.filtering.DeferredInstantColumn;
import tech.tablesaw.filtering.DeferredNumberColumn;
import tech.tablesaw.filtering.DeferredStringColumn;
import tech.tablesaw.filtering.DeferredTimeColumn;
import tech.tablesaw.filtering.Not;
import tech.tablesaw.filtering.Or;
import tech.tablesaw.selection.Selection;

/** Utility methods to aid in the construction of complex queries on tables */
public class QuerySupport {

  /** Returns a selection for all records for which the given function is {@code false} */
  public static Function<Table, Selection> not(Function<Table, Selection> deferredSelection) {
    return new Not(deferredSelection);
  }

  /**
   * Returns a selection for all records that match neither of the given functions. In other words,
   * if either (or both) of sel1 or sel2 is {@code true}, the record as a whole is {@code false}.
   */
  public static Function<Table, Selection> neither(
      Function<Table, Selection> sel1, Function<Table, Selection> sel2) {
    return new Not(either(sel1, sel2));
  }

  /**
   * Returns a selection for all records that don't match both of the given functions. In other
   * words, if both sel1 and sel2 are true, the record as a whole is false, and if either (or both)
   * of sel1 or sel2 is {@code false}, the record as a whole is {@code true}.
   */
  public static Function<Table, Selection> notBoth(
      Function<Table, Selection> sel1, Function<Table, Selection> sel2) {
    return new Not(both(sel1, sel2));
  }

  /** Returns a selection for all records that don't match any of the given functions */
  @SafeVarargs
  public static Function<Table, Selection> notAny(
      Function<Table, Selection>... deferredSelections) {
    return new Not(any(deferredSelections));
  }

  /** Returns a selection for all records that don't match all of the given functions */
  @SafeVarargs
  public static Function<Table, Selection> notAll(
      Function<Table, Selection>... deferredSelections) {
    return new Not(all(deferredSelections));
  }

  /** Returns a selection for all records that match all of the given functions */
  @SafeVarargs
  public static Function<Table, Selection> and(Function<Table, Selection>... deferredSelections) {
    return new And(deferredSelections);
  }

  /**
   * Returns a selection for all records that match all of the given functions. A synonym for and()
   */
  @SafeVarargs
  public static Function<Table, Selection> all(Function<Table, Selection>... deferredSelections) {
    return new And(deferredSelections);
  }

  /** Returns a selection for all records that match both of the given functions */
  public static Function<Table, Selection> both(
      Function<Table, Selection> sel1, Function<Table, Selection> sel2) {
    return new And(sel1, sel2);
  }

  /** Returns a selection for all records that match any of the given functions */
  @SafeVarargs
  public static Function<Table, Selection> or(Function<Table, Selection>... deferredSelections) {
    return new Or(deferredSelections);
  }

  /**
   * Returns a selection for all records that match any of the given functions. A synonym for or()
   */
  @SafeVarargs
  public static Function<Table, Selection> any(Function<Table, Selection>... deferredSelections) {
    return new Or(deferredSelections);
  }

  /** Returns a selection for all records that match either of the given functions */
  public static Function<Table, Selection> either(
      Function<Table, Selection> sel1, Function<Table, Selection> sel2) {
    return new Or(sel1, sel2);
  }

  public static DeferredColumn column(String columnName) {
    return new DeferredColumn(columnName);
  }

  public static DeferredColumn col(String columnName) {
    return new DeferredColumn(columnName);
  }

  public static DeferredBooleanColumn booleanColumn(String columnName) {
    return new DeferredBooleanColumn(columnName);
  }

  public static DeferredBooleanColumn bool(String columnName) {
    return new DeferredBooleanColumn(columnName);
  }

  public static DeferredStringColumn stringColumn(String columnName) {
    return new DeferredStringColumn(columnName);
  }

  public static DeferredStringColumn str(String columnName) {
    return new DeferredStringColumn(columnName);
  }

  public static DeferredNumberColumn numberColumn(String columnName) {
    return new DeferredNumberColumn(columnName);
  }

  public static DeferredNumberColumn num(String columnName) {
    return new DeferredNumberColumn(columnName);
  }

  public static DeferredDateColumn dateColumn(String columnName) {
    return new DeferredDateColumn(columnName);
  }

  public static DeferredDateColumn date(String columnName) {
    return new DeferredDateColumn(columnName);
  }

  public static DeferredDateTimeColumn dateTimeColumn(String columnName) {
    return new DeferredDateTimeColumn(columnName);
  }

  public static DeferredDateTimeColumn dateTime(String columnName) {
    return new DeferredDateTimeColumn(columnName);
  }

  public static DeferredInstantColumn instantColumn(String columnName) {
    return new DeferredInstantColumn(columnName);
  }

  public static DeferredTimeColumn timeColumn(String columnName) {
    return new DeferredTimeColumn(columnName);
  }

  public static DeferredTimeColumn time(String columnName) {
    return new DeferredTimeColumn(columnName);
  }
}
