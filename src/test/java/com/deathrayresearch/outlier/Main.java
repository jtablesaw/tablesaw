package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.columns.BooleanColumn;
import com.deathrayresearch.outlier.columns.CategoryColumn;
import com.deathrayresearch.outlier.columns.ColumnType;
import com.deathrayresearch.outlier.columns.IntColumn;
import com.deathrayresearch.outlier.io.CsvReader;
import com.deathrayresearch.outlier.io.CsvWriter;
import com.deathrayresearch.outlier.store.StorageManager;
import com.deathrayresearch.outlier.util.CsvCombiner;

import static com.deathrayresearch.outlier.QueryUtil.valueOf;

public class Main {

    public static void main(String[] args) throws Exception {

        Table queries = StorageManager.readTable("db/6252f750-c2e6-49d0-925e-330311db353a");

        out(queries.head(4).print());

        CategoryColumn query = queries.categoryColumn("Query");
        IntColumn executions = queries.intColumn("Executions");

        out("Total executions: " + executions.sum());
        out("Unique queries: " + query.countUnique());

        out("Max executions: " + executions.max());
        out("Min executions: " + executions.min());

        Table grouped = queries.sum(executions, query);
        out(grouped.rowCount());
        out("Max Grouped executions: " + grouped.intColumn("Sum").max());

        // grouped = grouped.sortDescendingOn("Sum");
        out("Grouped executions: " + grouped.intColumn("Sum").sum());
        CsvWriter.write("data/grouped.csv", grouped);

        StorageManager.saveTable("db-grouped", grouped);

        CategoryColumn q = grouped.categoryColumn("Group");
        // IntColumn queryExec = grouped.intColumn("Sum");

        System.out.println(grouped.head(10).print());

        BooleanColumn doctype = new BooleanColumn("doctype?", q.contains("documentype"), q.size());

        BooleanColumn th =
                new BooleanColumn("TransactionHistory", q.contains("TransactionHistory"), q.size());

        BooleanColumn sc =
                new BooleanColumn("ServiceConfiguration",
                        q.contains("ServiceConfiguration"), q.size());

        Relation f1 = queries.select().where(valueOf("Query").contains("XXBADXX")).run();
        out(f1.print());

        grouped.addColumn(doctype);
        grouped.addColumn(th);
        grouped.addColumn(sc);
        out(grouped.columnNames());
        out(grouped.head(100).print());

       // out(f1.intColumn(1).sum());
       // out(f1.head(100).print());

        System.exit(0);
    }

    public static void main2(String[] args) throws Exception {

        // Combine all the query CSVs in the folder into one by appending them
        CsvCombiner.readAll("/Users/lwhite/cloudseachqueries", "data/queries.csv", ',', true);

        // Read the combined CSV (we don't need the first column. It's empty)
        ColumnType[] columnTypes = {ColumnType.SKIP, ColumnType.CAT, ColumnType.INTEGER};
        Table queries = CsvReader.read("data/queries.csv", columnTypes);

        // Get the columns and give them better names
        CategoryColumn query = queries.categoryColumn("bq");
        IntColumn executions = queries.intColumn("Count");
        query.setName("Query");
        executions.setName("Executions");

        // Save the revised table in Tablesaw format
        StorageManager.saveTable("db", queries);

        // Peek at the data
        out(queries.head(4).print());
        out("Total executions: " + executions.sum());
        out("Unique queries: " + query.countUnique());
        out("Max executions: " + executions.max());
        out("Min executions: " + executions.min());

        // Combine duplicate queries into one, summing the executions from the originals
        Table grouped = queries.sum(executions, query);
        grouped = grouped.sortDescendingOn("Sum");

        StorageManager.saveTable("db-grouped", grouped);
    }

    private static void out(Object o) {System.out.println(String.valueOf(o));}
}
