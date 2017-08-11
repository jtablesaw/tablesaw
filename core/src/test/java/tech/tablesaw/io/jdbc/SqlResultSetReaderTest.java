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
                System.out.println(coffee.structure().print());
                System.out.println(coffee.print());
                Assert.assertEquals(4, coffee.columnCount());
                Assert.assertEquals(18, coffee.rowCount());
            }

            sql = "SELECT * FROM Customer";
            try (ResultSet rs = stmt.executeQuery(sql)) {
                Table customer = SqlResultSetReader.read(rs, "Customer");
                System.out.println(customer.structure().print());
                System.out.println(customer.print());
                Assert.assertEquals(6, customer.columnCount());
                Assert.assertEquals(3, customer.rowCount());
            }

            sql = "SELECT * FROM UnpaidOrder";
            try (ResultSet rs = stmt.executeQuery(sql)) {
                Table unpaidInvoice = SqlResultSetReader.read(rs, "Unpaid Invoice");
                System.out.println(unpaidInvoice.structure().print());
                System.out.println(unpaidInvoice.print());
                Assert.assertEquals(5, unpaidInvoice.columnCount());
                Assert.assertEquals(0, unpaidInvoice.rowCount());
            }
        }
    }
}