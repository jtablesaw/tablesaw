package tech.tablesaw.joining;

import tech.tablesaw.api.ColumnType;

/**
 * Describes two columns that are to be compared in a sort The columns are expected to be referenced
 * in two separate rows. The values of left and right provide the column index (position) in each of
 * the two rows.
 */
public class ColumnIndexPair {
  final ColumnType type;
  final int left;
  final int right;

  public ColumnIndexPair(ColumnType type, int left, int right) {
    this.type = type;
    this.left = left;
    this.right = right;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ColumnIndexPair{");
    sb.append("type=").append(type);
    sb.append(", left=").append(left);
    sb.append(", right=").append(right);
    sb.append('}');
    return sb.toString();
  }
}
