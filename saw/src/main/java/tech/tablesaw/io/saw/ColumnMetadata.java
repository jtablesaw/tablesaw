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

import static tech.tablesaw.io.saw.SawUtils.BOOLEAN;
import static tech.tablesaw.io.saw.SawUtils.DOUBLE;
import static tech.tablesaw.io.saw.SawUtils.FLOAT;
import static tech.tablesaw.io.saw.SawUtils.INSTANT;
import static tech.tablesaw.io.saw.SawUtils.INTEGER;
import static tech.tablesaw.io.saw.SawUtils.LOCAL_DATE;
import static tech.tablesaw.io.saw.SawUtils.LOCAL_DATE_TIME;
import static tech.tablesaw.io.saw.SawUtils.LOCAL_TIME;
import static tech.tablesaw.io.saw.SawUtils.LONG;
import static tech.tablesaw.io.saw.SawUtils.SHORT;
import static tech.tablesaw.io.saw.SawUtils.STRING;
import static tech.tablesaw.io.saw.SawUtils.TEXT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.Beta;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.InstantColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.TextColumn;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.strings.ByteDictionaryMap;
import tech.tablesaw.columns.strings.IntDictionaryMap;
import tech.tablesaw.columns.strings.LookupTableWrapper;
import tech.tablesaw.columns.strings.ShortDictionaryMap;

/** Data about a specific column used in it's persistence */
@Beta
public class ColumnMetadata {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private String id;
  private String name;
  private String type;
  private String stringColumnKeySize;

  ColumnMetadata(Column<?> column) {
    this.id = UUID.randomUUID().toString();
    this.name = column.name();
    this.type = column.type().name();
    if (column instanceof StringColumn) {
      StringColumn stringColumn = (StringColumn) column;
      LookupTableWrapper lookupTable = stringColumn.getLookupTable();
      if (lookupTable.dictionaryClass().equals(IntDictionaryMap.class)) {
        stringColumnKeySize = Integer.class.getSimpleName();
      } else if (lookupTable.dictionaryClass().equals(ByteDictionaryMap.class)) {
        stringColumnKeySize = Byte.class.getSimpleName();
      } else if (lookupTable.dictionaryClass().equals(ShortDictionaryMap.class)) {
        stringColumnKeySize = Short.class.getSimpleName();
      } else {
        stringColumnKeySize = "";
      }
    } else {
      stringColumnKeySize = "";
    }
  }

  protected ColumnMetadata() {}

  public static ColumnMetadata fromJson(String jsonString) {
    try {
      return objectMapper.readValue(jsonString, ColumnMetadata.class);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public String toJson() {
    try {
      return objectMapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
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

  public Column<?> createColumn() {
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
        return InstantColumn.create(name);
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
