package tech.tablesaw.table;

import org.junit.Assert;
import org.junit.Test;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;

public class SliceBugTests {

    private final Integer[] observations = new Integer[]{10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 10};

    private final LocalDateTime[] timestamps = new LocalDateTime[]{
            LocalDateTime.of(2018, 1, 1, 13, 1, 1),
            LocalDateTime.of(2018, 1, 1, 13, 1, 2),
            LocalDateTime.of(2018, 1, 1, 13, 1, 2),
            LocalDateTime.of(2018, 1, 1, 13, 1, 3),
            LocalDateTime.of(2018, 1, 1, 13, 1, 3),
            LocalDateTime.of(2018, 1, 1, 13, 1, 4),
            LocalDateTime.of(2018, 1, 1, 13, 1, 5),
            LocalDateTime.of(2018, 1, 1, 13, 1, 6),
            LocalDateTime.of(2018, 1, 1, 13, 1, 6),
            LocalDateTime.of(2018, 1, 1, 13, 1, 7)
    };
    
    private final String[] categories = new String[]{
            "Australia",
            "Australia",
            "Australia",
            "Germany",
            "USA",
            "Finland",
            "Finland",
            "Japan",
            "Japan",
            "Chile"
    };


    @Test
    public void sliceColumnIsSameWhenRetrievedWithNameOrIndex() {
        Table table = constructTableFromArrays();

        TableSliceGroup countrySplit = table.splitOn("countries");

        for (TableSlice slice : countrySplit) {
            NumberColumn priceColFromIndex = slice.numberColumn(2);
            NumberColumn priceColFromName = slice.numberColumn("price");

            Assert.assertTrue("Columns should have same data",
                    Arrays.equals(priceColFromName.asDoubleArray(), priceColFromIndex.asDoubleArray()));
        }
    }

    @Test
    public void sliceAsTableUsingDatesAfterFilteringDBLoadedTable() throws SQLException {
        Table salesTable = loadTableFromDB();

        Table filteredTable = salesTable.select(salesTable.columnNames().toArray(new String[0]))
                .where(salesTable.dateTimeColumn("sale_timestamp")
                        .isAfter(LocalDateTime.of(2018, 1, 1, 13, 1, 3)
                        ));
        filteredTable.setName("filteredTable");

        // work around
        TableSliceGroup slices = filteredTable.splitOn("countries");
        slices.forEach(slice -> {
            assertFalse(slice.isEmpty());
        });
    }

    private Table loadTableFromDB() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1");
        String create = "CREATE TABLE country_sales (" +
                "countries VARCHAR(255)," +
                "sale_timestamp TIMESTAMP," +
                "price INTEGER" +
                ");";
        Statement statement = connection.createStatement();
        statement.executeUpdate(create);

        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO country_sales (countries, sale_timestamp, price) VALUES (?, ?, ?);");

        for (int i = 0; i < categories.length; i++) {
            preparedStatement.setString(1, categories[i]);
            preparedStatement.setObject(2, timestamps[i]);
            preparedStatement.setInt(3, observations[i]);
            preparedStatement.executeUpdate();
        }
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM country_sales");

        return Table.read().db(resultSet, "sales_from_db");
    }

    private Table constructTableFromArrays() {
        StringColumn countries = StringColumn.create("countries", categories);
        DateTimeColumn timestamp = DateTimeColumn.create("sale_timestamp", timestamps);
        DoubleColumn values = DoubleColumn.create("price", observations);

        return Table.create("table_from_arrays", countries, timestamp, values);
    }
}
