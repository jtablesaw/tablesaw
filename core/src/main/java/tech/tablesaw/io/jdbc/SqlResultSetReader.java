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

package tech.tablesaw.io.jdbc;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.TypeUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Creates a Relation from the result of a SQL query, by passing the jdbc resultset to the constructor
 */
public class SqlResultSetReader {

    // Maps from supported SQL types to their Tablesaw 'equivalents'
    private static final ImmutableMap<Integer, ColumnType> SQL_TYPE_TO_TABLESAW_TYPE =
            new ImmutableMap.Builder<Integer, ColumnType>()
                    .put(Types.BINARY, ColumnType.BOOLEAN)
                    .put(Types.BOOLEAN, ColumnType.BOOLEAN)
                    .put(Types.BIT, ColumnType.BOOLEAN)

                    .put(Types.DATE, ColumnType.LOCAL_DATE)
                    .put(Types.TIME, ColumnType.LOCAL_TIME)
                    .put(Types.TIMESTAMP, ColumnType.LOCAL_DATE_TIME)

                    .put(Types.DECIMAL, ColumnType.FLOAT)
                    .put(Types.DOUBLE, ColumnType.FLOAT)
                    .put(Types.FLOAT, ColumnType.FLOAT)
                    .put(Types.NUMERIC, ColumnType.FLOAT)
                    .put(Types.REAL, ColumnType.FLOAT)

                    .put(Types.INTEGER, ColumnType.INTEGER)
                    .put(Types.SMALLINT, ColumnType.SHORT_INT)
                    .put(Types.TINYINT, ColumnType.SHORT_INT)
                    .put(Types.BIGINT, ColumnType.LONG_INT)

                    .put(Types.CHAR, ColumnType.CATEGORY)
                    .put(Types.LONGVARCHAR, ColumnType.CATEGORY)
                    .put(Types.LONGNVARCHAR, ColumnType.CATEGORY)
                    .put(Types.NCHAR, ColumnType.CATEGORY)
                    .put(Types.NVARCHAR, ColumnType.CATEGORY)
                    .put(Types.VARCHAR, ColumnType.CATEGORY)
                    .build();

    /**
     * Returns a new table with the given tableName, constructed from the given result set
     *
     * @throws SQLException
     */
    public static Table read(ResultSet resultSet, String tableName) throws SQLException {

        ResultSetMetaData metaData = resultSet.getMetaData();
        Table table = Table.create(tableName);

        // Setup the columns and add to the table
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String name = metaData.getColumnName(i);

            ColumnType type = SQL_TYPE_TO_TABLESAW_TYPE.get(metaData.getColumnType(i));
            Preconditions.checkState(type != null,
                    "No column type found for %s as specified for column %s", metaData.getColumnType(i), name);

            Column newColumn = TypeUtils.newColumn(name, type);
            table.addColumn(newColumn);
        }

        // Add the rows
        while (resultSet.next()) {
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                Column column = table.column(i - 1); // subtract 1 because results sets originate at 1 not 0
                column.appendCell(resultSet.getString(i));
            }
        }
        return table;
    }
}