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
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates a Relation from the result of a SQL query, by passing the jdbc resultset to the constructor
 */
public class SqlResultSetReader {

    // Maps from supported SQL types to their Tablesaw equivalents'
    private static final Map<Integer, ColumnType> SQL_TYPE_TO_TABLESAW_TYPE = initializeMap();

    private static Map<Integer, ColumnType> initializeMap() {
        return new HashMap<>(
                new ImmutableMap.Builder<Integer, ColumnType>()
                .put(Types.BINARY, ColumnType.BOOLEAN)
                .put(Types.BOOLEAN, ColumnType.BOOLEAN)
                .put(Types.BIT, ColumnType.BOOLEAN)

                .put(Types.DATE, ColumnType.LOCAL_DATE)
                .put(Types.TIME, ColumnType.LOCAL_TIME)
                 // Instant, LocalDateTime, OffsetDateTime and ZonedDateTime are often mapped to timestamp
                .put(Types.TIMESTAMP, ColumnType.INSTANT)

                .put(Types.DECIMAL, ColumnType.DOUBLE)
                .put(Types.DOUBLE, ColumnType.DOUBLE)
                .put(Types.FLOAT, ColumnType.DOUBLE)
                .put(Types.NUMERIC, ColumnType.DOUBLE)
                .put(Types.REAL, ColumnType.FLOAT)

                .put(Types.INTEGER, ColumnType.INTEGER)
                .put(Types.SMALLINT, ColumnType.SHORT)
                .put(Types.TINYINT, ColumnType.SHORT)
                .put(Types.BIGINT, ColumnType.LONG)

                .put(Types.CHAR, ColumnType.STRING)
                .put(Types.NCHAR, ColumnType.STRING)
                .put(Types.NVARCHAR, ColumnType.STRING)
                .put(Types.VARCHAR, ColumnType.STRING)
                .put(Types.LONGVARCHAR, ColumnType.TEXT)
                .put(Types.LONGNVARCHAR, ColumnType.TEXT)
                .build());
    }

    /**
     * Change or add a mapping between the given Jdbc type and column type.
     * When reading from a database, the db column type is automatically assigned to the associated tablesaw column type
     * @param jdbc          an int representing a legal value from java.sql.types;
     * @param columnType    a tablesaw column type
     */
    public static void mapJdbcTypeToColumnType(Integer jdbc, ColumnType columnType) {
        SQL_TYPE_TO_TABLESAW_TYPE.put(jdbc, columnType);
    }

    /**
     * Returns a new table with the given tableName, constructed from the given result set
     *
     * @throws SQLException if there is a problem detected in the database
     */
    public static Table read(ResultSet resultSet) throws SQLException {

        ResultSetMetaData metaData = resultSet.getMetaData();
        Table table = Table.create();

        // Setup the columns and add to the table
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            ColumnType type = getColumnType(metaData.getColumnType(i), metaData.getScale(i), metaData.getPrecision(i));

            Preconditions.checkState(type != null,
                    "No column type found for %s as specified for column %s", metaData.getColumnType(i), metaData.getColumnName(i));

            Column<?> newColumn = type.create(metaData.getColumnName(i));
            table.addColumns(newColumn);
        }

        // Add the rows
        while (resultSet.next()) {
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                Column<?> column = table.column(i - 1); // subtract 1 because results sets originate at 1 not 0
                if (column instanceof ShortColumn) {
                    column.appendObj(resultSet.getShort(i));
                } else if (column instanceof IntColumn) {
                    column.appendObj(resultSet.getInt(i));
                } else if (column instanceof LongColumn) {
                    column.appendObj(resultSet.getLong(i));
                } else if (column instanceof FloatColumn) {
                    column.appendObj(resultSet.getFloat(i));
                } else if (column instanceof DoubleColumn) {
                    column.appendObj(resultSet.getDouble(i));
                } else {
                    column.appendObj(resultSet.getObject(i));
                }
            }
        }
        return table;
    }
    
    protected static ColumnType getColumnType(int columnType, int scale, int precision) {
        ColumnType type = SQL_TYPE_TO_TABLESAW_TYPE.get(columnType);
        // Try to improve on the initial type assigned to 'type' to minimize size/space of type needed.
        // For all generic numeric columns inspect closer, checking the precision and 
        // scale to more accurately determine the appropriate java type to use.
        if (columnType == Types.NUMERIC || columnType == Types.DECIMAL) {
            // When scale is 0 then column is a type of integer
            if (scale == 0) {
/* Mapping to java integer types based on integer precision defined:

Java type           TypeMinVal              TypeMaxVal          p               MaxIntVal
-----------------------------------------------------------------------------------------
byte, Byte:         -128                    127                 NUMBER(2)       99
short, Short:       -32768                  32767               NUMBER(4)       9_999
int, Integer:       -2147483648             2147483647          NUMBER(9)       999_999_999 
long, Long:         -9223372036854775808    9223372036854775807 NUMBER(18)      999_999_999_999_999_999

*/
                if (precision > 0) {
                    if (precision <= 4) {
                        // Start with SHORT (since ColumnType.BYTE isn't supported yet)
                        // and find the smallest java integer type that fits
                        type = ColumnType.SHORT;
                    } else if (precision <= 9) {
                        type = ColumnType.INTEGER;
                    } else if (precision <= 18) {
                        type = ColumnType.LONG;
                    }
                }
            } else { // s is not zero, so a decimal value is expected. First try float, then double
                if (scale <= 7) {
                    type = ColumnType.FLOAT;
                } else if (scale <= 16) {
                    type = ColumnType.DOUBLE;
                }
            }
        }
        return type;
    }

}