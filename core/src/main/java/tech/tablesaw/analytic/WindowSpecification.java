package tech.tablesaw.analytic;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Streams;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import tech.tablesaw.sorting.Sort;

/** This class holds data on the Partition By and Order By clauses of an an analytic query. */
final class WindowSpecification {

  private final String windowName;
  private final Set<String> partitionColumns;
  private final Sort sort;

  private WindowSpecification(String windowName, Set<String> partitionColumns, Sort sort) {
    this.windowName = windowName;
    this.partitionColumns = partitionColumns;
    this.sort = sort;
  }

  static Builder builder() {
    return new Builder();
  }

  public String toSqlString() {
    StringBuilder sb = new StringBuilder();
    if (!partitionColumns.isEmpty()) {
      sb.append("PARTITION BY ");
      sb.append(String.join(", ", partitionColumns));
      sb.append(System.lineSeparator());
    }
    if (!sort.isEmpty()) {
      sb.append("ORDER BY ");
      sb.append(
          Streams.stream(sort.iterator())
              .map(this::formatOrdering)
              .collect(Collectors.joining(", ")));
    }
    return sb.toString();
  }

  public boolean isEmpty() {
    return partitionColumns.isEmpty() && sort == null;
  }

  @Override
  public String toString() {
    return this.toSqlString();
  }

  public String getWindowName() {
    return windowName;
  }

  public Set<String> getPartitionColumns() {
    return partitionColumns;
  }

  public Optional<Sort> getSort() {
    return Optional.ofNullable(this.sort);
  }

  private String formatOrdering(Map.Entry<String, Sort.Order> sortEntry) {
    String formattedOrder = "ASC";
    if (sortEntry.getValue() == Sort.Order.DESCEND) {
      formattedOrder = "DESC";
    }
    return sortEntry.getKey() + " " + formattedOrder;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WindowSpecification that = (WindowSpecification) o;
    return Objects.equal(windowName, that.windowName)
        && Objects.equal(partitionColumns, that.partitionColumns)
        && Objects.equal(sort, that.sort);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(windowName, partitionColumns, sort);
  }

  static class Builder {
    private String windowName = "w1";
    private LinkedHashSet<String> partitioning = new LinkedHashSet<>();
    private Sort sort = null;

    private Builder() {}

    Builder setWindowName(String windowName) {
      this.windowName = windowName;
      return this;
    }

    Builder setPartitionColumns(List<String> columns) {
      this.partitioning.clear();
      this.partitioning.addAll(columns);
      Preconditions.checkArgument(
          partitioning.size() == columns.size(),
          "Partition by Columns cannot contain duplicate columns");
      return this;
    }

    Builder setSort(Sort sort) {
      this.sort = sort;
      return this;
    }

    WindowSpecification build() {
      return new WindowSpecification(windowName, partitioning, sort);
    }
  }
}
