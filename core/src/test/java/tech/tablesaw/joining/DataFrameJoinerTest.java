package tech.tablesaw.joining;

import org.junit.Test;
import tech.tablesaw.api.Table;

import static org.junit.Assert.*;

public class DataFrameJoinerTest {

    private static final Table ONE_YEAR = Table.read().csv(
            "Date,1 Yr Treasury Rate\n"
                    + "\"Dec 1, 2017\",1.65%\n"
                    + "\"Nov 1, 2017\",1.56%\n"
                    + "\"Oct 1, 2017\",1.40%\n"
                    + "\"Sep 1, 2017\",1.28%\n"
                    + "\"Aug 1, 2017\",1.23%\n"
                    + "\"Jul 1, 2017\",1.22%\n",
            "1 Yr Treasury Rate");

    private static final Table SP500 = Table.read().csv(
            "Date,S&P 500\n"
                    + "\"Nov 1, 2017\",2579.36\n"
                    + "\"Oct 1, 2017\",2521.20\n"
                    + "\"Sep 1, 2017\",2474.42\n"
                    + "\"Aug 1, 2017\",2477.10\n"
                    + "\"Jul 1, 2017\",2431.39\n"
                    + "\"Jun 1, 2017\",2430.06\n",
            "S&P 500");

    private static final Table ANIMAL_NAMES = Table.read().csv(
            "Animal,Name\n"
                    + "Pig,Bob\n"
                    + "Pig,James\n"
                    + "Horse,David\n"
                    + "Goat,Samantha\n"
                    + "Tigon,Rudhrani\n"
                    + "Rabbit,Taylor\n",
            "Animal Names");

    private static final Table ANIMAL_FEED = Table.read().csv(
            "Animal,Feed\n"
                    + "Pig,Mush\n"
                    + "Horse,Hay\n"
                    + "Goat,Anything\n"
                    + "Guanaco,Grass\n"
                    + "Monkey,Banana\n",
            "Animal Feed");

    private static final Table DOUBLE_INDEXED_PEOPLE = Table.read().csv(
            "ID,Name\n"
                    + "1.0,Bob\n"
                    + "2.0,James\n"
                    + "3.0,David\n"
                    + "4.0,Samantha\n",
            "People");

    private static final Table DOUBLE_INDEXED_DOGS = Table.read().csv(
            "ID,Dog Name\n"
                    + "1.0,Spot\n"
                    + "3.0,Fido\n"
                    + "4.0,Sasha\n"
                    + "5.0,King\n",
            "Dogs");

    private static final Table DUPLICATE_COL_NAME_DOGS = Table.read().csv(
            "ID,Dog Name, Good\n"
                    + "1.0,Spot,true\n"
                    + "3.0,Fido,true\n"
                    + "4.0,Sasha,true\n"
                    + "5.0,King,true\n"
                    + "1.0,Spot,false\n"
                    + "3.0,Fido,false\n"
                    + "4.0,Sasha,false\n"
                    + "5.0,King,false\n",
            "Dogs");

    @Test
    public void innerJoinWithDoubles() {
        Table joined = DOUBLE_INDEXED_PEOPLE.join("ID").inner(DOUBLE_INDEXED_DOGS, "ID");
        assertEquals(3, joined.columnCount());
        assertEquals(3, joined.rowCount());
    }

    @Test
    public void innerJoinWithDuplicateColumnNames() {
        Table table1 = DUPLICATE_COL_NAME_DOGS.where(DUPLICATE_COL_NAME_DOGS.booleanColumn("Good").isTrue());
        Table table2 = DUPLICATE_COL_NAME_DOGS.where(DUPLICATE_COL_NAME_DOGS.booleanColumn("Good").isFalse());

        Table joined = table1.join("ID").inner(table2, "ID", true);
        assertEquals(5, joined.columnCount());
        assertEquals(4, joined.rowCount());
    }

    @Test
    public void rightOuterJoinWithDuplicateColumnNames() {
        Table table1 = DUPLICATE_COL_NAME_DOGS.where(DUPLICATE_COL_NAME_DOGS.booleanColumn("Good").isTrue());
        Table table2 = DUPLICATE_COL_NAME_DOGS.where(DUPLICATE_COL_NAME_DOGS.booleanColumn("Good").isFalse());

        Table joined = table1.join("ID").rightOuter(table2, "ID", true);
        assertEquals(5, joined.columnCount());
        assertEquals(4, joined.rowCount());
    }

    @Test
    public void leftOuterJoinWithDuplicateColumnNames() {
        Table table1 = DUPLICATE_COL_NAME_DOGS.where(DUPLICATE_COL_NAME_DOGS.booleanColumn("Good").isTrue());
        Table table2 = DUPLICATE_COL_NAME_DOGS.where(DUPLICATE_COL_NAME_DOGS.booleanColumn("Good").isFalse());

        Table joined = table1.join("ID").leftOuter(table2, "ID", true);
        assertEquals(5, joined.columnCount());
        assertEquals(4, joined.rowCount());
    }

    @Test
    public void leftOuterJoinWithDoubles() {
        Table joined = DOUBLE_INDEXED_PEOPLE.join("ID").leftOuter(DOUBLE_INDEXED_DOGS, "ID");
        assertEquals(3, joined.columnCount());
        assertEquals(4, joined.rowCount());
    }

    @Test
    public void rightOuterJoinWithDoubles() {
        Table joined = DOUBLE_INDEXED_PEOPLE.join("ID").rightOuter(DOUBLE_INDEXED_DOGS, "ID");
        assertEquals(3, joined.columnCount());
        assertEquals(4, joined.rowCount());
    }

    @Test
    public void leftOuterJoinWithDoubles2() {
        Table joined = DOUBLE_INDEXED_DOGS.join("ID").leftOuter(DOUBLE_INDEXED_PEOPLE, "ID");
        assertEquals(3, joined.columnCount());
        assertEquals(4, joined.rowCount());
    }

    @Test
    public void innerJoin() {
        Table joined = SP500.join("Date").inner(ONE_YEAR, "Date");
        assertEquals(3, joined.columnCount());
        assertEquals(5, joined.rowCount());
    }

    @Test
    public void leftOuterJoin() {
        Table joined = SP500.join("Date").leftOuter(ONE_YEAR, "Date");
        assertEquals(3, joined.columnCount());
        assertEquals(6, joined.rowCount());
    }

    @Test
    public void innerJoinDuplicateKeysFirstTable() {
        Table joined = ANIMAL_NAMES.join("Animal").inner(ANIMAL_FEED, "Animal");
        assertEquals(3, joined.columnCount());
        assertEquals(4, joined.rowCount());
    }

    @Test
    public void leftOuterJoinDuplicateKeysFirstTable() {
        Table joined = ANIMAL_NAMES.join("Animal").leftOuter(ANIMAL_FEED, "Animal");
        assertEquals(3, joined.columnCount());
        assertEquals(6, joined.rowCount());
    }

    @Test
    public void innerJoinDuplicateKeysSecondTable() {
        Table joined = ANIMAL_FEED.join("Animal").inner(ANIMAL_NAMES, "Animal");
        assertEquals(3, joined.columnCount());
        assertEquals(4, joined.rowCount());
    }

    @Test
    public void leftOuterJoinDuplicateKeysSecondTable() {
        Table joined = ANIMAL_FEED.join("Animal").leftOuter(ANIMAL_NAMES, "Animal");
        assertEquals(3, joined.columnCount());
        assertEquals(6, joined.rowCount());
    }

    @Test
    public void fullOuterJoin() {
        Table joined = ANIMAL_FEED.join("Animal").fullOuter(ANIMAL_NAMES, "Animal");
        assertEquals(3, joined.columnCount());
        assertEquals(8, joined.rowCount());
        assertEquals(8, joined.column("Animal").size());
        assertEquals(0, joined.column("Animal").countMissing());
        assertEquals(8, joined.column("Name").size());
        assertEquals(2, joined.column("Name").countMissing());
        assertEquals(8, joined.column("Feed").size());
        assertEquals(2, joined.column("Feed").countMissing());
    }

}
