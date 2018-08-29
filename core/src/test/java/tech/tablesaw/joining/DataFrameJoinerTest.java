package tech.tablesaw.joining;

import org.junit.Test;
import tech.tablesaw.api.Table;

import static org.junit.Assert.*;

import java.util.Arrays;

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

    private static Table create_STUDENT() {
    	return Table.read().csv(
            "ID,FirstName,LastName,City,State,Age,USID,GradYear\n"
                    + "1,Bob,Barney,Burke,VA,20,11122,2019\n"
                    + "2,Chris,Cabello,Canyonville,OR,20,22224,2019\n"
                    + "3,Dan,Dirble,Denver,CO,21,33335,2020\n"
                    + "4,Edward,Earhardt,Easterly,WA,21,44339,2021\n"
                    + "5,Frank,Farnsworth,Fredriksburg,VA,22,55338,2019\n"
                    + "6,George,Gabral,Garrisburg,MD,22,66337,2020\n"
                    + "7,Michael,Marbury,Milton,NH,23,77330,2020\n"
                    + "8,Robert,Riley,Roseburg,OR,23,88836,2020\n"
                    + "9,Bob,Earhardt,Milton,NH,24,93333,2019\n"
                    + "10,Dan,Gabral,Easterly,WA,24,13333,2020\n",
            "Student");
    }

    private static Table create_INSTRUCTOR() {
    	return Table.read().csv(
            "ID,First,Last,Title,City,State,Age,USID,GradYear\n"
                    + "1,Bob,Cabello,Prof,Burke,VA,20,11333,2019\n"
                    + "2,Chris,Barney,TA,Canyonville,OR,20,22334,2019\n"
                    + "3,Dan,Earhardt,Instructor,Denver,CO,19,33335,2020\n"
                    + "4,Edward,Dirble,Prof,Easterly,WA,22,43339,2020\n"
                    + "5,Farnsworth,Frank,Prof,Fredriksburg,VA,18,55338,2019\n"
                    + "6,Gabral,George,TA,Garrisburg,MD,20,66337,2019\n"
                    + "7,Robert,Marbury,TA,Msilton,NH,22,73330,2020\n"
                    + "8,Michael,Riley,TA,Roseburg,OR,22,88336,2020\n"
                    + "9,Bob,Riley,Prof,Milton,NH,19,99933,2020\n"
                    + "10,Earhardt,Gabral,Prof,Easterly,WA,21,13333,2019\n",
            "Instructor");
    }

    
    private static Table create_DEPTHEAD() {
    	return Table.read().csv(
    			"ID,First,Last,Dept,City,State,Age\n"
    					+ "1,John,Cabello,ComputerScience,Burke,VA,20\n"
    					+ "2,Samantha,Barney,Writing,Canyonville,OR,21\n"
    					+ "3,Mark,Earhardt,Mathematics,Denver,CO,22\n"
    					+ "4,Christie,Dirble,Architecture,Easterly,WA,23\n"
    					+ "5,Bhawesh,Frank,Psychology,Fredriksburg,VA,24\n"
    					+ "6,Robert,George,Sociology,Garrisburg,MD,25\n"
    					+ "7,George,Marbury,Physics,Msilton,NH,26\n"
    					+ "8,Zhongyu,Riley,Chemistry,Roseburg,OR,27\n"
    					+ "9,Laura,Riley,Economics,Milton,NH,28\n"
    					+ "10,Sally,Gabral,Marketing,Easterly,WA,29\n",
    			"DepartmentHead");
    }
    
    private static Table create_CLASS() {
    	return Table.read().csv(
            "ID,ClassType,Name,Level,Description,StartDate,EndDate,Completed,Age\n"
                    + "1001,Math,Calculus,101,Newton math,2018-09-20,2018-12-17,false,16\n"
                    + "1002,Math,Calculus,102,Newton math,2019-01-06,2019-03-06,false,17\n"
                    + "1003,Math,Calculus,103,Newton math,2019-03-10,2019-06-17,false,18\n"
                    + "1004,Writing,Composition,101,Writing papers,2018-09-20,2018-12-17,false,19\n"
                    + "1005,Writing,Composition,102,Writing papers,2019-01-06,2019-03-07,false,20\n"
                    + "1006,Software,Programming,101,Programming basics,2018-09-22,2018-12-15,false,21\n"
                    + "1007,Software,Programming,102,Programming basics,2019-01-05,2019-03-07,false,22\n"
                    + "1008,Economics,Microeconomics,101,Basic micro economics,2018-09-20,2018-12-17,false,23\n"
                    + "1009,Economics,Microeconomics,102,Basic micro economics,2018-01-05,2019-03-07,false,24\n"
                    + "1010,Literature,Shakespeare,101,Understanding Shakespeare,2018-09-20,2018-12-17,false,25\n",
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
    	Table STUDENT = create_STUDENT();
    	Table INSTRUCTOR = create_INSTRUCTOR();
    	Table joined = STUDENT.join("Age").inner(true, INSTRUCTOR);
    	assert(joined.columnNames().containsAll(Arrays.asList(new String[] {
    			"T2.ID", "T2.City", "T2.State", "T2.USID", "T2.GradYear"})));
    	assertEquals(16, joined.columnCount());
    	assertEquals(14, joined.rowCount());
    }    
    
    @Test
    public void innerJoinStudentInstructorClassOnAge() {
    	Table STUDENT = create_STUDENT();
    	Table INSTRUCTOR = create_INSTRUCTOR();
    	Table CLASS = create_CLASS();
    	Table joined = STUDENT.join("Age").inner(true, INSTRUCTOR,CLASS);
    	assert(joined.columnNames().containsAll(Arrays.asList(new String[] {
    			"T2.ID", "T2.City", "T2.State", "T2.USID", "T2.GradYear",
    	"T3.ID"})));
    	assertEquals(24, joined.columnCount());
    	assertEquals(14, joined.rowCount());
    }    
    
    @Test
    public void innerJoinStudentInstructorClassDeptHeadOnAge() {
    	Table STUDENT = create_STUDENT();
    	Table INSTRUCTOR = create_INSTRUCTOR();
    	Table CLASS = create_CLASS();
    	Table DEPTHEAD = create_DEPTHEAD();
    	Table joined = STUDENT.join("Age").inner(true, INSTRUCTOR,CLASS,DEPTHEAD);
    	assert(joined.columnNames().containsAll(Arrays.asList(new String[] {
    			"T2.ID", "T2.City", "T2.State", "T2.USID", "T2.GradYear",
    			"T3.ID", "T4.ID", "T4.First", "T4.Last", "T4.City", "T4.State"})));
    	assertEquals(30, joined.columnCount());
    	assertEquals(14, joined.rowCount());
    }
}
