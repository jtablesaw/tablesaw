package tech.tablesaw.joining;

import org.junit.Test;

import com.google.common.base.Joiner;

import tech.tablesaw.api.Table;

import static org.junit.Assert.*;

import java.util.Arrays;

public class DataFrameJoinerTest {

    private static final Table ONE_YEAR = Table.read().csv(Joiner.on(System.lineSeparator()).join(
                "Date,1 Yr Treasury Rate",
                "\"Dec 1, 2017\",1.65%",
                "\"Nov 1, 2017\",1.56%",
                "\"Oct 1, 2017\",1.40%",
                "\"Sep 1, 2017\",1.28%",
                "\"Aug 1, 2017\",1.23%",
                "\"Jul 1, 2017\",1.22%"),
            "1 Yr Treasury Rate");

    private static final Table SP500 = Table.read().csv(Joiner.on(System.lineSeparator()).join(
                "Date,S&P 500",
                "\"Nov 1, 2017\",2579.36",
                "\"Oct 1, 2017\",2521.20",
                "\"Sep 1, 2017\",2474.42",
                "\"Aug 1, 2017\",2477.10",
                "\"Jul 1, 2017\",2431.39",
                "\"Jun 1, 2017\",2430.06"),
            "S&P 500");

    private static final Table ANIMAL_NAMES = Table.read().csv(Joiner.on(System.lineSeparator()).join(
                "Animal,Name",
                "Pig,Bob",
                "Pig,James",
                "Horse,David",
                "Goat,Samantha",
                "Tigon,Rudhrani",
                "Rabbit,Taylor"),
            "Animal Names");

    private static final Table ANIMAL_FEED = Table.read().csv(Joiner.on(System.lineSeparator()).join(
                "Animal,Feed",
                "Pig,Mush",
                "Horse,Hay",
                "Goat,Anything",
                "Guanaco,Grass",
                "Monkey,Banana"),
            "Animal Feed");

    private static final Table DOUBLE_INDEXED_PEOPLE = Table.read().csv(Joiner.on(System.lineSeparator()).join(
                "ID,Name",
                "1.0,Bob",
                "2.0,James",
                "3.0,David",
                "4.0,Samantha"),
            "People");

    private static final Table DOUBLE_INDEXED_DOGS = Table.read().csv(Joiner.on(System.lineSeparator()).join(
                "ID,Dog Name",
                "1.0,Spot",
                "3.0,Fido",
                "4.0,Sasha",
                "5.0,King"),
            "Dogs");

    private static final Table DUPLICATE_COL_NAME_DOGS = Table.read().csv(Joiner.on(System.lineSeparator()).join(
                "ID,Dog Name, Good",
                "1.0,Spot,true",
                "3.0,Fido,true",
                "4.0,Sasha,true",
                "5.0,King,true",
                "1.0,Spot,false",
                "3.0,Fido,false",
                "4.0,Sasha,false",
                "5.0,King,false"),
            "Dogs");

    private static Table createSTUDENT() {
        return Table.read().csv(Joiner.on(System.lineSeparator()).join(
                "ID,FirstName,LastName,City,State,Age,USID,GradYear",
                "1,Bob,Barney,Burke,VA,17,11122,2019",
                "2,Chris,Cabello,Canyonville,OR,17,22224,2019",
                "3,Dan,Dirble,Denver,CO,17,33335,2020",
                "4,Edward,Earhardt,Easterly,WA,18,44339,2021",
                "5,Frank,Farnsworth,Fredriksburg,VA,18,55338,2019",
                "6,George,Gabral,Garrisburg,MD,19,66337,2020",
                "7,Michael,Marbury,Milton,NH,19,77330,2020",
                "8,Robert,Riley,Roseburg,OR,19,88836,2020",
                "9,Bob,Earhardt,Milton,NH,50,93333,2019",
                "10,Dan,Gabral,Easterly,WA,20,13333,2020"),
            "Student");
    }

    private static Table createINSTRUCTOR() {
        return Table.read().csv(Joiner.on(System.lineSeparator()).join(
                "ID,First,Last,Title,City,State,Age,USID,GradYear",
                "1,Bob,Cabello,Prof,Burke,VA,20,11333,2019",
                "2,Chris,Barney,TA,Canyonville,OR,20,22334,2019",
                "3,Dan,Earhardt,Instructor,Denver,CO,22,33335,2020",
                "4,Edward,Dirble,Prof,Easterly,WA,22,43339,2020",
                "5,Farnsworth,Frank,Prof,Fredriksburg,VA,22,55338,2019",
                "6,Gabral,George,TA,Garrisburg,MD,18,66337,2019",
                "7,Robert,Marbury,TA,Msilton,NH,23,73330,2020",
                "8,Michael,Riley,TA,Roseburg,OR,23,88336,2020",
                "9,Bob,Riley,Prof,Milton,NH,50,99933,2020",
                "10,Earhardt,Gabral,Prof,Easterly,WA,24,13333,2019"),
            "Instructor");
    }

    private static Table createDEPTHEAD() {
        return Table.read().csv(Joiner.on(System.lineSeparator()).join(
                    "ID,First,Last,Dept,City,State,Age",
                    "1,John,Cabello,ComputerScience,Burke,VA,20",
                    "2,Samantha,Barney,Writing,Canyonville,OR,18",
                    "3,Mark,Earhardt,Mathematics,Denver,CO,35",
                    "4,Christie,Dirble,Architecture,Easterly,WA,36",
                    "5,Bhawesh,Frank,Psychology,Fredriksburg,VA,37",
                    "6,Robert,George,Sociology,Garrisburg,MD,38",
                    "7,George,Marbury,Physics,Msilton,NH,39",
                    "8,Zhongyu,Riley,Chemistry,Roseburg,OR,40",
                    "9,Laura,Riley,Economics,Milton,NH,50",
                    "10,Sally,Gabral,Marketing,Easterly,WA,42"),
                "DepartmentHead");
    }

    private static Table createCLASS() {
        return Table.read().csv(Joiner.on(System.lineSeparator()).join(
                "ID,ClassType,Name,Level,Description,StartDate,EndDate,Completed,Age",
                "1001,Math,Calculus,101,Newton math,2018-09-20,2018-12-17,false,20",
                "1002,Math,Calculus,102,Newton math,2019-01-06,2019-03-06,false,18",
                "1003,Math,Calculus,103,Newton math,2019-03-10,2019-06-17,false,18",
                "1004,Writing,Composition,101,Writing papers,2018-09-20,2018-12-17,false,23",
                "1005,Writing,Composition,102,Writing papers,2019-01-06,2019-03-07,false,24",
                "1006,Software,Programming,101,Programming basics,2018-09-22,2018-12-15,false,25",
                "1007,Software,Programming,102,Programming basics,2019-01-05,2019-03-07,false,26",
                "1008,Economics,Microeconomics,101,Basic micro economics,2018-09-20,2018-12-17,false,27",
                "1009,Economics,Microeconomics,102,Basic micro economics,2018-01-05,2019-03-07,false,28",
                "1010,Literature,Shakespeare,101,Understanding Shakespeare,2018-09-20,2018-12-17,false,50"),
            "Class");
    }

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

    @Test
    public void innerJoinStudentInstructorOnAge() {
        Table STUDENT = createSTUDENT();
        Table INSTRUCTOR = createINSTRUCTOR();
        Table joined = STUDENT.join("Age").inner(true, INSTRUCTOR);
        assert(joined.columnNames().containsAll(Arrays.asList(new String[] {
                "T2.ID", "T2.City", "T2.State", "T2.USID", "T2.GradYear"})));
        assertEquals(16, joined.columnCount());
        assertEquals(5, joined.rowCount());
    }    

    @Test
    public void innerJoinInstructorStudentOnAge() {
        Table STUDENT = createSTUDENT();
        Table INSTRUCTOR = createINSTRUCTOR();
        Table joined = INSTRUCTOR.join("Age").inner(true, STUDENT);
        assert(joined.columnNames().containsAll(Arrays.asList(new String[] {
                "T2.ID", "T2.City", "T2.State", "T2.USID", "T2.GradYear"})));
        assertEquals(16, joined.columnCount());
        assertEquals(5, joined.rowCount());
    }    

    @Test
    public void innerJoinStudentInstructorClassOnAge() {
        Table STUDENT = createSTUDENT();
        Table INSTRUCTOR = createINSTRUCTOR();
        Table CLASS = createCLASS();
        Table joined = STUDENT.join("Age").inner(true, INSTRUCTOR,CLASS);
        assert(joined.columnNames().containsAll(Arrays.asList(new String[] {
                "T2.ID", "T2.City", "T2.State", "T2.USID", "T2.GradYear",
        "T3.ID"})));
        assertEquals(24, joined.columnCount());
        assertEquals(7, joined.rowCount());
    }

    @Test
    public void innerJoinStudentInstructorClassDeptHeadOnAge() {
        Table STUDENT = createSTUDENT();
        Table INSTRUCTOR = createINSTRUCTOR();
        Table CLASS = createCLASS();
        Table DEPTHEAD = createDEPTHEAD();
        Table joined = STUDENT.join("Age").inner(true, INSTRUCTOR,CLASS,DEPTHEAD);
        assert(joined.columnNames().containsAll(Arrays.asList(new String[] {
                "T2.ID", "T2.City", "T2.State", "T2.USID", "T2.GradYear",
                "T3.ID", "T4.ID", "T4.First", "T4.Last", "T4.City", "T4.State"})));
        assertEquals(30, joined.columnCount());
        assertEquals(7, joined.rowCount());
    }

}
