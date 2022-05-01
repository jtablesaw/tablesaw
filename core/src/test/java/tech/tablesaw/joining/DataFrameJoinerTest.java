package tech.tablesaw.joining;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.tablesaw.joining.JoinType.*;

import com.google.common.base.Joiner;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;

public class DataFrameJoinerTest {

  private static final Table ONE_YEAR =
      Table.read()
          .csv(
              Joiner.on(System.lineSeparator())
                  .join(
                      "Date,1 Yr Treasury Rate",
                      "\"Dec 1, 2017\",1.65%",
                      "\"Nov 1, 2017\",1.56%",
                      "\"Oct 1, 2017\",1.40%",
                      "\"Sep 1, 2017\",1.28%",
                      "\"Aug 1, 2017\",1.23%",
                      "\"Jul 1, 2017\",1.22%"),
              "1 Yr Treasury Rate");

  private static final Table SP500 =
      Table.read()
          .csv(
              Joiner.on(System.lineSeparator())
                  .join(
                      "Date,S&P 500",
                      "\"Nov 1, 2017\",2579.36",
                      "\"Oct 1, 2017\",2521.20",
                      "\"Sep 1, 2017\",2474.42",
                      "\"Aug 1, 2017\",2477.10",
                      "\"Jul 1, 2017\",2431.39",
                      "\"Jun 1, 2017\",2430.06"),
              "S&P 500");

  private static final Table ANIMAL_NAMES =
      Table.read()
          .csv(
              Joiner.on(System.lineSeparator())
                  .join(
                      "Animal,Name",
                      "Pig,Bob",
                      "Pig,James",
                      "Horse,David",
                      "Goat,Samantha",
                      "Tigon,Rudhrani",
                      "Rabbit,Taylor"),
              "Animal Names");

  private static final Table ANIMAL_FEED =
      Table.read()
          .csv(
              Joiner.on(System.lineSeparator())
                  .join(
                      "Animal,Feed",
                      "Pig,Mush",
                      "Horse,Hay",
                      "Goat,Anything",
                      "Guanaco,Grass",
                      "Monkey,Banana"),
              "Animal Feed");

  private static final Table STRING_INDEXED_PEOPLE =
      Table.read()
          .csv(
              Joiner.on(System.lineSeparator())
                  .join("ID,First Name", "aa,Bob", "ab,James", "ac,David", "ad,Samantha"),
              "People");

  private static final Table DOUBLE_INDEXED_PEOPLE =
      Table.read()
          .csv(
              Joiner.on(System.lineSeparator())
                  .join("ID,Name", "1.1,Bob", "2.1,James", "3.0,David", "4.0,Samantha"),
              "People");

  private static final Table DOUBLE_INDEXED_DOGS =
      Table.read()
          .csv(
              Joiner.on(System.lineSeparator())
                  .join("ID,Dog Name", "1.1,Spot", "3.0,Fido", "4.0,Sasha", "5.0,King"),
              "Dogs");

  private static final Table DOUBLE_INDEXED_DOGS_REVERSE =
      Table.read()
          .csv(
              Joiner.on(System.lineSeparator())
                  .join("Dog Name,ID", "Spot,1.1", "Fido,3.0", "Sasha,4.0", "King,5.0"),
              "DogsReverse");

  private static final Table DOUBLE_INDEXED_CATS =
      Table.read()
          .csv(
              Joiner.on(System.lineSeparator())
                  .join("ID,Cat Name", "1.1,Spot2", "2.1,Fido", "6.0,Sasha", "8.0,King2"),
              "Cats");

  private static final Table DOUBLE_INDEXED_FISH =
      Table.read()
          .csv(
              Joiner.on(System.lineSeparator())
                  .join("ID,Fish Name", "11.1,Spot3", "2.1,Fido", "4.0,Sasha", "6.0,King2"),
              "Fish");

  private static final Table DOUBLE_INDEXED_MICE =
      Table.read()
          .csv(
              Joiner.on(System.lineSeparator())
                  .join("ID,Mice_Name", "2.1,Jerry", "3.0,Fido", "6.0,Sasha", "9.0,Market"),
              "Mice");

  private static final Table DOUBLE_INDEXED_BIRDS =
      Table.read()
          .csv(
              Joiner.on(System.lineSeparator())
                  .join("ID,Bird_Name", "2.1,JerryB", "3.0,FidoB", "6.25,SashaB", "9.0,Market"),
              "Birds");

  private static final Table DUPLICATE_COL_NAME_DOGS =
      Table.read()
          .csv(
              Joiner.on(System.lineSeparator())
                  .join(
                      "ID,Dog Name, Good",
                      "1.1,Spot,true",
                      "3.0,Fido,true",
                      "4.0,Sasha,true",
                      "5.0,King,true",
                      "1.1,Spot,false",
                      "3.0,Fido,false",
                      "4.0,Sasha,false",
                      "5.0,King,false"),
              "Dogs");

  private static Table createHOUSE() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "Style,Bedrooms,BuildDate,Owner",
                    "Colonial,3,1976-06-02,Smith",
                    "Gambrel,4,1982-11-18,Jones",
                    "Contemporary,5,1980-03-24,White",
                    "Split,2,1970-09-30,Brown"),
            "House");
  }

  private static Table createBOAT() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "Type,Bedrooms,SoldDate,Owner",
                    "Yacht,2,1970-02-03,Jones",
                    "Dinghy,0,1988-12-12,Trump",
                    "HouseBoat,3,1981-04-21,Smith",
                    "Contemporary,5,1980-05-17,White",
                    "Cruise,200,1989-01-23,Brown"),
            "Boat");
  }

  private static Table createHOUSE10() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "Style,Bedrooms,BuildDate,Owner",
                    "Colonial,3,1976-06-02,Smith",
                    "Gambrel,4,1982-11-18,Jones",
                    "Contemporary,5,1980-03-24,White",
                    "Ranch,4,1982-11-18,Black",
                    "Victorian,5,1980-03-24,Red",
                    "Split,2,1970-09-30,Brown"),
            "House10");
  }

  private static Table createBOAT10() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "Type,Bedrooms,SoldDate,Owner",
                    "Yacht,2,1970-02-03,Jones",
                    "Dinghy,0,1988-12-12,White",
                    "HouseBoat,3,1981-04-21,Smith",
                    "Contemporary,5,1980-05-17,White",
                    "Paddleboat,3,1981-04-21,Smith",
                    "Rowboat,5,1980-05-17,White",
                    "Sailboat,4,1980-05-17,Black",
                    "Cruise,200,1989-01-23,Brown"),
            "Boat10");
  }

  private static Table createBEDANDBREAKFAST() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "Design,Bedrooms,SoldDate,Owner",
                    "Colonial,5,1980-05-17,Smith",
                    "Gambrel,4,1982-11-18,Jones",
                    "Contemporary,5,1980-03-24,White",
                    "Split,2,1970-09-30,Brown"),
            "BedAndBreakfast");
  }

  private static Table createSTUDENT() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "ID,FirstName,LastName,City,State,Age,USID,GradYear",
                    "1,Bob,Barney,Burke,VA,20,11122,2019",
                    "2,Chris,Cabello,Canyonville,OR,20,22224,2019",
                    "3,Dan,Dirble,Denver,CO,21,33335,2020",
                    "4,Edward,Earhardt,Easterly,WA,21,44339,2021",
                    "5,Frank,Farnsworth,Fredriksburg,VA,22,55338,2019",
                    "6,George,Gabral,Garrisburg,MD,22,66337,2020",
                    "7,Michael,Marbury,Milton,NH,23,77330,2020",
                    "8,Robert,Riley,Roseburg,OR,23,88836,2020",
                    "9,Bob,Earhardt,Milton,NH,24,93333,2019",
                    "10,Dan,Gabral,Easterly,WA,24,13333,2020"),
            "Student");
  }

  private static Table createSTUDENTLarger() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "ID,FirstName,LastName,City,State,Age,USID,GradYear",
                    "1,Bob,Barney,Burke,VA,20,11122,2019",
                    "2,Chris,Cabello,Canyonville,OR,20,22224,2019",
                    "3,Dan,Dirble,Denver,CO,21,33335,2020",
                    "4,Edward,Earhardt,Easterly,WA,21,44339,2021",
                    "5,Frank,Farnsworth,Fredriksburg,VA,22,55338,2019",
                    "6,George,Gabral,Garrisburg,MD,22,66337,2020",
                    "7,Michael,Marbury,Milton,NH,23,77330,2020",
                    "8,Robert,Riley,Roseburg,OR,23,88836,2020",
                    "9,Bob,Earhardt,Milton,NH,24,93333,2019",
                    "10,Dan,Gabral,Easterly,WA,24,13333,2020",
                    "11,Bob,Barney,Burke,VA,20,11122,2019",
                    "12,Chris,Cabello,Canyonville,OR,20,22224,2019",
                    "13,Dan,Dirble,Denver,CO,21,33335,2020",
                    "14,Edward,Earhardt,Easterly,WA,21,44339,2021",
                    "15,Frank,Farnsworth,Fredriksburg,VA,22,55338,2019",
                    "16,George,Gabral,Garrisburg,MD,22,66337,2020",
                    "17,Michael,Marbury,Milton,NH,23,77330,2020",
                    "18,Robert,Riley,Roseburg,OR,23,88836,2020",
                    "19,Bob,Earhardt,Milton,NH,24,93333,2019",
                    "20,Dan,Gabral,Easterly,WA,24,13333,2020"),
            "Student");
  }

  private static Table createINSTRUCTOR() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "ID,First,Last,Title,City,State,Age,USID,GradYear",
                    "1,Bob,Cabello,Prof,Burke,VA,20,11333,2019",
                    "2,Chris,Barney,TA,Canyonville,OR,20,22334,2019",
                    "3,Dan,Earhardt,Instructor,Denver,CO,19,33335,2020",
                    "4,Edward,Dirble,Prof,Easterly,WA,22,43339,2020",
                    "5,Farnsworth,Frank,Prof,Fredriksburg,VA,18,55338,2019",
                    "6,Gabral,George,TA,Garrisburg,MD,20,66337,2019",
                    "7,Robert,Marbury,TA,Msilton,NH,22,73330,2020",
                    "8,Michael,Riley,TA,Roseburg,OR,22,88336,2020",
                    "9,Bob,Riley,Prof,Milton,NH,19,99933,2020",
                    "10,Earhardt,Gabral,Prof,Easterly,WA,21,13333,2019"),
            "Instructor");
  }

  private static Table createGOODS1() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "ID,Name,Price",
                    "1,Chips,10",
                    "2,Coca-cola,20",
                    "3,Bread,30",
                    "4,Toothbrush,10",
                    "5,Soap,20",
                    "6,Towel,30"),
            "Goods1");
  }

  private static Table createGOODS2() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join("ID,Name,Price", "1,Chips,11", "2,Bread,29", "3,Soap,21", "4,Towel,33"),
            "Goods2");
  }

  private static Table createGOODS3() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join("ID,Name,Price", "1,Chips,9", "2,Coca-cola,22", "3,Bread,29"),
            "Goods3");
  }

  private static Table createDEPTHEAD() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "ID,First,Last,Dept,City,State,Age",
                    "1,John,Cabello,ComputerScience,Burke,VA,20",
                    "2,Samantha,Barney,Writing,Canyonville,OR,21",
                    "3,Mark,Earhardt,Mathematics,Denver,CO,22",
                    "4,Christie,Dirble,Architecture,Easterly,WA,23",
                    "5,Bhawesh,Frank,Psychology,Fredriksburg,VA,24",
                    "6,Robert,George,Sociology,Garrisburg,MD,25",
                    "7,George,Marbury,Physics,Msilton,NH,26",
                    "8,Zhongyu,Riley,Chemistry,Roseburg,OR,27",
                    "9,Laura,Riley,Economics,Milton,NH,28",
                    "10,Sally,Gabral,Marketing,Easterly,WA,29"),
            "DepartmentHead");
  }

  private static Table createCLASS() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "ID,ClassType,Name,Level,Description,StartDate,EndDate,Completed,Age",
                    "1001,Math,Calculus,101,Newton math,2018-09-20,2018-12-17,false,16",
                    "1002,Math,Calculus,102,Newton math,2019-01-06,2019-03-06,false,17",
                    "1003,Math,Calculus,103,Newton math,2019-03-10,2019-06-17,false,18",
                    "1004,Writing,Composition,101,Writing papers,2018-09-20,2018-12-17,false,19",
                    "1005,Writing,Composition,102,Writing papers,2019-01-06,2019-03-07,false,20",
                    "1006,Software,Programming,101,Programming basics,2018-09-22,2018-12-15,false,21",
                    "1007,Software,Programming,102,Programming basics,2019-01-05,2019-03-07,false,22",
                    "1008,Economics,Microeconomics,101,Basic micro economics,2018-09-20,2018-12-17,false,23",
                    "1009,Economics,Microeconomics,102,Basic micro economics,2018-01-05,2019-03-07,false,24",
                    "1010,Literature,Shakespeare,101,Understanding Shakespeare,2018-09-20,2018-12-17,false,25"),
            "Class");
  }

  private static Table createANIMALHOMES() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "Animal,Name,Home,Age,MoveInDate",
                    "Pig,Bob,Sty,5,5/5/2018",
                    "Horse,James,Field,5,6/6/2018",
                    "Goat,Samantha,Tree,10,7/7/2018",
                    "Tigon,Rudhrani,Jungle,2,2/2/2018",
                    "Chicken,Chuck,Pen,2,1/1/2018",
                    "Squirrel,Sidney,Tree,10,3/3/2018",
                    "Fox,Frankie,Forest,10,9/19/2018",
                    "Rabbit,Taylor,Forest,10,4/4/2018"),
            "Animal Homes");
  }

  private static Table createTREE() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "Name,Home,Age",
                    "Cherry,Frontyard,2",
                    "Walnut,Field,3",
                    "Birch,Forest,4",
                    "Tallgreen,Jungle,5",
                    "Apple,Orchard,6",
                    "Orange,Orchard,9",
                    "Hemlock,Forest,10"),
            "Tree");
  }

  private static Table createFLOWER() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "Name,Home,Age,Color",
                    "Lily,Backyard,2,White",
                    "VenusFlyTrap,Swamp,2,White",
                    "Rose,Frontyard,4,Red",
                    "Pansie,Meadow,5,Blue",
                    "Daisy,Meadow,6,Yellow",
                    "Dandelion,Field,7,Yellow",
                    "Violet,Forest,10,Blue"),
            "Flower");
  }

  private static Table createDOUBLEINDEXEDPEOPLENicknameDwellingYearsMoveInDate() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "ID,Nickname,Dwelling,Years,MoveInDate",
                    "1.1,Bob,Jungle,5,2/2/2018",
                    "2.1,James,Field,5,6/6/2018",
                    "3.0,David,Jungle,5,6/6/2018",
                    "4.0,Marty,Forest,10,2/2/2018",
                    "5.0,Tarzan,Jungle,10,7/7/2018",
                    "6.0,Samantha,Tree,10,5/5/2018"),
            "People - Nickname Dwelling Years MoveInDate");
  }

  private static Table createDOUBLEINDEXEDPEOPLENameDwellingYearsMoveInDate() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "ID,Name,Dwelling,Years,MoveInDate",
                    "1.1,Bob,Jungle,5,2/2/2018",
                    "2.1,James,Field,5,6/6/2018",
                    "3.0,David,Jungle,5,6/6/2018",
                    "4.0,Marty,Jungle,10,2/2/2018",
                    "5.0,Tarzan,Jungle,10,7/7/2018",
                    "6.0,Samantha,Tree,10,5/5/2018"),
            "People - Name Dwelling Years MoveInDate");
  }

  private static Table createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "ID,Name,HOME,Age,MoveInDate",
                    "1.1,Bob,Jungle,5,2/2/2018",
                    "2.1,James,Field,5,6/6/2018",
                    "3.0,David,Jungle,5,6/6/2018",
                    "4.0,Marty,Jungle,10,2/2/2018",
                    "5.0,Tarzan,Jungle,10,7/7/2018",
                    "6.0,Samantha,Forest,10,5/5/2018"),
            "People - Name Home Age MoveInDate");
  }

  private static Table createFOOTBALLSCHEDULE() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "TeamName,PlayDate,PlayTime,Location,HomeGame",
                    "Lancers,2018-09-10,15:30,Springfield,true",
                    "Tigers,2018-09-12,15:00,Detroit,false",
                    "Patriots,2018-09-14,14:30,Boston,true",
                    "Ravens,2018-09-10,12:30,Baltimore,true"),
            "FootballSchedule");
  }

  private static Table createSOCCERSCHEDULE() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "Mascot,PlayDate,PlayTime,Place",
                    "Steelers,2018-09-10,15:30,Pittsburgh",
                    "Dolphins,2018-09-12,15:00,Miami",
                    "Patriots,2018-09-13,14:30,Boston",
                    "Yankees,2018-09-10,12:00,NewYorkCity"),
            "SoccerSchedule");
  }

  private static Table createFOOTBALLSCHEDULEDateTime() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "TeamName,PlayDateTime,Location,HomeGame,SeasonRevenue,AllTimeRevenue",
                    "Lancers,2018-09-10T15:30,Springfield,true,2000000000,8500000000000",
                    "Tigers,2018-09-12T15:00,Detroit,false,1500000000,9000000000000",
                    "Patriots,2018-09-14T14:30,Boston,true,1400000000,8200000000000",
                    "Ravens,2018-09-10T12:30,Baltimore,true,2000000000,7000000000000"),
            "FootballSchedule2");
  }

  private static Table createBASEBALLSCHEDULEDateTime() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "TeamName,PlayDateTime,Location,HomeGame,SeasonRevenue,AllTimeRevenue",
                    "RedSox,2018-09-10T15:30,Springfield,false,2000000000,7000000000000",
                    "Marlins,2018-09-12T15:00,Detroit,true,1500000000,8500000000000",
                    "Mariners,2018-09-14T14:30,Boston,true,1400000000,9000000000000",
                    "Ravens,2018-09-10T12:30,Baltimore,false,2000000000,7000000000000"),
            "FootballSchedule2");
  }

  private static Table createSOCCERSCHEDULEDateTime() {
    return Table.read()
        .csv(
            Joiner.on(System.lineSeparator())
                .join(
                    "Mascot,PlayDateTime,Place,SeasonRevenue,AllTimeRevenue",
                    "Steelers,2018-09-10T15:30,Pittsburgh,2000000000,7500000000000",
                    "Dolphins,2018-09-12T15:00,Miami,1500000000,8200000000000",
                    "Patriots,2018-09-13T14:30,Boston,1500000000,9000000000000",
                    "Yankees,2018-09-10T12:00,NewYorkCity,1300000000,7000000000000"),
            "SoccerSchedule2");
  }

  @Test
  public void innerJoinWithDoubleBirdsCatsFishDouble() {
    Table joined =
        DOUBLE_INDEXED_BIRDS
            .joinOn("ID")
            .type(INNER)
            .with(DOUBLE_INDEXED_CATS, DOUBLE_INDEXED_FISH)
            .join();
    assertEquals(4, joined.columnCount());
    assertEquals(1, joined.rowCount());
  }

  @Test
  public void innerJoinWithDoubleDogsCatsBirdsDouble() {
    Table joined =
        DOUBLE_INDEXED_FISH
            .joinOn("ID")
            .type(INNER)
            .with(DOUBLE_INDEXED_CATS, DOUBLE_INDEXED_BIRDS)
            .join();
    assertEquals(4, joined.columnCount());
    assertEquals(1, joined.rowCount());
  }

  @Test
  public void innerJoinWithDoubleDogsCatsFishVarargs() {
    Table joined =
        DOUBLE_INDEXED_MICE.joinOn("ID").with(DOUBLE_INDEXED_CATS, DOUBLE_INDEXED_FISH).join();
    assertEquals(4, joined.columnCount());
    assertEquals(2, joined.rowCount());
  }

  @Test
  public void innerJoinWithDoublesSimple() {
    Table joined = DOUBLE_INDEXED_PEOPLE.joinOn("ID").with(DOUBLE_INDEXED_DOGS).join();
    assertEquals(3, joined.columnCount());
    assertEquals(3, joined.rowCount());
    assertEquals(3, joined.column("ID").size());
  }

  @Test
  public void innerJoinWithDoubles() {
    Table joined =
        DOUBLE_INDEXED_PEOPLE.joinOn("ID").with(DOUBLE_INDEXED_DOGS).rightJoinColumns("ID").join();
    assertEquals(3, joined.columnCount());
    assertEquals(3, joined.rowCount());
  }

  @Test
  public void innerJoinWithDuplicateColumnNames() {
    Table table1 =
        DUPLICATE_COL_NAME_DOGS.where(DUPLICATE_COL_NAME_DOGS.booleanColumn("Good").isTrue());
    Table table2 =
        DUPLICATE_COL_NAME_DOGS.where(DUPLICATE_COL_NAME_DOGS.booleanColumn("Good").isFalse());

    Table joined =
        table1
            .joinOn("ID")
            .with(table2)
            .rightJoinColumns("ID")
            .allowDuplicateColumnNames(true)
            .join();
    assertEquals(5, joined.columnCount());
    assertEquals(4, joined.rowCount());
  }

  @Test
  public void rightOuterJoinWithDuplicateColumnNames() {
    Table table1 =
        DUPLICATE_COL_NAME_DOGS.where(DUPLICATE_COL_NAME_DOGS.booleanColumn("Good").isTrue());
    Table table2 =
        DUPLICATE_COL_NAME_DOGS.where(DUPLICATE_COL_NAME_DOGS.booleanColumn("Good").isFalse());

    Table joined =
        table1
            .joinOn("ID")
            .with(table2)
            .allowDuplicateColumnNames(true)
            .rightJoinColumns("ID")
            .type(RIGHT_OUTER)
            .join();
    assertEquals(5, joined.columnCount());
    assertEquals(4, joined.rowCount());
  }

  @Test
  public void leftOuterWithDuplicateColumnNames() {
    Table table1 =
        DUPLICATE_COL_NAME_DOGS.where(DUPLICATE_COL_NAME_DOGS.booleanColumn("Good").isTrue());
    Table table2 =
        DUPLICATE_COL_NAME_DOGS.where(DUPLICATE_COL_NAME_DOGS.booleanColumn("Good").isFalse());

    Table joined =
        table1
            .joinOn("ID")
            .with(table2)
            .allowDuplicateColumnNames(true)
            .type(LEFT_OUTER)
            .rightJoinColumns("ID")
            .join();
    assertEquals(5, joined.columnCount());
    assertEquals(4, joined.rowCount());
    assertEquals(4, joined.column("ID").size());
  }

  @Test
  public void leftOuterWithDoubles() {
    Table joined =
        DOUBLE_INDEXED_PEOPLE
            .joinOn("ID")
            .with(DOUBLE_INDEXED_DOGS)
            .rightJoinColumns("ID")
            .type(LEFT_OUTER)
            .join();
    assertEquals(3, joined.columnCount());
    assertEquals(4, joined.rowCount());
    assertEquals(4, joined.column("ID").size());
  }

  @Test
  public void rightOuterJoinWithDoubles() {
    Table joined =
        DOUBLE_INDEXED_PEOPLE
            .joinOn("ID")
            .with(DOUBLE_INDEXED_DOGS)
            .type(RIGHT_OUTER)
            .rightJoinColumns("ID")
            .join();
    assertEquals(3, joined.columnCount());
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(4, joined.rowCount());
    assertEquals(4, joined.column("ID").size());
  }

  @Test
  public void rightOuterJoinWithDoubles2() {
    Table joined =
        DOUBLE_INDEXED_PEOPLE.joinOn("ID").with(DOUBLE_INDEXED_DOGS).type(RIGHT_OUTER).join();
    assertEquals(3, joined.columnCount());
    assertEquals(4, joined.rowCount());
    assertEquals(4, joined.column("ID").size());
  }

  @Test
  public void rightOuterJoinWithDoubles2Reverse() {
    Table joined =
        DOUBLE_INDEXED_PEOPLE
            .joinOn("ID")
            .with(DOUBLE_INDEXED_DOGS_REVERSE)
            .type(RIGHT_OUTER)
            .join();
    assertEquals(3, joined.columnCount());
    assertEquals(4, joined.rowCount());
    assertEquals(4, joined.column("ID").size());
  }

  @Test
  public void rightOuterJoinWithDoubles3() {
    Table joined =
        DOUBLE_INDEXED_PEOPLE
            .joinOn("ID")
            .with(DOUBLE_INDEXED_DOGS, DOUBLE_INDEXED_CATS)
            .type(RIGHT_OUTER)
            .join();
    assertTrue(
        joined.columnNames().containsAll(Arrays.asList("ID", "Name", "Dog Name", "Cat Name")));
    assertEquals(4, joined.rowCount());
    assertEquals(4, joined.column("ID").size());
    assertEquals(0, joined.column("ID").countMissing());
    assertEquals(4, joined.column("Name").size());
    assertEquals(3, joined.column("Name").countMissing());
    assertEquals(4, joined.column("Dog Name").size());
    assertEquals(3, joined.column("Dog Name").countMissing());
    assertEquals(4, joined.column("Cat Name").size());
    assertEquals(0, joined.column("Cat Name").countMissing());
  }

  @Test
  public void rightOuterJoinWithDoubles4() {
    Table joined =
        DOUBLE_INDEXED_PEOPLE.joinOn("ID").with(DOUBLE_INDEXED_DOGS).type(RIGHT_OUTER).join();
    assertTrue(joined.columnNames().containsAll(Arrays.asList("ID", "Name", "Dog Name")));
    assertEquals(4, joined.rowCount());
    assertEquals(4, joined.column("ID").size());
    assertEquals(0, joined.column("ID").countMissing());
    assertEquals(4, joined.column("Dog Name").size());
    assertEquals(0, joined.column("Dog Name").countMissing());
    assertEquals(4, joined.column("Name").size());
    assertEquals(1, joined.column("Name").countMissing());
  }

  @Test
  public void leftOuterWithDoubles2() {
    Table joined =
        DOUBLE_INDEXED_DOGS
            .joinOn("ID")
            .with(DOUBLE_INDEXED_PEOPLE)
            .rightJoinColumns("ID")
            .type(LEFT_OUTER)
            .join();
    assertEquals(3, joined.columnCount());
    assertEquals(4, joined.rowCount());
    assertEquals(4, joined.column("ID").size());
  }

  @Test
  public void leftOuterWithDoubles3() {
    Table joined =
        DOUBLE_INDEXED_DOGS.joinOn("ID").with(DOUBLE_INDEXED_PEOPLE).type(LEFT_OUTER).join();
    assertEquals(3, joined.columnCount());
    assertEquals(4, joined.rowCount());
    assertEquals(4, joined.column("ID").size());
  }

  @Test
  public void leftOuterWithDoubles4() {
    Table joined =
        DOUBLE_INDEXED_DOGS
            .joinOn("ID")
            .with(DOUBLE_INDEXED_PEOPLE, DOUBLE_INDEXED_CATS)
            .type(LEFT_OUTER)
            .join();
    assertTrue(
        joined.columnNames().containsAll(Arrays.asList("ID", "Dog Name", "Name", "Cat Name")));
    assertEquals(4, joined.column("ID").size());
    assertEquals(4, joined.column("Dog Name").size());
    assertEquals(0, joined.column("Dog Name").countMissing());
    assertEquals(4, joined.column("Name").size());
    assertEquals(1, joined.column("Name").countMissing());
    assertEquals(4, joined.column("Cat Name").size());
    assertEquals(3, joined.column("Cat Name").countMissing());
    assertEquals(4, joined.rowCount());
  }

  @Test
  public void innerJoin() {
    Table joined = SP500.joinOn("Date").with(ONE_YEAR).rightJoinColumns("Date").join();
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(3, joined.columnCount());
    assertEquals(5, joined.rowCount());
  }

  @Test
  public void innerJoinSingleColumn() {
    Table joined =
        SP500.selectColumns("Date").joinOn("Date").with(ONE_YEAR.selectColumns("Date")).join();
    assertEquals(5, joined.rowCount());
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(1, joined.columnCount());
  }

  @Test
  public void innerJoinSingleColumnOnRight() {
    Table joined = SP500.joinOn("Date").with(ONE_YEAR.selectColumns("Date")).join();
    assertEquals(5, joined.rowCount());
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(2, joined.columnCount());
  }

  @Test
  public void innerJoinMultipleColumnsAllColumnsInJoinKey() {
    Table table1 =
        Table.create(SP500.dateColumn("Date"), SP500.dateColumn("Date").copy().setName("Date2"));
    Table table2 =
        Table.create(
            ONE_YEAR.dateColumn("Date"), ONE_YEAR.dateColumn("Date").copy().setName("Date2"));

    Table joined = table1.joinOn("Date", "Date2").with(table2).join();
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(5, joined.rowCount());
    assertEquals(2, joined.columnCount());
  }

  @Test
  public void innerJoinWithBoolean() {
    Table joined =
        DUPLICATE_COL_NAME_DOGS
            .joinOn("Good")
            .allowDuplicateColumnNames(true)
            .with(DUPLICATE_COL_NAME_DOGS)
            .join();
    assertEquals(5, joined.columnCount());
    assertEquals(32, joined.rowCount());
  }

  @Test
  public void leftOuter() {
    Table joined =
        SP500.joinOn("Date").with(ONE_YEAR).rightJoinColumns("Date").type(LEFT_OUTER).join();
    assertEquals(3, joined.columnCount());
    assertEquals(6, joined.rowCount());
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(6, joined.column("Date").size());
  }

  @Test
  public void innerJoinDuplicateKeysFirstTable() {
    Table joined =
        ANIMAL_NAMES.joinOn("Animal").with(ANIMAL_FEED).rightJoinColumns("Animal").join();
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(3, joined.columnCount());
    assertEquals(4, joined.rowCount());
  }

  @Test
  public void leftOuterDuplicateKeysFirstTable() {
    Table joined =
        ANIMAL_NAMES
            .joinOn("Animal")
            .with(ANIMAL_FEED)
            .type(LEFT_OUTER)
            .rightJoinColumns("Animal")
            .join();
    assertEquals(3, joined.columnCount());
    assertEquals(6, joined.rowCount());
    assertEquals(6, joined.column("Animal").size());
  }

  @Test
  public void innerJoinDuplicateKeysSecondTable() {
    Table joined =
        ANIMAL_FEED.joinOn("Animal").with(ANIMAL_NAMES).rightJoinColumns("Animal").join();
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(3, joined.columnCount());
    assertEquals(4, joined.rowCount());
  }

  @Test
  public void innerJoinDuplicateKeysSecondTableWithTextColumn() {
    Table feed = ANIMAL_FEED.copy();
    Table names = ANIMAL_NAMES.copy();
    feed.replaceColumn("Animal", feed.stringColumn("Animal").asStringColumn());
    StringColumn nameCol = names.stringColumn("Animal");
    nameCol = nameCol.where(Selection.withRange(0, feed.rowCount()));
    feed.replaceColumn("Animal", nameCol);
    Table joined =
        ANIMAL_FEED.joinOn("Animal").with(ANIMAL_NAMES).rightJoinColumns("Animal").join();
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(3, joined.columnCount());
    assertEquals(4, joined.rowCount());
  }

  @Test
  public void leftOuterDuplicateKeysSecondTable() {
    Table joined =
        ANIMAL_FEED
            .joinOn("Animal")
            .with(ANIMAL_NAMES)
            .rightJoinColumns("Animal")
            .type(LEFT_OUTER)
            .join();
    assertEquals(3, joined.columnCount());
    assertEquals(6, joined.rowCount());
    assertEquals(6, joined.column("Animal").size());
  }

  @Test
  public void fullOuterJoinJustTable() {
    Table joined = ANIMAL_FEED.joinOn("Animal").with(ANIMAL_NAMES).type(FULL_OUTER).join();
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
    Table joined =
        ANIMAL_FEED
            .joinOn("Animal")
            .with(ANIMAL_NAMES)
            .type(FULL_OUTER)
            .rightJoinColumns("Animal")
            .join();
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
  public void fullOuterJoinColTwoOnlyJoinKeys() {
    Table joined =
        ANIMAL_FEED
            .joinOn("Animal")
            .with(ANIMAL_NAMES.selectColumns("Animal"))
            .type(FULL_OUTER)
            .join();
    assertEquals(2, joined.columnCount());
    assertEquals(8, joined.rowCount());
  }

  @Test
  public void fullOuterJoinNew() {
    Table joined =
        ANIMAL_FEED
            .joinOn("Animal")
            .allowDuplicateColumnNames(true)
            .type(FULL_OUTER)
            .with(ANIMAL_NAMES)
            .join();
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
  public void fullOuterJoinMultiTable1() {
    Table table1 = createGOODS1();
    Table table2 = createGOODS2();
    Table table3 = createGOODS3();
    Table joined =
        table1
            .joinOn("Name")
            .allowDuplicateColumnNames(true)
            .with(table2, table3)
            .type(FULL_OUTER)
            .join();
    assertEquals(7, joined.columnCount());
    assertTrue(
        joined
            .columnNames()
            .containsAll(
                Arrays.asList("ID", "Name", "Price", "T2.ID", "T2.Price", "T3.ID", "T3.Price")));
    assertEquals(6, joined.rowCount());
    assertEquals(0, joined.column("ID").countMissing());
    assertEquals(0, joined.column("Name").countMissing());
    assertEquals(0, joined.column("Price").countMissing());
    assertEquals(6, joined.column("T2.ID").size());
    assertEquals(2, joined.column("T2.ID").countMissing());
    assertEquals(6, joined.column("T2.Price").size());
    assertEquals(2, joined.column("T2.Price").countMissing());
    assertEquals(6, joined.column("T3.ID").size());
    assertEquals(3, joined.column("T3.ID").countMissing());
    assertEquals(6, joined.column("T3.Price").size());
    assertEquals(3, joined.column("T3.Price").countMissing());
  }

  @Test
  public void fullOuterJoinMultiTable2() {
    Table table3 = createGOODS3();
    Table table2 = createGOODS2();
    Table table1 = createGOODS1();
    Table joined =
        table3
            .joinOn("Name")
            .allowDuplicateColumnNames(true)
            .type(FULL_OUTER)
            .with(table2, table1)
            .join();
    assertEquals(7, joined.columnCount());
    assertTrue(
        joined
            .columnNames()
            .containsAll(
                Arrays.asList("ID", "Name", "Price", "T2.ID", "T2.Price", "T3.ID", "T3.Price")));
    assertEquals(6, joined.rowCount());
    assertEquals(3, joined.column("ID").countMissing());
    assertEquals(0, joined.column("Name").countMissing());
    assertEquals(3, joined.column("Price").countMissing());
    assertEquals(6, joined.column("T2.ID").size());
    assertEquals(2, joined.column("T2.ID").countMissing());
    assertEquals(6, joined.column("T2.Price").size());
    assertEquals(2, joined.column("T2.Price").countMissing());
    assertEquals(6, joined.column("T3.ID").size());
    assertEquals(0, joined.column("T3.ID").countMissing());
    assertEquals(6, joined.column("T3.Price").size());
    assertEquals(0, joined.column("T3.Price").countMissing());
  }

  @Test
  public void fullOuterJoinMultiTable3() {
    Table table2 = createGOODS2();
    Table table3 = createGOODS3();
    Table table1 = createGOODS1();
    Table joined =
        table2
            .joinOn("Name")
            .allowDuplicateColumnNames(true)
            .type(FULL_OUTER)
            .with(table3, table1)
            .join();
    assertEquals(7, joined.columnCount());
    assertTrue(
        joined
            .columnNames()
            .containsAll(
                Arrays.asList("ID", "Name", "Price", "T2.ID", "T2.Price", "T3.ID", "T3.Price")));
    assertEquals(6, joined.rowCount());
    assertEquals(2, joined.column("ID").countMissing());
    assertEquals(0, joined.column("Name").countMissing());
    assertEquals(2, joined.column("Price").countMissing());
    assertEquals(6, joined.column("T2.ID").size());
    assertEquals(3, joined.column("T2.ID").countMissing());
    assertEquals(6, joined.column("T2.Price").size());
    assertEquals(3, joined.column("T2.Price").countMissing());
    assertEquals(6, joined.column("T3.ID").size());
    assertEquals(0, joined.column("T3.ID").countMissing());
    assertEquals(6, joined.column("T3.Price").size());
    assertEquals(0, joined.column("T3.Price").countMissing());
  }

  @Test
  public void innerJoinStudentInstructorOnAge() {
    Table table1 = createSTUDENT();
    Table table2 = createINSTRUCTOR();
    Table joined = table1.joinOn("Age").allowDuplicateColumnNames(true).with(table2).join();
    assert (joined
        .columnNames()
        .containsAll(Arrays.asList("T2.ID", "T2.City", "T2.State", "T2.USID", "T2.GradYear")));
    assertEquals(16, joined.columnCount());
    assertEquals(14, joined.rowCount());
  }

  @Test
  public void joiningShouldNotMutateColNamesOnOriginalTable() {
    Table table1 = createSTUDENT();
    Table table2 = createINSTRUCTOR();
    List<String> originalColumns =
        table2.columns().stream().map(Column::name).collect(Collectors.toList());

    table1.joinOn("Age").with(table2).allowDuplicateColumnNames(true).join();

    List<String> newColumns =
        table2.columns().stream().map(Column::name).collect(Collectors.toList());
    assertEquals(originalColumns, newColumns);
  }

  @Test
  public void innerJoinInstructorStudentOnAge() {
    Table table1 = createINSTRUCTOR();
    Table table2 = createSTUDENT();
    Table joined = table1.joinOn("Age").with(table2).allowDuplicateColumnNames(true).join();
    assert (joined
        .columnNames()
        .containsAll(Arrays.asList("T2.ID", "T2.City", "T2.State", "T2.USID", "T2.GradYear")));
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(16, joined.columnCount());
    assertEquals(14, joined.rowCount());
  }

  @Test
  public void innerJoinStudentInstructorClassOnAge() {
    Table table1 = createSTUDENT();
    Table table2 = createINSTRUCTOR();
    Table table3 = createCLASS();
    Table joined = table1.joinOn("Age").with(table2, table3).allowDuplicateColumnNames(true).join();
    assert (joined
        .columnNames()
        .containsAll(
            Arrays.asList("T2.ID", "T2.City", "T2.State", "T2.USID", "T2.GradYear", "T3.ID")));
    assertEquals(24, joined.columnCount());
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(14, joined.rowCount());
  }

  @Test
  public void innerJoinStudentInstructorClassDeptHeadOnAge() {
    Table table1 = createSTUDENT();
    Table table2 = createINSTRUCTOR();
    Table table3 = createCLASS();
    Table table4 = createDEPTHEAD();
    Table joined =
        table1.joinOn("Age").with(table2, table3, table4).allowDuplicateColumnNames(true).join();
    assert (joined
        .columnNames()
        .containsAll(
            Arrays.asList(
                "T2.ID",
                "T2.City",
                "T2.State",
                "T2.USID",
                "T2.GradYear",
                "T3.ID",
                "T4.ID",
                "T4.First",
                "T4.Last",
                "T4.City",
                "T4.State")));
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(30, joined.columnCount());
    assertEquals(14, joined.rowCount());
  }
  // Tests for left table is larger than right table, single cols
  @Test
  public void innerJoinStudentInstructorClassDeptHeadOnAgeLargerLeftTable() {
    Table table1 = createSTUDENTLarger();
    Table table2 = createINSTRUCTOR();
    Table table3 = createCLASS();
    Table table4 = createDEPTHEAD();
    Table joined =
        table1.joinOn("Age").with(table2, table3, table4).allowDuplicateColumnNames(true).join();
    List<String> expectedCol =
        Arrays.asList(
            "ID",
            "FirstName",
            "LastName",
            "City",
            "State",
            "Age",
            "USID",
            "GradYear",
            "T2.ID",
            "First",
            "Last",
            "Title",
            "T2.City",
            "T2.State",
            "T2.USID",
            "T2.GradYear",
            "T3.ID",
            "ClassType",
            "Name",
            "Level",
            "Description",
            "StartDate",
            "EndDate",
            "Completed",
            "T4.ID",
            "T4.First",
            "T4.Last",
            "Dept",
            "T4.City",
            "T4.State");
    assert (String.join("", joined.columnNames()).equals(String.join("", expectedCol)));
    assertEquals(30, joined.columnCount());
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(28, joined.rowCount());
  }

  @Test
  public void innerJoinStudentInstructorDeptHeadOnStateAge() {
    Table table1 = createSTUDENT();
    Table table2 = createINSTRUCTOR();
    Table table3 = createDEPTHEAD();
    Table joined =
        table1.joinOn("State", "Age").with(table2, table3).allowDuplicateColumnNames(true).join();
    assert (joined
        .columnNames()
        .containsAll(
            Arrays.asList(
                "T2.ID",
                "T2.City",
                "T2.USID",
                "T2.GradYear",
                "T3.ID",
                "T3.First",
                "T3.Last",
                "T3.City")));
    assertEquals(20, joined.columnCount());
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(1, joined.rowCount());
  }
  // Tests for left table is larger than right table, multiple cols
  @Test
  public void innerJoinStudentInstructorDeptHeadOnStateAgeWithLargerLeftTable() {
    Table table1 = createSTUDENTLarger();
    Table table2 = createINSTRUCTOR();
    Table table3 = createDEPTHEAD();
    // table1 join table 2 will have 6 rows, so drop last 5 rows of table3 for test
    table3 = table3.dropRows(5, 6, 7, 8, 9);
    Table joined =
        table1.joinOn("State", "Age").with(table2, table3).allowDuplicateColumnNames(true).join();
    List<String> expectedCol =
        Arrays.asList(
            "ID",
            "FirstName",
            "LastName",
            "City",
            "State",
            "Age",
            "USID",
            "GradYear",
            "T2.ID",
            "First",
            "Last",
            "Title",
            "T2.City",
            "T2.USID",
            "T2.GradYear",
            "T3.ID",
            "T3.First",
            "T3.Last",
            "Dept",
            "T3.City");
    // test for col order
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assert (String.join("", joined.columnNames()).equals(String.join("", expectedCol)));
    assertEquals(20, joined.columnCount());
    assertEquals(2, joined.rowCount());
  }

  @Test
  public void innerJoinStudentInstructorOnStateAge() {
    Table table1 = createSTUDENT();
    Table table2 = createINSTRUCTOR();
    Table joined =
        table1.joinOn("State", "Age").with(table2).allowDuplicateColumnNames(true).join();
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(15, joined.columnCount());
    assertEquals(3, joined.rowCount());
  }

  @Test
  public void innerJoinStudentInstructorOnStateAgeGradYear() {
    Table table1 = createSTUDENT();
    Table table2 = createINSTRUCTOR();
    Table joined =
        table1
            .joinOn("State", "Age", "GradYear")
            .with(table2)
            .allowDuplicateColumnNames(true)
            .join();
    assertEquals(14, joined.columnCount());
    assertEquals(2, joined.rowCount());
  }

  @Test
  public void leftJoinStudentInstructorOnStateAge() {
    Table table1 = createSTUDENT();
    Table table2 = createINSTRUCTOR();
    Table joined =
        table1
            .joinOn("State", "Age")
            .allowDuplicateColumnNames(true)
            .with(table2)
            .type(LEFT_OUTER)
            .join();
    assertEquals(15, joined.columnCount());
    assertEquals(10, joined.rowCount());
    assertEquals(10, joined.column("State").size());
    assertEquals(10, joined.column("Age").size());
  }

  @Test
  public void leftJoinStoreWithMultiTables() {
    Table table1 = createGOODS1();
    Table table2 = createGOODS2();
    Table table3 = createGOODS3();
    Table joined =
        table1
            .joinOn("Name")
            .allowDuplicateColumnNames(true)
            .with(table2, table3)
            .type(LEFT_OUTER)
            .join();
    assertEquals(7, joined.columnCount());
    assertTrue(
        joined
            .columnNames()
            .containsAll(
                Arrays.asList("ID", "Name", "Price", "T2.ID", "T2.Price", "T3.ID", "T3.Price")));
    assertEquals(6, joined.rowCount());
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(6, joined.column("Price").size());
    assertEquals(6, joined.column("T2.ID").size());
    assertEquals(2, joined.column("T2.ID").countMissing());
    assertEquals(6, joined.column("T3.ID").size());
    assertEquals(3, joined.column("T3.ID").countMissing());
  }

  @Test
  public void innerJoinHouseBoatOnBedroomsOwner() {
    Table table1 = createHOUSE();
    Table table2 = createBOAT();
    Table joined =
        table1
            .joinOn("Bedrooms", "Owner")
            .with(table2)
            .rightJoinColumns("Bedrooms", "Owner")
            .join();
    assertEquals(6, joined.columnCount());
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(2, joined.rowCount());
    assertEquals(2, joined.column("Bedrooms").size());
  }

  @Test
  public void innerJoinHouseBoatOnStyleTypeBedroomsOwner() {
    Table table1 = createHOUSE();
    Table table2 = createBOAT();
    Table joined =
        table1
            .joinOn("Style", "Bedrooms", "Owner")
            .with(table2)
            .rightJoinColumns("Type", "Bedrooms", "Owner")
            .join();

    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(5, joined.columnCount());
    assertEquals(1, joined.rowCount());
  }

  @Test
  public void fullJoinHouseBoatOnBedroomsOwner() {
    Table table1 = createHOUSE();
    Table table2 = createBOAT();
    Table joined =
        table1
            .joinOn("Bedrooms", "Owner")
            .allowDuplicateColumnNames(true)
            .with(table2)
            .type(FULL_OUTER)
            .join();
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
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
    Table joined =
        table1
            .joinOn("Bedrooms", "Owner")
            .allowDuplicateColumnNames(true)
            .with(table2)
            .type(FULL_OUTER)
            .join();
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
    Table joined =
        table1
            .joinOn("Bedrooms", "SoldDate")
            .allowDuplicateColumnNames(true)
            .with(table2)
            .type(FULL_OUTER)
            .join();
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
    Table joined =
        table1
            .joinOn("Bedrooms", "Owner")
            .with(table2)
            .type(LEFT_OUTER)
            .rightJoinColumns("Bedrooms", "Owner")
            .join();
    assertEquals(6, joined.columnCount());
    assertEquals(4, joined.rowCount());
  }

  @Test
  public void leftJoinHouseBoatBnBOnStyleTypeBedroomsOwner() {
    Table table1 = createHOUSE();
    Table table2 = createBOAT();
    Table joined =
        table1
            .joinOn("Style", "Bedrooms", "Owner")
            .with(table2)
            .type(LEFT_OUTER)
            .rightJoinColumns("Type", "Bedrooms", "Owner")
            .join();
    assertEquals(5, joined.columnCount());
    assertEquals(4, joined.rowCount());
  }

  @Test
  public void rightJoinHouseBoatOnBedroomsOwner() {
    Table table1 = createHOUSE();
    Table table2 = createBOAT();
    Table joined =
        table1
            .joinOn("Bedrooms", "Owner")
            .with(table2)
            .type(RIGHT_OUTER)
            .rightJoinColumns("Bedrooms", "Owner")
            .join();
    assertEquals(6, joined.columnCount());
    assertEquals(5, joined.rowCount());
  }

  @Test
  public void rightJoinHouseBoatOnStyleTypeBedroomsOwner() {
    Table table1 = createHOUSE();
    Table table2 = createBOAT();
    Table joined =
        table1
            .joinOn("Style", "Bedrooms", "Owner")
            .with(table2)
            .type(RIGHT_OUTER)
            .rightJoinColumns("Type", "Bedrooms", "Owner")
            .join();
    assertEquals(5, joined.columnCount());
    assertEquals(5, joined.rowCount());
  }

  @Test
  public void rightJoinStudentInstructorOnStateAge() {
    Table table1 = createSTUDENT();
    Table table2 = createINSTRUCTOR();
    Table joined =
        table1
            .joinOn("State", "Age")
            .with(table2)
            .type(RIGHT_OUTER)
            .allowDuplicateColumnNames(true)
            .join();
    assertEquals(15, joined.columnCount());
    assertEquals(10, joined.rowCount());
  }

  @Test
  public void innerJoinStudentInstructorOnStateName() {
    Table table1 = createSTUDENT();
    Table table2 = createINSTRUCTOR();
    Table joined =
        table1
            .joinOn("State", "FirstName")
            .with(table2)
            .allowDuplicateColumnNames(true)
            .rightJoinColumns("State", "First")
            .join();
    assertEquals(15, joined.columnCount());
    assertEquals(5, joined.rowCount());
  }

  @Test
  public void leftJoinStudentInstructorOnStateName() {
    Table table1 = createSTUDENT();
    Table table2 = createINSTRUCTOR();
    Table joined =
        table1
            .joinOn("State", "FirstName")
            .with(table2)
            .allowDuplicateColumnNames(true)
            .type(LEFT_OUTER)
            .rightJoinColumns("State", "First")
            .join();
    assertEquals(15, joined.columnCount());
    assertEquals(10, joined.rowCount());
  }

  @Test
  public void rightJoinStudentInstructorOnStateName() {
    Table table1 = createSTUDENT();
    Table table2 = createINSTRUCTOR();
    Table joined =
        table1
            .joinOn("State", "FirstName")
            .with(table2)
            .allowDuplicateColumnNames(true)
            .rightJoinColumns("State", "First")
            .type(RIGHT_OUTER)
            .join();
    assertEquals(15, joined.columnCount());
    assertEquals(10, joined.rowCount());
  }

  @Test
  public void innerJoinOnAge() {
    Table table1 = createANIMALHOMES();
    Table table2 = createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
    Table joined =
        table1
            .joinOn("Age")
            .with(table2)
            .rightJoinColumns("Age")
            .allowDuplicateColumnNames(true)
            .join();
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(9, joined.columnCount());
    assertEquals(18, joined.rowCount());
  }

  @Test
  public void innerJoinAnimalPeopleOnAge() {
    Table table1 = createANIMALHOMES();
    Table table2 = createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
    Table joined = table1.joinOn("Age").with(table2).allowDuplicateColumnNames(true).join();
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(9, joined.columnCount());
    assertEquals(18, joined.rowCount());
  }

  @Test
  public void innerJoinAnimalTreeOnAge() {
    Table table1 = createANIMALHOMES();
    Table table2 = createTREE();
    Table joined = table1.joinOn("Age").with(table2).allowDuplicateColumnNames(true).join();
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(7, joined.columnCount());
    assertEquals(8, joined.rowCount());
  }

  @Test
  public void innerJoinAnimalPeopleTreeOnAge() {
    Table table1 = createANIMALHOMES();
    Table table2 = createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
    Table table3 = createTREE();
    Table table4 = createFLOWER();
    Table joined =
        table1.joinOn("Age").with(table2, table3, table4).allowDuplicateColumnNames(true).join();
    assert (joined
        .columnNames()
        .containsAll(
            Arrays.asList(
                "T2.Name",
                "T2.HOME",
                "T2.MoveInDate",
                "T3.Name",
                "T3.Home",
                "T4.Name",
                "T4.Home",
                "Color")));
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(14, joined.columnCount());
    assertEquals(18, joined.rowCount());
  }

  @Test
  public void innerJoinAnimalPeopleTreeOnAgeHome() {
    Table table1 = createANIMALHOMES();
    Table table2 = createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
    Table table3 = createTREE();
    Table table4 = createFLOWER();
    Table joined =
        table1
            .joinOn("Age", "Home")
            .with(table2, table3, table4)
            .allowDuplicateColumnNames(true)
            .join();
    assert (joined
        .columnNames()
        .containsAll(
            Arrays.asList(
                "Animal",
                "Name",
                "Home",
                "Age",
                "MoveInDate",
                "ID",
                "T2.Name",
                "T2.MoveInDate",
                "T3.Name",
                "T4.Name",
                "Color")));
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(11, joined.columnCount());
    assertEquals(2, joined.rowCount());
  }

  @Test
  public void innerJoinOnNameHomeAge() {
    Table table1 = createANIMALHOMES();
    Table table2 = createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
    Table joined =
        table1.joinOn("Name", "Home", "Age").with(table2).allowDuplicateColumnNames(true).join();
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(7, joined.columnCount());
    assertEquals(1, joined.rowCount());
  }

  @Test
  public void innerJoinOnAllMismatchedColNames() {
    Table table1 = createANIMALHOMES();
    Table table2 = createDOUBLEINDEXEDPEOPLENicknameDwellingYearsMoveInDate();
    Table joined =
        table1
            .joinOn("Name", "Home", "Age")
            .with(table2)
            .allowDuplicateColumnNames(true)
            .rightJoinColumns("Nickname", "Dwelling", "Years")
            .join();
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(7, joined.columnCount());
    assertEquals(2, joined.rowCount());
  }

  @Test
  public void innerJoinOnPartiallyMismatchedColNames() {
    Table table1 = createANIMALHOMES();
    Table table2 = createDOUBLEINDEXEDPEOPLENameDwellingYearsMoveInDate();
    Table joined =
        table1
            .joinOn("Name", "Home", "Age")
            .with(table2)
            .allowDuplicateColumnNames(true)
            .rightJoinColumns("Name", "Dwelling", "Years")
            .join();
    assert (joined.columnNames().containsAll(Arrays.asList("Name", "Home", "Age")));
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(7, joined.columnCount());
    assertEquals(2, joined.rowCount());
  }

  @Test
  public void leftOuterOnPartiallyMismatchedColNames() {
    Table table1 = createANIMALHOMES();
    Table table2 = createDOUBLEINDEXEDPEOPLENameDwellingYearsMoveInDate();
    Table joined =
        table1
            .joinOn("Name", "Home", "Age")
            .with(table2)
            .allowDuplicateColumnNames(true)
            .type(LEFT_OUTER)
            .rightJoinColumns("Name", "Dwelling", "Years")
            .join();
    assert (joined.columnNames().containsAll(Arrays.asList("Name", "Home", "Age")));
    assertEquals(7, joined.columnCount());
    assertEquals(8, joined.rowCount());
  }

  @Test
  public void rightOuterJoinOnPartiallyMismatchedColNames() {
    Table table1 = createANIMALHOMES();
    Table table2 = createDOUBLEINDEXEDPEOPLENameDwellingYearsMoveInDate();
    Table joined =
        table1
            .joinOn("Name", "Home", "Age")
            .with(table2)
            .type(RIGHT_OUTER)
            .allowDuplicateColumnNames(true)
            .rightJoinColumns("Name", "Dwelling", "Years")
            .join();
    assert (joined.columnNames().containsAll(Arrays.asList("Name", "Dwelling", "Years")));
    assertEquals(7, joined.columnCount());
    assertEquals(6, joined.rowCount());
  }

  @Test
  public void innerJoinOnAgeMoveInDate() {
    Table table1 = createANIMALHOMES();
    Table table2 = createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
    Table joined =
        table1.joinOn("Age", "MoveInDate").with(table2).allowDuplicateColumnNames(true).join();
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(8, joined.columnCount());
    assertEquals(3, joined.rowCount());
  }

  @Test
  public void leftOuterOnAgeMoveInDate() {
    Table table1 = createANIMALHOMES();
    Table table2 = createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
    Table joined =
        table1
            .joinOn("Age", "MoveInDate")
            .type(LEFT_OUTER)
            .allowDuplicateColumnNames(true)
            .with(table2)
            .join();
    assertEquals(8, joined.columnCount());
    assertEquals(9, joined.rowCount());
  }

  @Test
  public void leftOuterKeepAllJoinKeyColumns() {
    Table table1 = createANIMALHOMES();
    Table table2 = createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
    Table joined =
        table1
            .joinOn("Age", "MoveInDate")
            .with(table2)
            .type(LEFT_OUTER)
            .allowDuplicateColumnNames(true)
            .keepAllJoinKeyColumns(true)
            .rightJoinColumns("Age", "MoveInDate")
            .join();
    assertEquals(10, joined.columnCount());
    assertEquals(9, joined.rowCount());
  }

  @Test
  public void rightOuterJoin_keepAllJoinKeyColumns() {
    Table table1 = createANIMALHOMES();
    Table table2 = createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
    Table joined =
        table1
            .joinOn("Age", "MoveInDate")
            .with(table2)
            .type(RIGHT_OUTER)
            .allowDuplicateColumnNames(true)
            .keepAllJoinKeyColumns(true)
            .rightJoinColumns("Age", "MoveInDate")
            .join();
    assertEquals(10, joined.columnCount());
    assertEquals(6, joined.rowCount());
  }

  @Test
  public void fullOuter_keepAllJoinKeyColumns() {
    Table table1 = createANIMALHOMES();
    Table table2 = createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
    Table joined =
        table1
            .joinOn("Age", "MoveInDate")
            .with(table2)
            .allowDuplicateColumnNames(true)
            .keepAllJoinKeyColumns(true)
            .type(FULL_OUTER)
            .rightJoinColumns("Age", "MoveInDate")
            .join();
    assertEquals(10, joined.columnCount());
    assertEquals(12, joined.rowCount());
  }

  @Test
  public void innerJoin_keepAllJoinKeyColumns() {
    Table table1 = createANIMALHOMES();
    Table table2 = createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
    Table joined =
        table1
            .joinOn("Name", "Home", "Age")
            .with(table2)
            .allowDuplicateColumnNames(true)
            .keepAllJoinKeyColumns(true)
            .rightJoinColumns("Name", "Home", "Age")
            .join();
    assertEquals(10, joined.columnCount());
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(1, joined.rowCount());
  }

  @Test
  public void rightOuterJoinOnAgeMoveInDate() {
    Table table1 = createANIMALHOMES();
    Table table2 = createDOUBLEINDEXEDPEOPLENameHomeAgeMoveInDate();
    Table joined =
        table1
            .joinOn("Age", "MoveInDate")
            .with(table2)
            .type(RIGHT_OUTER)
            .allowDuplicateColumnNames(true)
            .join();
    assertEquals(8, joined.columnCount());
    assertEquals(6, joined.rowCount());
  }

  @Test
  public void innerJoinFootballSoccerOnPlayDate() {
    Table table1 = createFOOTBALLSCHEDULE();
    Table table2 = createSOCCERSCHEDULE();
    Table joined = table1.joinOn("PlayDate").with(table2).allowDuplicateColumnNames(true).join();
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(8, joined.columnCount());
    assertEquals(5, joined.rowCount());
  }

  @Test
  public void innerJoinFootballSoccerOnPlayTime() {
    Table table1 = createFOOTBALLSCHEDULE();
    Table table2 = createSOCCERSCHEDULE();
    Table joined = table1.joinOn("PlayTime").with(table2).allowDuplicateColumnNames(true).join();
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(8, joined.columnCount());
    assertEquals(3, joined.rowCount());
  }

  @Test
  public void innerJoinFootballSoccerOnPlayDatePlayTime() {
    Table table1 = createFOOTBALLSCHEDULE();
    Table table2 = createSOCCERSCHEDULE();
    Table joined =
        table1.joinOn("PlayDate", "PlayTime").with(table2).allowDuplicateColumnNames(true).join();
    assertEquals(7, joined.columnCount());
    assertEquals(2, joined.rowCount());
  }

  @Test
  public void fullOuterJoinFootballSoccerOnPlayTime() {
    Table table1 = createFOOTBALLSCHEDULE();
    Table table2 = createSOCCERSCHEDULE();
    Table joined =
        table1
            .joinOn("PlayTime")
            .allowDuplicateColumnNames(true)
            .type(FULL_OUTER)
            .with(table2)
            .join();
    assertEquals(8, joined.columnCount());
    assertEquals(5, joined.rowCount());
  }

  @Test
  public void innerJoinFootballSoccerOnPlayDatePlayDateTime() {
    Table table1 = createFOOTBALLSCHEDULEDateTime();
    Table table2 = createSOCCERSCHEDULEDateTime();
    Table joined =
        table1.joinOn("PlayDateTime").with(table2).allowDuplicateColumnNames(true).join();
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(10, joined.columnCount());
    assertEquals(2, joined.rowCount());
  }

  @Test
  public void innerJoinFootballSoccerOnSeasonRevenue() {
    Table table1 = createFOOTBALLSCHEDULEDateTime();
    Table table2 = createSOCCERSCHEDULEDateTime();
    Table joined =
        table1
            .joinOn("SeasonRevenue", "AllTimeRevenue")
            .with(table2)
            .allowDuplicateColumnNames(true)
            .join();
    assertEquals(9, joined.columnCount());
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    assertEquals(1, joined.rowCount());
  }

  @Test
  public void fullOuterJoinFootballSoccerOnPlayDateTimeSeasonRevenue() {
    Table table1 = createFOOTBALLSCHEDULEDateTime();
    Table table2 = createSOCCERSCHEDULEDateTime();
    Table joined =
        table1
            .joinOn("PlayDateTime", "SeasonRevenue")
            .allowDuplicateColumnNames(true)
            .with(table2)
            .type(FULL_OUTER)
            .join();
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
  public void fullOuterJoinFootballSoccerOnAllTimeRevenue() {
    Table table1 = createFOOTBALLSCHEDULEDateTime();
    Table table2 = createSOCCERSCHEDULEDateTime();
    Table joined =
        table1
            .joinOn("AllTimeRevenue")
            .allowDuplicateColumnNames(true)
            .with(table2)
            .type(FULL_OUTER)
            .join();
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
  public void rightOuterJoinFootballSoccerOnAllTimeRevenue() {
    Table table1 = createFOOTBALLSCHEDULEDateTime();
    Table table2 = createSOCCERSCHEDULEDateTime();
    Table joined =
        table1
            .joinOn("AllTimeRevenue")
            .with(table2)
            .type(RIGHT_OUTER)
            .allowDuplicateColumnNames(true)
            .join();
    assertEquals(10, joined.columnCount());
    assertEquals(4, joined.rowCount());
    assertEquals(4, joined.column("SeasonRevenue").size());
    assertEquals(1, joined.column("SeasonRevenue").countMissing());
  }

  @Test
  public void innerJoinFootballSoccerOnAllTimeRevenue() {
    Table table1 = createFOOTBALLSCHEDULEDateTime();
    Table table2 = createSOCCERSCHEDULEDateTime();
    Table joined =
        table1.joinOn("AllTimeRevenue").with(table2).allowDuplicateColumnNames(true).join();
    assertEquals(10, joined.columnCount());
    assertEquals(3, joined.rowCount());
    assertEquals(3, joined.column("SeasonRevenue").size());
  }

  @Test
  public void fullOuterJoinFootballBaseballBoolean() {
    Table table1 = createFOOTBALLSCHEDULE();
    Table table2 = createBASEBALLSCHEDULEDateTime();
    Table joined =
        table1
            .joinOn("HomeGame")
            .allowDuplicateColumnNames(true)
            .with(table2)
            .type(FULL_OUTER)
            .join();
    assertEquals(10, joined.columnCount());
    assertEquals(8, joined.rowCount());
  }

  @Test
  public void differentColumnTypes() {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> {
          STRING_INDEXED_PEOPLE.joinOn("ID").with(DOUBLE_INDEXED_PEOPLE).join();
        });
  }

  @Test
  public void innerJoinEmptyLeftTable() {
    Table leftTable = Table.create(StringColumn.create("Animal"));
    Table joined = leftTable.joinOn("Animal").with(ANIMAL_NAMES).join();
    assertEquals(0, joined.rowCount());
    assertTrue(joined.columnNames().stream().noneMatch(e -> e.startsWith("Placeholder_")));
    for (Column<?> column : joined.columnArray()) {
      assertEquals(0, column.size());
    }
  }

  @Test
  public void leftOuterEmptyLeftTable() {
    Table leftTable = Table.create(StringColumn.create("Animal"));
    Table joined = leftTable.joinOn("Animal").with(ANIMAL_NAMES).join();
    assertEquals(0, joined.rowCount());
    for (Column<?> column : joined.columnArray()) {
      assertEquals(0, column.size());
    }
  }
}
