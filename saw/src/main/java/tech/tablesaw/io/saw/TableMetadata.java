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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.Beta;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.table.Relation;

/** Data about a specific physical table used in its persistence */
@Beta
public class TableMetadata {

  static final String METADATA_FILE_NAME = "Metadata.json";
  private static final int SAW_VERSION = 1;

  /**
   * Returns a TableMetadata instance derived from the json-formatted Metadata.json file in the
   * directory specified by sawPath
   *
   * @param sawPath The path to the folder containing the Saw metadata file and table data
   * @throws IOException if the file can not be read
   */
  static TableMetadata readTableMetadata(Path sawPath) throws IOException {

    Path resolvePath = sawPath.resolve(METADATA_FILE_NAME);
    byte[] encoded = Files.readAllBytes(resolvePath);
    return TableMetadata.fromJson(new String(encoded, StandardCharsets.UTF_8));
  }

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @JsonProperty("columnMetadata")
  private final List<ColumnMetadata> columnMetadataList = new ArrayList<>();

  // The name of the table
  private String name;

  // The number of rows in the table
  private int rowCount;

  // The saw file format version
  private int version;

  TableMetadata(Relation table) {
    this.name = table.name();
    this.rowCount = table.rowCount();

    for (Column<?> column : table.columns()) {
      ColumnMetadata metadata = new ColumnMetadata(column);
      columnMetadataList.add(metadata);
    }
    this.version = SAW_VERSION;
  }

  /** Default constructor for Jackson json serialization */
  protected TableMetadata() {}

  /**
   * Returns an instance of TableMetadata constructed from the provided json string
   *
   * @param jsonString A json-formatted String consistent with those output by the toJson() method
   */
  static TableMetadata fromJson(String jsonString) {
    try {
      return objectMapper.readValue(jsonString, TableMetadata.class);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Returns a JSON string that represents this object
   *
   * @see static methdod fromJson() which constructs a TableMetadata object from this JSON output
   */
  String toJson() {
    try {
      return objectMapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TableMetadata that = (TableMetadata) o;
    return rowCount == that.rowCount
        && Objects.equals(name, that.name)
        && Objects.equals(columnMetadataList, that.columnMetadataList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, rowCount, columnMetadataList);
  }

  /**
   * Returns the name of the table
   *
   * @deprecated Use name() instead
   */
  @SuppressWarnings("WeakerAccess")
  public String getName() {
    return name;
  }

  /** Returns the name of the table */
  public String name() {
    return name;
  }

  /**
   * Returns the number of rows in the table
   *
   * @deprecated Use rowCount() instead
   */
  @SuppressWarnings("WeakerAccess")
  @Deprecated
  public int getRowCount() {
    return rowCount;
  }

  /** Returns the number of rows in the table */
  public int rowCount() {
    return rowCount;
  }

  /** Returns the saw file format version used to create this file */
  public int getVersion() {
    return version;
  }

  /** Returns a list of ColumnMetadata objects, one for each Column in the table */
  List<ColumnMetadata> getColumnMetadataList() {
    return columnMetadataList;
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
    return rowCount() + " rows X " + columnCount() + " cols";
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
