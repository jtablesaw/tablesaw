/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.sorting;

import static java.util.stream.Collectors.toSet;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import tech.tablesaw.api.Table;

/**
 * Provides fine-grained control over sorting.
 *
 * <p>Use:
 *
 * <p>table.sortOn(first("Year", DESCEND).next("State", ASCEND));
 *
 * <p>This sorts table on the column named year in descending order, such that the most recent years
 * appear first, then on State, in ascending order so "AL" will appear before "CA". You can add
 * additional instructions for multi-column sorts by chaining additional calls to next() with the
 * appropriate column names and Order.
 */
public class Sort implements Iterable<Map.Entry<String, Sort.Order>> {

  private final LinkedHashMap<String, Order> sortOrder = new LinkedHashMap<>();

  /**
   * Constructs a Sort specifying the order (ascending or descending) to apply to the column with
   * the given name
   */
  public Sort(String columnName, Order order) {
    next(columnName, order);
  }

  /**
   * Returns a Sort specifying the order (ascending or descending) to apply to the column with the
   * given name
   */
  public static Sort on(String columnName, Order order) {
    return new Sort(columnName, order);
  }

  /**
   * Returns a Sort that concatenates a new sort on the given order (ascending or descending) and
   * columnName onto the sort specified here. This method is used to construct complex sorts such
   * as: Sort.on("foo", Order.ASCEND).next("bar", Order.DESCEND);
   */
  public Sort next(String columnName, Order order) {
    sortOrder.put(columnName, order);
    return this;
  }

  /** Returns true if no order has been set */
  public boolean isEmpty() {
    return sortOrder.isEmpty();
  }

  /** Returns the number of columns used in this sort */
  public int size() {
    return sortOrder.size();
  }

  /**
   * Create a Sort object from the given table and sort column names. Does not sort the table.
   *
   * @param table to sort. Used only to pull the table's schema. Does not modify the table.
   * @param columnNames The columns to sort on. Can prefix column name with + for ascending, - for
   *     descending. Default to ascending if no prefix is added.
   * @return a {@link #Sort} Object.
   */
  public static Sort create(Table table, String... columnNames) {
    Preconditions.checkArgument(columnNames.length > 0, "At least one sort column must provided.");

    Sort key = null;
    Set<String> names = table.columnNames().stream().map(String::toUpperCase).collect(toSet());

    for (String columnName : columnNames) {
      Sort.Order order = Sort.Order.ASCEND;
      if (!names.contains(columnName.toUpperCase())) {
        // the column name has been annotated with a prefix.
        // get the prefix which could be - or +
        String prefix = columnName.substring(0, 1);
        Optional<Order> orderOptional = getOrder(prefix);

        // Invalid prefix, column name exists on table.
        if (!orderOptional.isPresent() && names.contains(columnName.substring(1).toUpperCase())) {
          throw new IllegalStateException("Column prefix: " + prefix + " is unknown.");
        }

        // Valid prefix, column name does not exist on table.
        if (orderOptional.isPresent() && !names.contains(columnName.substring(1).toUpperCase())) {
          throw new IllegalStateException(
              String.format(
                  "Column %s does not exist in table %s", columnName.substring(1), table.name()));
        }

        // Invalid prefix, column name does not exist on table.
        if (!orderOptional.isPresent()) {
          throw new IllegalStateException("Unrecognized Column: '" + columnName + "'");
        }

        // Valid Prefix, column name exists on table.
        // remove - prefix so provided name matches actual column name
        columnName = columnName.substring(1);
        order = orderOptional.get();
      }

      if (key == null) { // key will be null the first time through
        key = new Sort(columnName, order);
      } else {
        key.next(columnName, order);
      }
    }
    return key;
  }

  private static Optional<Order> getOrder(String prefix) {
    switch (prefix) {
      case "+":
        return Optional.of(Order.ASCEND);
      case "-":
        return Optional.of(Order.DESCEND);
      default:
        return Optional.empty();
    }
  }

  /**
   * Returns an iterator over elements of type {@code T}.
   *
   * @return an Iterator.
   */
  @Override
  public Iterator<Map.Entry<String, Order>> iterator() {
    return sortOrder.entrySet().iterator();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("order", sortOrder).toString();
  }

  public enum Order {
    ASCEND,
    DESCEND
  }
}
