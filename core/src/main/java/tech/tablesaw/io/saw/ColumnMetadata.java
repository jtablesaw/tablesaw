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

import com.google.gson.Gson;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;

import java.util.UUID;

import static tech.tablesaw.io.saw.StorageManager.*;

/** Data about a specific column used in it's persistence */
public class ColumnMetadata {

  private static final Gson GSON = new Gson();

  private final String id;
  private final String name;
  private final String type;
  private final int size;

  ColumnMetadata(Column column) {
    this.id = UUID.randomUUID().toString();
    this.name = column.name();
    this.type = column.type().name();
    this.size = column.size();
  }

  public static ColumnMetadata fromJson(String jsonString) {
    return GSON.fromJson(jsonString, ColumnMetadata.class);
  }

  public String toJson() {
    return GSON.toJson(this);
  }

  @Override
  public String toString() {
    return "ColumnMetadata{"
        + "id='"
        + id
        + '\''
        + ", name='"
        + name
        + '\''
        + ", type="
        + type
        + ", size="
        + size
        + '}';
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public int getSize() {
    return size;
  }

  public Column createColumn() {
      final String typeString = getType();
    switch (typeString) {
      case FLOAT:
        return FloatColumn.create(name);
      case DOUBLE:
        return DoubleColumn.create(name);
      case INTEGER:
        return IntColumn.create(name);
      case BOOLEAN:
        return BooleanColumn.create(name);
      case LOCAL_DATE:
        return DateColumn.create(name);
      case LOCAL_TIME:
        return TimeColumn.create(name);
      case LOCAL_DATE_TIME:
        return DateTimeColumn.create(name);
      case STRING:
        return StringColumn.create(name);
      case SHORT:
        return ShortColumn.create(name);
      case LONG:
        return LongColumn.create(name);
      default:
        throw new IllegalStateException("Unhandled column type writing columns: " + typeString);
    }
  }
}
