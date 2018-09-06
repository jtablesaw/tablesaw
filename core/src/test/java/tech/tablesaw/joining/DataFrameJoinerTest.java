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

    private static final Table DOUBLE_INDEXED_CATS = Table.read().csv(
            "ID,Cat Name\n"
                    + "1.0,Spot2\n"
                    + "2.0,Fido\n"
                    + "6.0,Sasha\n"
                    + "8.0,King2\n",
            "Cats");

    private static final Table DOUBLE_INDEXED_FISH = Table.read().csv(
            "ID,Fish Name\n"
                    + "11.0,Spot3\n"
                    + "2.0,Fido\n"
                    + "4.0,Sasha\n"
                    + "6.0,King2\n",
            "Fish");

    private static final Table DOUBLE_INDEXED_MICE = Table.read().csv(
            "ID,Mice_Name\n"
                    + "2.0,Jerry\n"
                    + "3.0,Fido\n"
                    + "6.0,Sasha\n"
                    + "9.0,Market\n",
            "Mice");

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

    private static Table createHOUSE() {
        return Table.read().csv(
                "Style,Bedrooms,BuildDate,Owner\n"
                        + "Colonial,3,1976-06-02,Smith\n"
                        + "Gambrel,4,1982-11-18,Jones\n"
                        + "Contemporary,5,1980-03-24,White\n"
                        + "Split,2,1970-09-30,Brown\n",
                "House");
    }

    private static Table createBOAT() {
        return Table.read().csv(
                "Type,Bedrooms,SoldDate,Owner\n"
                        + "Yacht,2,1970-02-03,Jones\n"
                        + "Dinghy,0,1988-12-12,Trump\n"
                        + "HouseBoat,3,1981-04-21,Smith\n"
                        + "Contemporary,5,1980-05-17,White\n"
                        + "Cruise,200,1989-01-23,Brown\n",
                "Boat");
    }

    private static Table createHOUSE10() {
        return Table.read().csv(
                "Style,Bedrooms,BuildDate,Owner\n"
                        + "Colonial,3,1976-06-02,Smith\n"
                        + "Gambrel,4,1982-11-18,Jones\n"
                        + "Contemporary,5,1980-03-24,White\n"
                        + "Ranch,4,1982-11-18,Black\n"
                        + "Victorian,5,1980-03-24,Red\n"
                        + "Split,2,1970-09-30,Brown\n",
                "House10");
    }

    private static Table createBOAT10() {
        return Table.read().csv(
                "Type,Bedrooms,SoldDate,Owner\n"
                        + "Yacht,2,1970-02-03,Jones\n"
                        + "Dinghy,0,1988-12-12,White\n"
                        + "HouseBoat,3,1981-04-21,Smith\n"
                        + "Contemporary,5,1980-05-17,White\n"
                        + "Paddleboat,3,1981-04-21,Smith\n"
                        + "Rowboat,5,1980-05-17,White\n"
                        + "Sailboat,4,1980-05-17,Black\n"
                        + "Cruise,200,1989-01-23,Brown\n",
                "Boat10");
    }

    private static Table createBEDANDBREAKFAST() {
        return Table.read().csv(
                "Design,Bedrooms,SoldDate,Owner\n"
                        + "Colonial,5,1980-05-17,Smith\n"
                        + "Gambrel,4,1982-11-18,Jones\n"
                        + "Contemporary,5,1980-03-24,White\n"
                        + "Split,2,1970-09-30,Brown\n",
                "BedAndBreakfast");
    }

    private static Table createSTUDENT() {
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

    private static Table createINSTRUCTOR() {
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

    private static Table createDEPTHEAD() {
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

    private static Table createCLASS() {
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

    private static Table createANIMALHOMES() {
        return Table.read().csv(
            "Animal,Name,Home,Age,MoveInDate\n"
                    + "Pig,Bob,Sty,5,5/5/2018\n"
                    + "Horse,James,Field,5,6/6/2018\n"
                    + "Goat,Samantha,Tree,10,7/7/2018\n"
                    + "Tigon,Rudhrani,Jungle,2,2/2/2018\n"
                    + "Chicken,Chuck,Pen,2,1/1/2018\n"
                    + "Squirrel,Sidney,Tree,10,3/3/2018\n"
                    + "Fox,Frankie,Forest,10,9/19/2018\n"
                    + "Rabbit,Taylor,Forest,10,4/4/2018\n",
            "Animal Homes");
    }

    private static Table createTREE() {
        return Table.read().csv(
                "Name,Home,Age\n"
                        + "Cherry,Frontyard,2\n"
                        + "Walnut,Field,3\n"
                        + "Birch,Forest,4\n"
                        + "Tallgreen,Jungle,5\n"
                        + "Apple,Orchard,6\n"
                        + "Orange,Orchard,9\n"
                        + "Hemlock,Forest,10\n",
                "Tree");
    }

    private static Table createFLOWER() {
        return Table.read().csv(
                "Name,Home,Age,Color\n"
                        + "Lily,Backyard,2,White\n"
                        + "VenusFlyTrap,Swamp,2,White\n"
                        + "Rose,Frontyard,4,Red\n"
                        + "Pansie,Meadow,5,Blue\n"
                        + "Daisy,Meadow,6,Yellow\n"
                        + "Dandelion,Field,7,Yellow\n"
                        + "Violet,Forest,10,Blue\n",
                "Flower");
    }

    private static Table createDOUBLEINDEXEDPEOPLENicknameDwellingYearsMoveInDate() {
        return Table.read().csv(
                "ID,Nickname,Dwelling,Years,MoveInDate\n"
                        + "1.0,Bob,Jungle,5,2/2/2018\n"
                        + "2.0,James,Field,5,6/6/2018\n"
                        + "3.0,David,Jungle,5,6/6/2018\n"
                        + "4.0,Marty,Forest,10,2/2/2018\n"
                        + "5.0,Tarzan,Jungle,10,7/7/2018\n"
                        + "6.0,Samantha,Tree,10,5/5/2018\n",
                "People - Nickname Dwelling Years MoveInDate");
    }

    private static Table createDOUBLEINDEXEDPEOPLENameDwellingYearsMoveInDate() {
        return Table.read().csv(
                "ID,Name,Dwelling,Years,MoveInDate\n"
                        + "1.0,Bob,Jungle,5,2/2/2018\n"
                        + "2.0,James,Field,5,6/6/2018\n"
                        + "3.0,David,Jungle,5,6/6/2018\n"
                        + "4.0,Marty,Jungle,10,2/2/2018\n"
                        + "5.0,Tarzan,Jungle,10,7/7/2018\n"
                        + "6.0,Samantha,Tree,10,5/5/2018\n",
                "People - Name Dwelling Years MoveInDate");
    }

    private static Table createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate() {
        return Table.read().csv(
            "ID,Name,HOME,Age,MoveInDate\n"
                    + "1.0,Bob,Jungle,5,2/2/2018\n"
                    + "2.0,James,Field,5,6/6/2018\n"
                    + "3.0,David,Jungle,5,6/6/2018\n"
                    + "4.0,Marty,Jungle,10,2/2/2018\n"
                    + "5.0,Tarzan,Jungle,10,7/7/2018\n"
                    + "6.0,Samantha,Forest,10,5/5/2018\n",
            "People - Name Home Age MoveInDate");
    }

    private static Table createFOOTBALLSCHEDULE() {
        return Table.read().csv(
                "TeamName,PlayDate,PlayTime,Location,HomeGame\n"
                        + "Lancers,2018-09-10,15:30,Springfield,true\n"
                        + "Tigers,2018-09-12,15:00,Detroit,false\n"
                        + "Patriots,2018-09-14,14:30,Boston,true\n"
                        + "Ravens,2018-09-10,12:30,Baltimore,true\n",
                "FootballSchedule");
    }

    private static Table createSOCCERSCHEDULE() {
        return Table.read().csv(
                "Mascot,PlayDate,PlayTime,Place\n"
                        + "Steelers,2018-09-10,15:30,Pittsburgh\n"
                        + "Dolphins,2018-09-12,15:00,Miami\n"
                        + "Patriots,2018-09-13,14:30,Boston\n"
                        + "Yankees,2018-09-10,12:00,NewYorkCity\n",
                "SoccerSchedule");
    }

    private static Table createFOOTBALLSCHEDULEDateTime() {
        return Table.read().csv(
                "TeamName,PlayDateTime,Location,HomeGame,SeasonRevenue,AllTimeRevenue\n"
                        + "Lancers,2018-09-10T15:30,Springfield,true,2000000000,8500000000000\n"
                        + "Tigers,2018-09-12T15:00,Detroit,false,1500000000,9000000000000\n"
                        + "Patriots,2018-09-14T14:30,Boston,true,1400000000,8200000000000\n"
                        + "Ravens,2018-09-10T12:30,Baltimore,true,2000000000,7000000000000\n",
                "FootballSchedule2");
    }

    private static Table createBASEBALLSCHEDULEDateTime() {
        return Table.read().csv(
                "TeamName,PlayDateTime,Location,HomeGame,SeasonRevenue,AllTimeRevenue\n"
                        + "RedSox,2018-09-10T15:30,Springfield,false,2000000000,7000000000000\n"
                        + "Marlins,2018-09-12T15:00,Detroit,true,1500000000,8500000000000\n"
                        + "Mariners,2018-09-14T14:30,Boston,true,1400000000,9000000000000\n"
                        + "Ravens,2018-09-10T12:30,Baltimore,false,2000000000,7000000000000\n",
                "FootballSchedule2");
    }

    private static Table createSOCCERSCHEDULEDateTime() {
        return Table.read().csv(
                "Mascot,PlayDateTime,Place,SeasonRevenue,AllTimeRevenue\n"
                        + "Steelers,2018-09-10T15:30,Pittsburgh,2000000000,7500000000000\n"
                        + "Dolphins,2018-09-12T15:00,Miami,1500000000,8200000000000\n"
                        + "Patriots,2018-09-13T14:30,Boston,1500000000,9000000000000\n"
                        + "Yankees,2018-09-10T12:00,NewYorkCity,1300000000,7000000000000\n",
                "SoccerSchedule2");
    }

    @Test
    public void innerJoinWithDoubleDogsCatsFish() {
        Table joined = DOUBLE_INDEXED_MICE.join("ID").inner(new Table[] {DOUBLE_INDEXED_CATS,DOUBLE_INDEXED_FISH});
        assertEquals(4, joined.columnCount());
        assertEquals(2, joined.rowCount());
    }

    @Test
    public void innerJoinWithDoubleDogsCatsFishVarargs() {
        Table joined = DOUBLE_INDEXED_MICE.join("ID").inner(DOUBLE_INDEXED_CATS,DOUBLE_INDEXED_FISH);
        assertEquals(4, joined.columnCount());
        assertEquals(2, joined.rowCount());
    }

    @Test
    public void innerJoinWithDoublesSimple() {
        Table joined = DOUBLE_INDEXED_PEOPLE.join("ID").inner(DOUBLE_INDEXED_DOGS);
        assertEquals(3, joined.columnCount());
        assertEquals(3, joined.rowCount());
        assertEquals(3, joined.column("ID").size());
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

        Table joined = table1.join("ID").rightOuter(table2, true, "ID");
        assertEquals(5, joined.columnCount());
        assertEquals(4, joined.rowCount());
    }

    @Test
    public void leftOuterJoinWithDuplicateColumnNames() {
        Table table1 = DUPLICATE_COL_NAME_DOGS.where(DUPLICATE_COL_NAME_DOGS.booleanColumn("Good").isTrue());
        Table table2 = DUPLICATE_COL_NAME_DOGS.where(DUPLICATE_COL_NAME_DOGS.booleanColumn("Good").isFalse());

        Table joined = table1.join("ID").leftOuter(table2, true, "ID");
        assertEquals(5, joined.columnCount());
        assertEquals(4, joined.rowCount());
        assertEquals(4, joined.column("ID").size());

    }

    @Test
    public void leftOuterJoinWithDoubles() {
        Table joined = DOUBLE_INDEXED_PEOPLE.join("ID").leftOuter(DOUBLE_INDEXED_DOGS, "ID");
        assertEquals(3, joined.columnCount());
        assertEquals(4, joined.rowCount());
        assertEquals(4, joined.column("ID").size());
    }

    @Test
    public void rightOuterJoinWithDoubles() {
        Table joined = DOUBLE_INDEXED_PEOPLE.join("ID").rightOuter(DOUBLE_INDEXED_DOGS, "ID");
        assertEquals(3, joined.columnCount());
        assertEquals(4, joined.rowCount());
        assertEquals(4, joined.column("ID").size());
    }

    @Test
    public void rightOuterJoinWithDoubles2() {
        Table joined = DOUBLE_INDEXED_PEOPLE.join("ID").rightOuter(DOUBLE_INDEXED_DOGS);
        assertEquals(3, joined.columnCount());
        assertEquals(4, joined.rowCount());
        assertEquals(4, joined.column("ID").size());
    }

    @Test
    public void rightOuterJoinWithDoubles3() {
        Table joined = DOUBLE_INDEXED_PEOPLE.join("ID").rightOuter(DOUBLE_INDEXED_DOGS, DOUBLE_INDEXED_CATS);
        assertEquals(3, joined.columnCount());
        assertEquals(4, joined.rowCount());
    }

    @Test
    public void leftOuterJoinWithDoubles2() {
        Table joined = DOUBLE_INDEXED_DOGS.join("ID").leftOuter(DOUBLE_INDEXED_PEOPLE, "ID");
        assertEquals(3, joined.columnCount());
        assertEquals(4, joined.rowCount());
        assertEquals(4, joined.column("ID").size());
    }

    @Test
    public void leftOuterJoinWithDoubles3() {
        Table joined = DOUBLE_INDEXED_DOGS.join("ID").leftOuter(DOUBLE_INDEXED_PEOPLE);
        assertEquals(3, joined.columnCount());
        assertEquals(4, joined.rowCount());
        assertEquals(4, joined.column("ID").size());
    }

    @Test
    public void leftOuterJoinWithDoubles4() {
        Table joined = DOUBLE_INDEXED_DOGS.join("ID").leftOuter(DOUBLE_INDEXED_PEOPLE, DOUBLE_INDEXED_CATS);
        assertEquals(3, joined.columnCount());
        assertEquals(4, joined.rowCount());
        assertEquals(4, joined.column("ID").size());
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
        assertEquals(6, joined.column("Date").size());
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
        assertEquals(6, joined.column("Animal").size());
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
        assertEquals(6, joined.column("Animal").size());
    }

    @Test
    public void fullOuterJoinJustTable() {
        Table joined = ANIMAL_FEED.join("Animal").fullOuter(ANIMAL_NAMES);
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
    public void fullOuterJoinNew() {
        Table joined = ANIMAL_FEED.join("Animal").fullOuter(true, ANIMAL_NAMES);
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
        Table table1 = createSTUDENT();
        Table table2 = createINSTRUCTOR();
        Table joined = table1.join("Age").inner(true, table2);
        assert(joined.columnNames().containsAll(Arrays.asList(new String[] {
                "T2.ID", "T2.City", "T2.State", "T2.USID", "T2.GradYear"})));
        assertEquals(16, joined.columnCount());
        assertEquals(14, joined.rowCount());
    }

    @Test
    public void innerJoinInstructorStudentOnAge() {
        Table table1 = createINSTRUCTOR();
        Table table2 = createSTUDENT();
        Table joined = table1.join("Age").inner(true, table2);
        assert(joined.columnNames().containsAll(Arrays.asList(new String[] {
                "T2.ID", "T2.City", "T2.State", "T2.USID", "T2.GradYear"})));
        assertEquals(16, joined.columnCount());
        assertEquals(14, joined.rowCount());
    }

    @Test
    public void innerJoinStudentInstructorClassOnAge() {
        Table table1 = createSTUDENT();
        Table table2 = createINSTRUCTOR();
        Table table3 = createCLASS();
        Table joined = table1.join("Age").inner(true, table2,table3);
        assert(joined.columnNames().containsAll(Arrays.asList(new String[] {
                "T2.ID", "T2.City", "T2.State", "T2.USID", "T2.GradYear",
        "T3.ID"})));
        assertEquals(24, joined.columnCount());
        assertEquals(14, joined.rowCount());
    }

    @Test
    public void innerJoinStudentInstructorClassDeptHeadOnAge() {
        Table table1 = createSTUDENT();
        Table table2 = createINSTRUCTOR();
        Table table3 = createCLASS();
        Table table4 = createDEPTHEAD();
        Table joined = table1.join("Age").inner(true, table2,table3,table4);
        assert(joined.columnNames().containsAll(Arrays.asList(new String[] {
                "T2.ID", "T2.City", "T2.State", "T2.USID", "T2.GradYear",
                "T3.ID", "T4.ID", "T4.First", "T4.Last", "T4.City", "T4.State"})));
        assertEquals(30, joined.columnCount());
        assertEquals(14, joined.rowCount());
    }

    @Test
    public void innerJoinStudentInstructorDeptHeadOnStateAge() {
        Table table1 = createSTUDENT();
        Table table2 = createINSTRUCTOR();
        Table table3 = createDEPTHEAD();
        Table joined = table1.join(new String[] {"State","Age"})
                .inner(true, table2,table3);
        assert(joined.columnNames().containsAll(Arrays.asList(new String[] {
                "T2.ID", "T2.City", "T2.USID", "T2.GradYear",

                "T3.ID", "T3.First","T3.Last","T3.City"})));
        assertEquals(20, joined.columnCount());
        assertEquals(1, joined.rowCount());
    }

    @Test
    public void innerJoinStudentInstructorOnStateAge() {
        Table table1 = createSTUDENT();
        Table table2 = createINSTRUCTOR();
        Table joined = table1.join(new String[] {"State","Age"}).inner(true, table2);
        assertEquals(15, joined.columnCount());
        assertEquals(3, joined.rowCount());
    }

    @Test
    public void innerJoinStudentInstructorOnStateAgeGradYear() {
        Table table1 = createSTUDENT();
        Table table2 = createINSTRUCTOR();
        Table joined = table1.join(new String[] {"State","Age","GradYear"})
                .inner(true, table2);
        assertEquals(14, joined.columnCount());
        assertEquals(2, joined.rowCount());
    }

    @Test
    public void leftJoinStudentInstructorOnStateAge() {
        Table table1 = createSTUDENT();
        Table table2 = createINSTRUCTOR();
        Table joined = table1.join(new String[] {"State","Age"})
                .leftOuter(true, table2);
        assertEquals(15, joined.columnCount());
        assertEquals(10, joined.rowCount());
        assertEquals(10, joined.column("State").size());
        assertEquals(10, joined.column("Age").size());
    }

    @Test
    public void innerJoinHouseBoatOnBedroomsOwner() {
        Table table1 = createHOUSE();
        Table table2 = createBOAT();
        Table joined = table1.join(new String[] {"Bedrooms","Owner"})
                .inner(table2,new String[] {"Bedrooms","Owner"});
        assertEquals(6, joined.columnCount());
        assertEquals(2, joined.rowCount());
        assertEquals(2, joined.column("Bedrooms").size());
    }

    @Test
    public void innerJoinHouseBoatOnStyleTypeBedroomsOwner() {
        Table table1 = createHOUSE();
        Table table2 = createBOAT();
        Table joined = table1.join(new String[] {"Style","Bedrooms","Owner"})
                .inner(table2,new String[] {"Type","Bedrooms","Owner"});
        assertEquals(5, joined.columnCount());
        assertEquals(1, joined.rowCount());
    }

    @Test
    public void fullJoinHouseBoatOnBedroomsOwner() {
        Table table1 = createHOUSE();
        Table table2 = createBOAT();
        Table joined = table1.join(new String[] {"Bedrooms","Owner"})
          .fullOuter(true,table2);
        assertEquals(6, joined.columnCount());
        assertEquals(7, joined.rowCount());
        assertEquals(7, joined.column("Style").size());
        assertEquals(3, joined.column("Style").countMissing());
        assertEquals(7, joined.column("Bedrooms").size());
        assertEquals(0, joined.column("Bedrooms").countMissing());
        assertEquals(7, joined.column("BuildDate").size());
        assertEquals(3, joined.column("BuildDate").countMissing());
        assertEquals(7, joined.column("Owner").size());
        assertEquals(0, joined.column("Owner").countMissing());
        assertEquals(7, joined.column("Type").size());
        assertEquals(2, joined.column("Type").countMissing());
        assertEquals(7, joined.column("SoldDate").size());
        assertEquals(2, joined.column("SoldDate").countMissing());

    }

    @Test
    public void fullJoinHouse10Boat10OnBedroomsOwner() {
        Table table1 = createHOUSE10();
        Table table2 = createBOAT10();
        Table joined = table1.join(new String[] {"Bedrooms","Owner"})
                .fullOuter(true,table2);
        assertEquals(6, joined.columnCount());
        assertEquals(11, joined.rowCount());
        assertEquals(11, joined.column("Bedrooms").size());
        assertEquals(0, joined.column("Bedrooms").countMissing());
        assertEquals(11, joined.column("Bedrooms").size());
        assertEquals(0, joined.column("Bedrooms").countMissing());
        assertEquals(11, joined.column("BuildDate").size());
        assertEquals(3, joined.column("BuildDate").countMissing());
        assertEquals(11, joined.column("Owner").size());
        assertEquals(0, joined.column("Owner").countMissing());
        assertEquals(11, joined.column("Type").size());
        assertEquals(3, joined.column("Type").countMissing());
        assertEquals(11, joined.column("SoldDate").size());
        assertEquals(3, joined.column("SoldDate").countMissing());

    }

    @Test
    public void fullJoinBnBBoat10OnBedroomsOwner() {
        Table table1 = createBEDANDBREAKFAST();
        Table table2 = createBOAT10();
        Table joined = table1.join(new String[] {"Bedrooms","SoldDate"})
                .fullOuter(true,table2);
        assertEquals(6, joined.columnCount());
        assertEquals(11, joined.rowCount());
        assertEquals(11, joined.column("Design").size());
        assertEquals(6, joined.column("Design").countMissing());
        assertEquals(11, joined.column("Bedrooms").size());
        assertEquals(0, joined.column("Bedrooms").countMissing());
        assertEquals(11, joined.column("SoldDate").size());
        assertEquals(0, joined.column("SoldDate").countMissing());
        assertEquals(11, joined.column("Owner").size());
        assertEquals(6, joined.column("Owner").countMissing());
        assertEquals(11, joined.column("Type").size());
        assertEquals(3, joined.column("Type").countMissing());
        assertEquals(11, joined.column("T2.Owner").size());
        assertEquals(3, joined.column("T2.Owner").countMissing());
    }

    @Test
    public void leftJoinHouseBoatOnBedroomsOwner() {
        Table table1 = createHOUSE();
        Table table2 = createBOAT();
        Table joined = table1.join(new String[] {"Bedrooms","Owner"})
                .leftOuter(table2,new String[] {"Bedrooms","Owner"});
        assertEquals(6, joined.columnCount());
        assertEquals(4, joined.rowCount());
    }

    @Test
    public void leftJoinHouseBoatOnStyleTypeBedroomsOwner() {
        Table table1 = createHOUSE();
        Table table2 = createBOAT();
        Table joined = table1.join(new String[] {"Style","Bedrooms","Owner"})
                .leftOuter(table2,new String[] {"Type","Bedrooms","Owner"});
        assertEquals(5, joined.columnCount());
        assertEquals(4, joined.rowCount());
    }

    @Test
    public void leftJoinHouseBoatBnBOnStyleTypeBedroomsOwner() {
        Table table1 = createHOUSE();
        Table table2 = createBOAT();
        Table joined = table1.join(new String[] {"Style","Bedrooms","Owner"})
                .leftOuter(table2,new String[] {"Type","Bedrooms","Owner"});
        assertEquals(5, joined.columnCount());
        assertEquals(4, joined.rowCount());
    }

    @Test
    public void rightJoinHouseBoatOnBedroomsOwner() {
        Table table1 = createHOUSE();
        Table table2 = createBOAT();
        Table joined = table1.join(new String[] {"Bedrooms","Owner"})
                .rightOuter(table2,new String[] {"Bedrooms","Owner"});
        assertEquals(6, joined.columnCount());
        assertEquals(5, joined.rowCount());
    }

    @Test
    public void rightJoinHouseBoatOnStyleTypeBedroomsOwner() {
        Table table1 = createHOUSE();
        Table table2 = createBOAT();
        Table joined = table1.join(new String[] {"Style","Bedrooms","Owner"})
                .rightOuter(table2,new String[] {"Type","Bedrooms","Owner"});
        assertEquals(5, joined.columnCount());
        assertEquals(5, joined.rowCount());
    }

    @Test
    public void rightJoinStudentInstructorOnStateAge() {
        Table table1 = createSTUDENT();
        Table table2 = createINSTRUCTOR();
        Table joined = table1.join(new String[] {"State","Age"})
                .rightOuter(true, table2);
        assertEquals(15, joined.columnCount());
        assertEquals(10, joined.rowCount());
    }

    @Test
    public void innerJoinStudentInstructorOnStateName() {
        Table table1 = createSTUDENT();
        Table table2 = createINSTRUCTOR();
        Table joined = table1.join(new String[] {"State","FirstName"})
                .inner(table2, true, new String[] {"State","First"});
        assertEquals(15, joined.columnCount());
        assertEquals(5, joined.rowCount());
    }

    @Test
    public void leftJoinStudentInstructorOnStateName() {
        Table table1 = createSTUDENT();
        Table table2 = createINSTRUCTOR();
        Table joined = table1.join(new String[] {"State","FirstName"})
                .leftOuter(table2, true, new String[] {"State","First"});
        assertEquals(15, joined.columnCount());
        assertEquals(10, joined.rowCount());
    }

    @Test
    public void rightJoinStudentInstructorOnStateName() {
        Table table1 = createSTUDENT();
        Table table2 = createINSTRUCTOR();
        Table joined = table1.join(new String[] {"State","FirstName"})
                .rightOuter(table2, true, new String[] {"State","First"});
        assertEquals(15, joined.columnCount());
        assertEquals(10, joined.rowCount());
    }

    @Test
    public void innerJoinOnAge() {
        Table table1 = createANIMALHOMES();
        Table table2 = createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
        Table joined = table1.join("Age")
                .inner(table2, "Age", true);
        assertEquals(9, joined.columnCount());
        assertEquals(18, joined.rowCount());
    }

    @Test
    public void innerJoinAnimalPeopleOnAge() {
        Table table1 = createANIMALHOMES();
        Table table2 = createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
        Table joined = table1.join("Age").inner(true, table2);
        assertEquals(9, joined.columnCount());
        assertEquals(18, joined.rowCount());
    }
    @Test
    public void innerJoinAnimalTreeOnAge() {
        Table table1 = createANIMALHOMES();
        Table table2 = createTREE();
        Table joined = table1.join("Age").inner(true, table2);
        assertEquals(7, joined.columnCount());
        assertEquals(8, joined.rowCount());
    }

    @Test
    public void innerJoinAnimalPeopleTreeOnAge() {
        Table table1 = createANIMALHOMES();
        Table table2 = createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
        Table table3 = createTREE();
        Table table4 = createFLOWER();
        Table joined = table1.join("Age")
                .inner(true, table2, table3, table4);
        assert(joined.columnNames().containsAll(Arrays.asList(new String[] {
                "T2.Name", "T2.HOME", "T2.MoveInDate",
                "T3.Name", "T3.Home", "T4.Name","T4.Home","Color"})));
        assertEquals(14, joined.columnCount());
        assertEquals(18, joined.rowCount());
    }

    @Test
    public void innerJoinAnimalPeopleTreeOnAgeHome() {
        Table table1 = createANIMALHOMES();
        Table table2 = createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
        Table table3 = createTREE();
        Table table4 = createFLOWER();
        Table joined = table1.join(new String[] {"Age","Home"})
                .inner(true, table2, table3, table4);
        assert(joined.columnNames().containsAll(Arrays.asList(new String[] {
                "Animal", "Name", "Home", "Age", "MoveInDate", "ID",
                "T2.Name","T2.MoveInDate","T3.Name","T4.Name","Color"})));
        assertEquals(11, joined.columnCount());
        assertEquals(2, joined.rowCount());
    }

    @Test
    public void innerJoinOnNameHomeAge() {
        Table table1 = createANIMALHOMES();
        Table table2 = createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
        Table joined = table1.join(new String[] {"Name", "Home", "Age"})
                .inner(true, table2);
        assertEquals(7, joined.columnCount());
        assertEquals(1, joined.rowCount());
    }

    @Test
    public void innerJoinOnAllMismatchedColNames() {
        Table table1 = createANIMALHOMES();
        Table table2 = createDOUBLEINDEXEDPEOPLENicknameDwellingYearsMoveInDate();
        Table joined = table1.join(new String[] {"Name", "Home", "Age"})
                .inner(table2,
                true, new String[] {"Nickname", "Dwelling", "Years"});
        assertEquals(7, joined.columnCount());
        assertEquals(2, joined.rowCount());
    }

    @Test
    public void innerJoinOnPartiallyMismatchedColNames() {
        Table table1 = createANIMALHOMES();
        Table table2 = createDOUBLEINDEXEDPEOPLENameDwellingYearsMoveInDate();
        Table joined = table1.join(new String[] {"Name", "Home", "Age"})
                .inner(table2, true,
                new String[] {"Name", "Dwelling", "Years"});
        assert(joined.columnNames().containsAll(Arrays.asList(new String[] {"Name", "Home", "Age"})));
        assertEquals(7, joined.columnCount());
        assertEquals(2, joined.rowCount());
    }

    @Test
    public void leftOuterJoinOnPartiallyMismatchedColNames() {
        Table table1 = createANIMALHOMES();
        Table table2 = createDOUBLEINDEXEDPEOPLENameDwellingYearsMoveInDate();
        Table joined = table1.join(new String[] {"Name", "Home", "Age"})
                .leftOuter(table2, true,
                        new String[] {"Name", "Dwelling", "Years"});
        assert(joined.columnNames().containsAll(Arrays.asList(new String[] {"Name", "Home", "Age"})));
        assertEquals(7, joined.columnCount());
        assertEquals(8, joined.rowCount());
    }

    @Test
    public void rightOuterJoinOnPartiallyMismatchedColNames() {
        Table table1 = createANIMALHOMES();
        Table table2 = createDOUBLEINDEXEDPEOPLENameDwellingYearsMoveInDate();
        Table joined = table1.join(new String[] {"Name", "Home", "Age"})
                .rightOuter(table2, true,
                        "Name", "Dwelling", "Years");
        assert(joined.columnNames().containsAll(Arrays.asList(new String[] {"Name", "Dwelling", "Years"})));
        assertEquals(7, joined.columnCount());
        assertEquals(6, joined.rowCount());
    }

    @Test
    public void innerJoinOnAgeMoveInDate() {
        Table table1 = createANIMALHOMES();
        Table table2 = createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
        Table joined = table1.join(new String[] {"Age","MoveInDate"})
                .inner(true, table2);
        assertEquals(8, joined.columnCount());
        assertEquals(3, joined.rowCount());
    }

    @Test
    public void leftOuterJoinOnAgeMoveInDate() {
        Table table1 = createANIMALHOMES();
        Table table2 = createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
        Table joined = table1.join(new String[] {"Age","MoveInDate"})
                .leftOuter(true, table2);
        assertEquals(8, joined.columnCount());
        assertEquals(9, joined.rowCount());
    }

    @Test
    public void rightOuterJoinOnAgeMoveInDate() {
        Table table1 = createANIMALHOMES();
        Table table2 = createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
        Table joined = table1.join("Age","MoveInDate")
                .rightOuter(true, table2);
        assertEquals(8, joined.columnCount());
        assertEquals(6, joined.rowCount());
    }

    @Test
    public void innerJoinFootballSoccerOnPlayDate() {
        Table table1 = createFOOTBALLSCHEDULE();
        Table table2 = createSOCCERSCHEDULE();
        Table joined = table1.join(new String[] {"PlayDate"})
                .inner(true, table2);
        assertEquals(8, joined.columnCount());
        assertEquals(5, joined.rowCount());
    }

    @Test
    public void innerJoinFootballSoccerOnPlayTime() {
        Table table1 = createFOOTBALLSCHEDULE();
        Table table2 = createSOCCERSCHEDULE();
        Table joined = table1.join(new String[] {"PlayTime"})
                .inner(true, table2);
        assertEquals(8, joined.columnCount());
        assertEquals(3, joined.rowCount());
    }

    @Test
    public void innerJoinFootballSoccerOnPlayDatePlayTime() {
        Table table1 = createFOOTBALLSCHEDULE();
        Table table2 = createSOCCERSCHEDULE();
        Table joined = table1.join(new String[] {"PlayDate","PlayTime"})
                .inner(true, table2);
        assertEquals(7, joined.columnCount());
        assertEquals(2, joined.rowCount());
    }

    @Test
    public void fullOuterJoinFootballSoccerOnPlayTime() {
        Table table1 = createFOOTBALLSCHEDULE();
        Table table2 = createSOCCERSCHEDULE();
        Table joined = table1.join("PlayTime")
                .fullOuter(true, table2);
        assertEquals(8, joined.columnCount());
        assertEquals(5, joined.rowCount());
    }

    @Test
    public void innerJoinFootballSoccerOnPlayDatePlayDateTime() {
        Table table1 = createFOOTBALLSCHEDULEDateTime();
        Table table2 = createSOCCERSCHEDULEDateTime();
        Table joined = table1.join("PlayDateTime")
                .inner(true, table2);
        assertEquals(10, joined.columnCount());
        assertEquals(2, joined.rowCount());
    }

    @Test
    public void innerJoinFootballSoccerOnSeasonRevenue() {
        Table table1 = createFOOTBALLSCHEDULEDateTime();
        Table table2 = createSOCCERSCHEDULEDateTime();
        Table joined = table1.join(new String[] {"SeasonRevenue","AllTimeRevenue"})
                .inner(true, table2);
        assertEquals(9, joined.columnCount());
        assertEquals(1, joined.rowCount());
    }

    @Test
    public void innerJoinFootballSoccerOnHomeGame() {
        Table table1 = createFOOTBALLSCHEDULEDateTime();
        Table table2 = createSOCCERSCHEDULEDateTime();
        try {
            Table joined = table1.join(new String[] {"HomeGame"})
                    .inner(true, table2);
            assertEquals(9, joined.columnCount());
            assertEquals(1, joined.rowCount());
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Joining is supported on integral, string, and date-like columns."));
        }
    }

    @Test
    public void fullOuterJoinFootballSoccerOnPlayDateTimeSeasonRevenue() {
        Table table1 = createFOOTBALLSCHEDULEDateTime();
        Table table2 = createSOCCERSCHEDULEDateTime();
        Table joined = table1.join(new String[] {"PlayDateTime","SeasonRevenue"})
                .fullOuter(true, table2);
        assertEquals(9, joined.columnCount());
        assertEquals(6, joined.rowCount());
        assertEquals(6, joined.column("TeamName").size());
        assertEquals(2, joined.column("TeamName").countMissing());
        assertEquals(6, joined.column("PlayDateTime").size());
        assertEquals(0, joined.column("PlayDateTime").countMissing());
        assertEquals(6, joined.column("Location").size());
        assertEquals(2, joined.column("Location").countMissing());
        assertEquals(6, joined.column("HomeGame").size());
        assertEquals(2, joined.column("HomeGame").countMissing());
        assertEquals(6, joined.column("SeasonRevenue").size());
        assertEquals(0, joined.column("SeasonRevenue").countMissing());
        assertEquals(6, joined.column("Mascot").size());
        assertEquals(2, joined.column("Mascot").countMissing());
        assertEquals(6, joined.column("Place").size());
        assertEquals(2, joined.column("Place").countMissing());
    }

    @Test
    public void fullOuterJoinFootballSoccerOnPlayDateTimeAllTimeRevenue() {
        Table table1 = createFOOTBALLSCHEDULEDateTime();
        Table table2 = createSOCCERSCHEDULEDateTime();
        Table joined = table1.join(new String[] {"AllTimeRevenue"})
                .fullOuter(true, table2);
        assertEquals(10, joined.columnCount());
        assertEquals(5, joined.rowCount());
        assertEquals(5, joined.column("TeamName").size());
        assertEquals(1, joined.column("TeamName").countMissing());
        assertEquals(5, joined.column("PlayDateTime").size());
        assertEquals(1, joined.column("PlayDateTime").countMissing());
        assertEquals(5, joined.column("Location").size());
        assertEquals(1, joined.column("Location").countMissing());
        assertEquals(5, joined.column("HomeGame").size());
        assertEquals(1, joined.column("HomeGame").countMissing());
        assertEquals(5, joined.column("SeasonRevenue").size());
        assertEquals(1, joined.column("SeasonRevenue").countMissing());
        assertEquals(5, joined.column("AllTimeRevenue").size());
        assertEquals(0, joined.column("AllTimeRevenue").countMissing());
        assertEquals(5, joined.column("Mascot").size());
        assertEquals(1, joined.column("Mascot").countMissing());
        assertEquals(5, joined.column("T2.PlayDateTime").size());
        assertEquals(1, joined.column("T2.PlayDateTime").countMissing());
        assertEquals(5, joined.column("Place").size());
        assertEquals(1, joined.column("Place").countMissing());
        assertEquals(5, joined.column("T2.SeasonRevenue").size());
        assertEquals(1, joined.column("T2.SeasonRevenue").countMissing());
    }

    @Test
    public void fullOuterJoinFootballBaseballException() {
        Table table1 = createFOOTBALLSCHEDULE();
        Table table2 = createBASEBALLSCHEDULEDateTime();
        try {
            table1.join(new String[] {"HomeGame"})
                .fullOuter(true, table2);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Joining is supported on integral, string, and date-like columns."));
        }
    }
}
