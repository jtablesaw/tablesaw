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

package tech.tablesaw.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Test database using H2 in-memory database
 *
 * <p>Derived mainly from a tutorial by:
 *
 * @author John J. Couture
 * @version 1.01 - 04/07/2014
 * @email jcouture@sdccd.edu
 */
public class TestDb {
  public TestDb() {
    try {
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

      // Build the OracleNumbers table.
      buildNumbersTable(conn);

      // Close the connection.
      conn.close();
    } catch (Exception e) {
      System.out.println("Error Creating the Coffee Table");
      System.out.println(e.getMessage());
    }
  }

  /** The dropTables method drops any existing in case the database already exists. */
  public static void dropTables(Connection conn) {
    try {
      // Get a Statement object.
      Statement stmt = conn.createStatement();

      try {
        // Drop the UnpaidOrder table.
        stmt.execute("DROP TABLE Unpaidorder");
      } catch (SQLException ex) {
        // No need to report an error.
        // The table simply did not exist.
      }

      try {
        // Drop the Customer table.
        stmt.execute("DROP TABLE Customer");
      } catch (SQLException ex) {
        // No need to report an error.
        // The table simply did not exist.
      }

      try {
        // Drop the Coffee table.
        stmt.execute("DROP TABLE Coffee");
      } catch (SQLException ex) {
        // No need to report an error.
        // The table simply did not exist.
      }
      try {
        // Drop the OracleNumbers table.
        stmt.execute("DROP TABLE OracleNumbers");
      } catch (SQLException ex) {
        // No need to report an error.
        // The table simply did not exist.
      }

      try {
        // Drop the NullValues table.
        stmt.execute("DROP TABLE NullValues");
      } catch (SQLException ex) {
        // No need to report an error.
        // The table simply did not exist.
      }
    } catch (SQLException ex) {
      System.out.println("ERROR: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  /** The buildCoffeeTable method creates the Coffee table and adds some rows to it. */
  public static void buildCoffeeTable(Connection conn) {
    try {
      // Get a Statement object.
      Statement stmt = conn.createStatement();

      // Create the table.
      stmt.execute(
          "CREATE TABLE Coffee ("
              + "Description CHAR(25), "
              + "ProdNum CHAR(10) NOT NULL PRIMARY KEY, "
              + "Price DOUBLE, "
              + "Imported BOOLEAN"
              + ")");

      // Insert row #1.
      stmt.execute(
          "INSERT INTO Coffee VALUES ( "
              + "'Bolivian Dark', "
              + "'14-001', "
              + "8.95, "
              + "TRUE )");

      // Insert row #2.
      stmt.execute(
          "INSERT INTO Coffee VALUES ( "
              + "'Bolivian Medium', "
              + "'14-002', "
              + "8.95, "
              + "TRUE )");

      // Insert row #3.
      stmt.execute(
          "INSERT INTO Coffee VALUES ( "
              + "'Brazilian Dark', "
              + "'15-001', "
              + "7.95, "
              + "TRUE )");

      // Insert row #4.
      stmt.execute(
          "INSERT INTO Coffee VALUES ( "
              + "'Brazilian Medium', "
              + "'15-002', "
              + "7.95, "
              + "TRUE )");

      // Insert row #5.
      stmt.execute(
          "INSERT INTO Coffee VALUES ( "
              + "'Brazilian Decaf', "
              + "'15-003', "
              + "8.55, "
              + "TRUE )");

      // Insert row #6.
      stmt.execute(
          "INSERT INTO Coffee VALUES ( "
              + "'Central American Dark', "
              + "'16-001', "
              + "9.95, "
              + "FALSE )");

      // Insert row #7.
      stmt.execute(
          "INSERT INTO Coffee VALUES ( "
              + "'Central American Medium', "
              + "'16-002', "
              + "9.95, "
              + "FALSE )");

      // Insert row #8.
      stmt.execute(
          "INSERT INTO Coffee VALUES ( " + "'Sumatra Dark', " + "'17-001', " + "7.95, " + "TRUE )");

      // Insert row #9.
      stmt.execute(
          "INSERT INTO Coffee VALUES ( "
              + "'Sumatra Decaf', "
              + "'17-002', "
              + "8.95, "
              + "TRUE )");

      // Insert row #10.
      stmt.execute(
          "INSERT INTO Coffee VALUES ( "
              + "'Sumatra Medium', "
              + "'17-003', "
              + "7.95, "
              + "TRUE )");

      // Insert row #11.
      stmt.execute(
          "INSERT INTO Coffee VALUES ( "
              + "'Sumatra Organic Dark', "
              + "'17-004', "
              + "11.95, "
              + "TRUE )");

      // Insert row #12.
      stmt.execute(
          "INSERT INTO Coffee VALUES ( " + "'Kona Medium', " + "'18-001', " + "18.45, " + "TRUE )");

      // Insert row #13.
      stmt.execute(
          "INSERT INTO Coffee VALUES ( " + "'Kona Dark', " + "'18-002', " + "18.45, " + "TRUE )");

      // Insert row #14.
      stmt.execute(
          "INSERT INTO Coffee VALUES ( "
              + "'French Roast Dark', "
              + "'19-001', "
              + "9.65, "
              + "TRUE )");

      // Insert row #15.
      stmt.execute(
          "INSERT INTO Coffee VALUES ( "
              + "'Galapagos Medium', "
              + "'20-001', "
              + "6.85, "
              + "TRUE )");

      // Insert row #16.
      stmt.execute(
          "INSERT INTO Coffee VALUES ( "
              + "'Guatemalan Dark', "
              + "'21-001', "
              + "9.95, "
              + "TRUE )");

      // Insert row #17.
      stmt.execute(
          "INSERT INTO Coffee VALUES ( "
              + "'Guatemalan Decaf', "
              + "'21-002', "
              + "10.45, "
              + "TRUE )");

      // Insert row #18.
      stmt.execute(
          "INSERT INTO Coffee VALUES ( "
              + "'Guatemalan Medium', "
              + "'21-003', "
              + "9.95, "
              + "TRUE )");
    } catch (SQLException ex) {
      System.out.println("ERROR: " + ex.getMessage());
    }
  }

  /** The buildCustomerTable method creates the Customer table and adds some rows to it. */
  public static void buildCustomerTable(Connection conn) {
    try {
      // Get a Statement object.
      Statement stmt = conn.createStatement();

      // Create the table.
      stmt.execute(
          "CREATE TABLE Customer"
              + "( CustomerNumber CHAR(10) NOT NULL PRIMARY KEY, "
              + "  Name CHAR(25),"
              + "  RegistrationDate DATE,"
              + "  Address CHAR(25),"
              + "  City CHAR(12),"
              + "  State CHAR(2),"
              + "  Zip CHAR(5) )");

      // Add some rows to the new table.
      stmt.executeUpdate(
          "INSERT INTO Customer VALUES"
              + "('101', 'Downtown Cafe', '2004-01-29', '17 N. Main Street',"
              + " 'Asheville', 'NC', '55515')");

      stmt.executeUpdate(
          "INSERT INTO Customer VALUES"
              + "('102', 'Main Street Grocery', '2005-02-10',"
              + " '110 E. Main Street',"
              + " 'Canton', 'NC', '55555')");

      stmt.executeUpdate(
          "INSERT INTO Customer VALUES"
              + "('103', 'The Coffee Place', '2006-08-31', '101 Center Plaza',"
              + " 'Waynesville', 'NC', '55516')");
    } catch (SQLException ex) {
      System.out.println("ERROR: " + ex.getMessage());
    }
  }

  /** The buildUnpaidOrderTable method creates the UnpaidOrder table. */
  public static void buildUnpaidOrderTable(Connection conn) {
    try {
      // Get a Statement object.
      Statement stmt = conn.createStatement();

      // Create the table.
      stmt.execute(
          "CREATE TABLE UnpaidOrder "
              + "( CustomerNumber CHAR(10) NOT NULL REFERENCES Customer(CustomerNumber), "
              + "  ProdNum CHAR(10) NOT NULL REFERENCES Coffee(ProdNum),"
              + "  OrderDate CHAR(10),"
              + "  Quantity DOUBLE,"
              + "  Cost DOUBLE )");
    } catch (SQLException ex) {
      System.out.println("ERROR: " + ex.getMessage());
    }
  }

  /** The buildNumbersTable method creates the Numbers table and adds some rows to it. */
  public static void buildNumbersTable(Connection conn) {
    try {
      // Get a Statement object.
      Statement stmt = conn.createStatement();
      // Create the table.
      stmt.execute(
          "CREATE TABLE Numbers ("
              + "Description CHAR(25), "
              + "NumInt NUMBER(9), "
              + "NumInt6_0 NUMBER(6,0), "
              + "NumLong NUMBER(10), "
              + "NumShort NUMBER(4), "
              + "NumNumber NUMBER(38), "
              + "NumBigInt NUMBER(38), "
              + "NumBigDec NUMBER(38), "
              + "NumFloat NUMBER(19,4), "
              + "NumFloat7_1 NUMBER(7,1), "
              + "NumFloat7_7 NUMBER(12,7), "
              + "NumDouble7_8 NUMBER(13,8), "
              + "NumDouble7_16 NUMBER(21,16)"
              + ")");

      // Insert row #1.
      stmt.execute(
          "INSERT INTO Numbers VALUES ( "
              + "'RowOne', "
              + "99999, "
              + "999999, "
              + "2000000000, "
              + "5555, "
              + "111111111133333333334444444444, "
              + "555555555566666666667777777777, "
              + "888888888899999999990000000000, "
              + "911222333444555.6677, "
              + "77777.1, "
              + "77777.1234567, "
              + "77777.12345678, "
              + "77777.1234567890123456)");

      // Insert row #2.
      stmt.execute(
          "INSERT INTO Numbers VALUES ( "
              + "'RowTwo', "
              + "89999, "
              + "900001, "
              + "3000000000, "
              + "4555, "
              + "911111111133333333334444444444, "
              + "455555555566666666667777777777, "
              + "788888888899999999990000000000, "
              + "911222333444555.6667, "
              + "67777.1, "
              + "67777.1234567, "
              + "67777.12345678, "
              + "67777.1234567890123456)");

      // Insert row #3.
      stmt.execute(
          "INSERT INTO Numbers VALUES ( "
              + "'RowThree', "
              + "79999, "
              + "800001, "
              + "2000000000, "
              + "3555, "
              + "811111111133333333334444444444, "
              + "355555555566666666667777777777, "
              + "688888888899999999990000000000, "
              + "811222333444555.6667, "
              + "57777.1, "
              + "57777.1234567, "
              + "57777.12345678, "
              + "57777.1234567890123456)");
    } catch (SQLException ex) {
      System.out.println("ERROR: " + ex.getMessage());
    }
  }

  /** The buildNullValues method creates the NullValues table and adds some rows to it. */
  public static void buildNullValuesTable(Connection conn) {
    try {
      // Get a Statement object.
      Statement stmt = conn.createStatement();

      // Create the table.
      stmt.execute(
          "CREATE TABLE NullValues ("
              + "StringValue CHAR(25), "
              + "PrimaryValue CHAR(10) NOT NULL PRIMARY KEY, "
              + "DoubleValue DOUBLE, "
              + "IntegerValue INTEGER, "
              + "ShortValue SMALLINT, "
              + "LongValue BIGINT, "
              + "FloatValue FLOAT, "
              + "BooleanValue Boolean"
              + ")");

      // Insert row #1.
      stmt.execute(
          "INSERT INTO NullValues VALUES ( "
              + "'Non Null Description', "
              + "'001', "
              + "8.95, "
              + "1, "
              + "1, "
              + "1, "
              + "3.14, "
              + "TRUE )");

      // Insert row #2.
      stmt.execute(
          "INSERT INTO NullValues (PrimaryValue, IntegerValue) VALUES ( " + "'002', " + "2 )");

      // Insert row #3.
      stmt.execute(
          "INSERT INTO NullValues (StringValue, PrimaryValue) VALUES ( "
              + "'Non Null Description', "
              + "'003')");
    } catch (SQLException ex) {
      System.out.println("ERROR: " + ex.getMessage());
    }
  }
}
