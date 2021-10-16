package tech.tablesaw.sorting;

import it.unimi.dsi.fastutil.ints.IntComparator;
import java.util.Iterator;
import java.util.Map;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.sorting.comparators.IntComparatorChain;
import tech.tablesaw.sorting.comparators.ReversingIntComparator;

/** Static utility class that provides Comparators for sorting */
public class SortUtils {

  private SortUtils() {}

  /** Returns a comparator chain for sorting according to the given key */
  public static IntComparatorChain getChain(Table table, Sort key) {
    Iterator<Map.Entry<String, Sort.Order>> entries = key.iterator();
    Map.Entry<String, Sort.Order> sort = entries.next();
    Column<?> column = table.column(sort.getKey());
    IntComparator comparator = rowComparator(column, sort.getValue());

    IntComparatorChain chain = new IntComparatorChain(comparator);
    while (entries.hasNext()) {
      sort = entries.next();
      chain.addComparator(rowComparator(table.column(sort.getKey()), sort.getValue()));
    }
    return chain;
  }

  /**
   * Returns a comparator for the column matching the specified name
   *
   * @param column The column to sort
   * @param order Specifies whether the sort should be in ascending or descending order
   */
  public static IntComparator rowComparator(Column<?> column, Sort.Order order) {
    IntComparator rowComparator = column.rowComparator();
    if (order == Sort.Order.DESCEND) {
      return ReversingIntComparator.reverse(rowComparator);
    } else {
      return rowComparator;
    }
  }

  /**
   * Returns a comparator that can be used to sort the records in this table according to the given
   * sort key
   */
  public static IntComparator getComparator(Table table, Sort key) {
    Iterator<Map.Entry<String, Sort.Order>> entries = key.iterator();
    Map.Entry<String, Sort.Order> sort = entries.next();
    Column<?> column = table.column(sort.getKey());
    return SortUtils.rowComparator(column, sort.getValue());
  }
}
