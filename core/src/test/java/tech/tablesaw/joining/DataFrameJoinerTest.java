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
                    + "\"Pig\",Bob\n"
                    + "\"Pig\",James\n"
                    + "\"Horse\",David\n"
                    + "\"Goat\",Samantha\n",
            "Animal Names");

    private static final Table ANIMAL_FEED = Table.read().csv(
            "Animal,Feed\n"
                    + "\"Pig\",Mush\n"
                    + "\"Horse\",Hay\n"
                    + "\"Goat\",Anything\n",
            "Ainmal Feed");

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
                    + "4.0,Sasha\n",
            "Dogs");

    @Test
    public void innerJoinWithDoubles() {
        Table joined = DOUBLE_INDEXED_PEOPLE.join("ID").inner(DOUBLE_INDEXED_DOGS, "ID");
        assertEquals(3, joined.columnCount());
        assertEquals(3, joined.rowCount());
        System.out.println(joined);
    }

    @Test
    public void innerJoin() {
        Table joined = SP500.join("Date").inner(ONE_YEAR, "Date");
        assertEquals(3, joined.columnCount());
        assertEquals(5, joined.rowCount());
    }

    @Test
    public void innerJoin_duplicateKeysFirstTable() {
        Table joined = ANIMAL_NAMES.join("Animal").inner(ANIMAL_FEED, "Animal");
        assertEquals(3, joined.columnCount());
        assertEquals(4, joined.rowCount());
    }

    @Test
    public void innerJoin_duplicateKeysSecondTable() {
        Table joined = ANIMAL_FEED.join("Animal").inner(ANIMAL_NAMES, "Animal");
        assertEquals(3, joined.columnCount());
        assertEquals(4, joined.rowCount());
    }

}
