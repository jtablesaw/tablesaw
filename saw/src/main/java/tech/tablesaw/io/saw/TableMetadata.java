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

package tech.tablesaw.io.saw;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.table.Relation;

/** Data about a specific physical table used in its persistence */
@Beta
public class TableMetadata {

  @JsonProperty("columnMetadata")
  private final List<ColumnMetadata> columnMetadataList = new ArrayList<>();

  // The name of the table
  private String name;

  // The number of rows in the table
  private int rowCount;

  @JsonIgnore private Map<String, ColumnMetadata> columnMetadataMap = new HashMap<>();

  TableMetadata(Relation table) {
    this.name = table.name();
    this.rowCount = table.rowCount();

    for (Column<?> column : table.columns()) {
      ColumnMetadata metadata = new ColumnMetadata(column);
      columnMetadataList.add(metadata);
      columnMetadataMap.put(column.name(), metadata);
    }
  }

  /** Default constructor for Jackson json serialization */
  protected TableMetadata() {}

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TableMetadata metadata = (TableMetadata) o;
    return getRowCount() == metadata.getRowCount()
        && Objects.equal(getColumnMetadataList(), metadata.getColumnMetadataList())
        && Objects.equal(getName(), metadata.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getColumnMetadataList(), getName(), getRowCount());
  }

  /** Returns the name of the table */
  @SuppressWarnings("WeakerAccess")
  public String getName() {
    return name;
  }

  /** Returns the number of rows in the table */
  @SuppressWarnings("WeakerAccess")
  public int getRowCount() {
    return rowCount;
  }

  /** Returns a list of ColumnMetadata objects, one for each Column in the table */
  List<ColumnMetadata> getColumnMetadataList() {
    return columnMetadataList;
  }

  public Map<String, ColumnMetadata> getColumnMetadataMap() {
    return columnMetadataMap;
  }

  /** Returns the number of columns in the table */
  public int columnCount() {
    return getColumnMetadataList().size();
  }

  /**
   * Returns a string describing the number of rows and columns in the table. This is analogous to
   * the shape() method defined on Relation.
   */
  public String shape() {
    return getName() + ": " + getRowCount() + " rows X " + columnCount() + " cols";
  }

  /** Returns a List of the names of all the columns in this table */
  public List<String> columnNames() {
    return columnMetadataList.stream().map(ColumnMetadata::getName).collect(toList());
  }

  /** Returns a table that describes the columns in this table */
  public Table structure() {
    Table t = Table.create("Structure of " + getName());
    IntColumn index = IntColumn.indexColumn("Index", columnCount(), 0);
    StringColumn columnName = StringColumn.create("Column Name", columnCount());
    StringColumn columnType = StringColumn.create("Column Type", columnCount());
    t.addColumns(index);
    t.addColumns(columnName);
    t.addColumns(columnType);
    for (int i = 0; i < columnCount(); i++) {
      ColumnMetadata column = columnMetadataList.get(i);
      columnType.set(i, column.getType());
      columnName.set(i, columnNames().get(i));
    }
    return t;
  }
}
