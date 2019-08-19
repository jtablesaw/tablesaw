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
import tech.tablesaw.columns.Column;
import tech.tablesaw.table.Relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data about a specific physical table used in its persistence
 */
public class TableMetadata {

    private static final Gson GSON = new Gson();

    private final List<ColumnMetadata> columnMetadataList = new ArrayList<>();
    private final String name;
    private final int rowCount;

    TableMetadata(Relation table) {
        this.name = table.name();
        this.rowCount = table.rowCount();
        for (Column column : table.columns()) {
            ColumnMetadata metadata = new ColumnMetadata(column);
            columnMetadataList.add(metadata);
        }
    }

    static TableMetadata fromJson(String jsonString) {
        return GSON.fromJson(jsonString, TableMetadata.class);
    }

    String toJson() {
        return GSON.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableMetadata that = (TableMetadata) o;
        return rowCount == that.rowCount &&
                Objects.equals(name, that.name) &&
                Objects.equals(columnMetadataList, that.columnMetadataList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, rowCount, columnMetadataList);
    }

    public String getName() {
        return name;
    }

    public int getRowCount() {
        return rowCount;
    }

    List<ColumnMetadata> getColumnMetadataList() {
        return columnMetadataList;
    }
}