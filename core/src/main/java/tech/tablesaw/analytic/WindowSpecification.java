package tech.tablesaw.analytic;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import tech.tablesaw.analytic.AnalyticQuery.Order;

final public class WindowSpecification {

  private final String windowName;
  private final LinkedHashSet<String> partitioning;
  private final List<OrderPair> ordering;

  private WindowSpecification(String windowName, LinkedHashSet<String> partitioning, List<OrderPair> ordering) {
    this.windowName = windowName;
    this.partitioning = partitioning;
    this.ordering = ordering;
  }

  static Builder builder() {
    return new Builder();
  }

  public String toSqlString() {
    StringBuilder sb = new StringBuilder();
    if (!partitioning.isEmpty()) {
      sb.append("PARTITION BY ");
      sb.append(String.join(", ", partitioning));
      sb.append(System.lineSeparator());
    }
    if (!ordering.isEmpty()) {
      sb.append("ORDER BY ");
      sb.append(ordering.stream().map(java.util.Objects::toString).collect(Collectors.joining(", ")));
    }
    return sb.toString();
  }

  public boolean isEmpty() {
    return partitioning.isEmpty() && ordering.isEmpty();
  }

  @Override
  public String toString() {
    return this.toSqlString();
  }

  public String getWindowName() {
    return windowName;
  }

  public LinkedHashSet<String> getPartitioning() {
    return partitioning;
  }

  public List<OrderPair> getOrdering() {
    return ordering;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    WindowSpecification that = (WindowSpecification) o;
    return Objects.equal(windowName, that.windowName) &&
      Objects.equal(partitioning, that.partitioning) &&
      Objects.equal(ordering, that.ordering);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(windowName, partitioning, ordering);
  }

  public static class OrderPair {
    private final String columnName;
    private final Order order;

    private OrderPair(String columnName, Order order) {
      this.columnName = columnName;
      this.order = order;
    }

    public static OrderPair of(String columnName, Order order) {
      return new OrderPair(columnName, order);
    }

    public String getColumnName() {
      return columnName;
    }

    public Order getOrder() {
      return order;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      OrderPair orderPair = (OrderPair) o;
      return Objects.equal(columnName, orderPair.columnName) &&
        order == orderPair.order;
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(columnName, order);
    }

    @Override
    public String toString() {
      return columnName + " " + order;
    }
  }

  static class Builder {
    private String windowName = "w1";
    private LinkedHashSet<String> partitioning = new LinkedHashSet<>();
    private List<OrderPair> ordering = new ArrayList<>();

    private Builder() {
    }

    Builder setWindowName(String windowName) {
      this.windowName = windowName;
      return this;
    }

    Builder setPartitionColumns(List<String> columns) {
      Preconditions.checkArgument(columns.size() > 0);
      this.partitioning.clear();
      this.partitioning.addAll(columns);
      // TODO add actual duplicate columns to the error message.
      Preconditions.checkArgument(partitioning.size() == columns.size(),
        "Partition by Columns cannot contain duplicate columns");
      return this;
    }

    Builder setOrderColumns(List<OrderPair> orderPairs) {
      Set<String> orderPairSet = orderPairs.stream().map(OrderPair::getColumnName).collect(Collectors.toSet());
      // TODO add actual duplicate columns to the error message.
      Preconditions.checkArgument(orderPairSet.size() == orderPairs.size(),
        "Order By cannot contain duplicate columns"
      );
      this.ordering = ImmutableList.copyOf(orderPairs);
      return this;
    }

    WindowSpecification build() {
      return new WindowSpecification(
        windowName,
        partitioning,
        ordering
      );
    }
  }
}
