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

import org.junit.Assert;
import org.junit.Test;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.jdbc.SqlResultSetReader;

import static tech.tablesaw.util.TestDb.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Tests for creating Tables from JDBC result sets using SqlResutSetReader
 */
public class SqlResultSetReaderTest {

    @Test
    public void testSqlResultSetReader() throws Exception {

        // Create a named constant for the URL.
        // NOTE: This value is specific for H2 in-memory DB.
        final String DB_URL = "jdbc:h2:mem:CoffeeDB";

        // Create a connection to the database.
        Connection conn = DriverManager.getConnection(DB_URL);

        // If the DB already exists, drop the tables.
        dropTables(conn);

        // Build the Coffee table.
        buildCoffeeTable(conn);

        // Build the Customer table.
        buildCustomerTable(conn);

        // Build the UnpaidInvoice table.
        buildUnpaidOrderTable(conn);

        try (Statement stmt = conn.createStatement()) {
            String sql;
            sql = "SELECT * FROM coffee";
            try (ResultSet rs = stmt.executeQuery(sql)) {
                Table coffee = SqlResultSetReader.read(rs, "Coffee");
                Assert.assertEquals(4, coffee.columnCount());
                Assert.assertEquals(18, coffee.rowCount());
            }

            sql = "SELECT * FROM Customer";
            try (ResultSet rs = stmt.executeQuery(sql)) {
                Table customer = SqlResultSetReader.read(rs, "Customer");
                Assert.assertEquals(6, customer.columnCount());
                Assert.assertEquals(3, customer.rowCount());
            }

            sql = "SELECT * FROM UnpaidOrder";
            try (ResultSet rs = stmt.executeQuery(sql)) {
                Table unpaidInvoice = SqlResultSetReader.read(rs, "Unpaid Invoice");
                Assert.assertEquals(5, unpaidInvoice.columnCount());
                Assert.assertEquals(0, unpaidInvoice.rowCount());
            }
        }
    }
}