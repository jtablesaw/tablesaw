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

import static tech.tablesaw.io.saw.SawUtils.*;

import com.google.common.annotations.Beta;
import com.google.gson.Gson;
import java.util.UUID;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.strings.ByteDictionaryMap;
import tech.tablesaw.columns.strings.DictionaryMap;
import tech.tablesaw.columns.strings.IntDictionaryMap;
import tech.tablesaw.columns.strings.ShortDictionaryMap;

/** Data about a specific column used in it's persistence */
@Beta
public class ColumnMetadata {

  private static final Gson GSON = new Gson();

  private final String id;
  private final String name;
  private final String type;
  private final int size;
  private final String stringColumnKeySize;

  ColumnMetadata(Column column) {
    this.id = UUID.randomUUID().toString();
    this.name = column.name();
    this.type = column.type().name();
    this.size = column.size();
    if (column instanceof StringColumn) {
      StringColumn stringColumn = (StringColumn) column;
      DictionaryMap lookupTable = stringColumn.unsafeGetLookupTable();
      if (lookupTable instanceof IntDictionaryMap) {
        stringColumnKeySize = Integer.class.getSimpleName();
      } else if (lookupTable instanceof ByteDictionaryMap) {
        stringColumnKeySize = Byte.class.getSimpleName();
      } else if (lookupTable instanceof ShortDictionaryMap) {
        stringColumnKeySize = Short.class.getSimpleName();
      } else {
        stringColumnKeySize = "";
      }
    } else {
      stringColumnKeySize = "";
    }
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

  public String getStringColumnKeySize() {
    return stringColumnKeySize;
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
      case INSTANT:
        return DateTimeColumn.create(name);
      case STRING:
        return StringColumn.create(name);
      case TEXT:
        return TextColumn.create(name);
      case SHORT:
        return ShortColumn.create(name);
      case LONG:
        return LongColumn.create(name);
      default:
        throw new IllegalStateException("Unhandled column type writing columns: " + typeString);
    }
  }
}
