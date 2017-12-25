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

package tech.tablesaw.store;

import com.google.gson.Gson;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;

/**
 * Data about a specific column used in it's persistence
 */
public class ColumnMetadata {

    private static final Gson GSON = new Gson();

    private final String id;
    private final String name;
    private final ColumnType type;
    private final int size;

    public ColumnMetadata(Column column) {
        this.id = column.id();
        this.name = column.name();
        this.type = column.type();
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
        return "ColumnMetadata{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", size=" + size +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnMetadata that = (ColumnMetadata) o;

        if (size != that.size) return false;
        if (!id.equals(that.id)) return false;
        if (!name.equals(that.name)) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + size;
        return result;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ColumnType getType() {
        return type;
    }

    public int getSize() {
        return size;
    }
    
    public Column createColumn() {
      switch (getType()) {
        case FLOAT:
          return new FloatColumn(this);
        case DOUBLE:
          return new DoubleColumn(this);
        case INTEGER:
          return new IntColumn(this);
        case BOOLEAN:
          return new BooleanColumn(this);
        case LOCAL_DATE:
          return new DateColumn(this);
        case LOCAL_TIME:
          return new TimeColumn(this);
        case LOCAL_DATE_TIME:
          return new DateTimeColumn(this);
        case CATEGORY:
          return new CategoryColumn(this);
        case SHORT_INT:
          return new ShortColumn(this);
        case LONG_INT:
          return new LongColumn(this);
        default:
            throw new IllegalStateException("Unhandled column type writing columns");
      }
    }
    
}
