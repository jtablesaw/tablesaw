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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.Beta;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import tech.tablesaw.columns.Column;
import tech.tablesaw.table.Relation;

/** Data about a specific physical table used in its persistence */
@Beta
public class TableMetadata {

  static final String METADATA_FILE_NAME = "Metadata.json";
  private static final int SAW_VERSION = 1;

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @JsonProperty("columnMetadata")
  private List<ColumnMetadata> columnMetadataList = new ArrayList<>();

  private String name;
  private int rowCount;
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

  static TableMetadata fromJson(String jsonString) {
    try {
      return objectMapper.readValue(jsonString, TableMetadata.class);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

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

  @SuppressWarnings("WeakerAccess")
  public String getName() {
    return name;
  }

  @SuppressWarnings("WeakerAccess")
  public int getRowCount() {
    return rowCount;
  }

  public int getVersion() {
    return version;
  }

  List<ColumnMetadata> getColumnMetadataList() {
    return columnMetadataList;
  }
}
