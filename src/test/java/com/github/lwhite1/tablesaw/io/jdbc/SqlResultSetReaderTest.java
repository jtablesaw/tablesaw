package com.github.lwhite1.tablesaw.io.jdbc;

import com.github.lwhite1.tablesaw.api.Table;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static com.github.lwhite1.tablesaw.util.TestDb.*;

/**
 *  Tests for creating Tables from JDBC result sets using SqlResutSetReader
 */
public class SqlResultSetReaderTest {


  public static void main(String[] args) throws Exception {

    // Create a named constant for the URL.
    // NOTE: This value is specific for Java DB.
    final String DB_URL = "jdbc:derby:CoffeeDB;createFromCsv=true";

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
      }

      sql = "SELECT * FROM Customer";
      try (ResultSet rs = stmt.executeQuery(sql)) {
        Table customer = SqlResultSetReader.read(rs, "Customer");
        System.out.println(customer.structure().print());
        System.out.println(customer.print());
      }

      sql = "SELECT * FROM UnpaidOrder";
      try (ResultSet rs = stmt.executeQuery(sql)) {
        Table unpaidInvoice = SqlResultSetReader.read(rs, "Unpaid Invoice");
        System.out.println(unpaidInvoice.structure().print());
        System.out.println(unpaidInvoice.print());
      }
    }
  }
}