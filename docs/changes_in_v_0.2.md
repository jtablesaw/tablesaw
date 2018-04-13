## Changes to Tablesaw in release 0.20

### Testing & Documentation

1. Increased test coverage from 44% in core, to 76%.

### Removed:

1. Removed Float, Int, Short, Long column types. An enhanced DoubleColumn is used for all numbers. 
2. Removed TableGroup, SubTable, and NumericSummaryTable. All replaced by standard table or TableSlice and TableSliceGroup.
3. Removed Smile integration
4. Removed experimental time interval support
5. Removed several index types
6. Removed .saw file persistence
7. Removed all deprecated methods
8. Removed methods of limited or unclear utility 
9. Removed Lombok dependency (it caused warnings in Java9 and The IDEA plugin is flakey)
10. Removed duplicate comparator implementations 

### Renamed:

1. Renamed CategoryColumn to StringColumn
1. Renamed NumericColumn interface to NumberColumn
1. Renamed TemporaryView and ViewGroup to TableSlice and TableSliceGroup

### Enhancements:

1. Added CsvWriteOptions to allow greater control over file writing

1. Improved filtering support
    1. Implemented support for selecting specific columns, including calculated columns, in query result tables.

       ```java
       table.select(dateCol, dateCol.year()).where(nCol("quantity").isPositive());
       ```

    1. Standardized naming on filtering methods for tables and columns

    1. Added support for using Java 8 predicates to filter columns

    1. Extended table filtering to support direct use of column filter methods (e.g. *col.startsWith("foo")*) in table where clauses

    1. Added support for chaining "selections" so filters can be readily combined

       ```java
       col.where(col.isLessThan(3).and(col.isGreaterThan(-2)));
       ```

1. Improved join support
    1. Added support for left and right outer joins. For example:

       ```java
       table.join("myJoinColumn").leftOuter(table2, "otherJoinColumn");
       ```

    1. Added support for joining on doubles (after rounding to ints)

1. Reading and printing formatted data
    1. Added support for formatted printing of tables and columns (esp., number and time columns)

       ```java
       dateColumn.setPrintFormatter(DateTimeFormatter.ofPattern("MMM~dd~yyyy"));
       ```

       ```Java
       doubleColumn.setPrintFormatter(NumberColumnFormatter.ints); 
       ```

    1. Added support for applying a locale for CSV file import

1. Standardize column instantiation methods
    1. Always use a static *create()* method rather than public constructors
    1. Standardize support for instantiating from lists and arrays.

1. Added support for lag(n) and lead(n) methods on all column types. These return the receivers data offset by n positions:

    ```Java
    Column xLag = columnX.lag(1);
    Column xLead = columnX.lead(1);
    ```

1. Extended PackedLocalTime and PackedLocalDate to be (approximately) functionally equivalent to Java's LocalDate and LocalTime.

1. Improved Aggregation/Summarization
    1. Simplified the CrossTab API, and provided methods for creating CrossTabs (aka contingency tables) in table objects:

       ```java
       table.xTabCounts("columnA", "columnB");
       ```

    1. Support for table summaries that include summaries of columns created on the fly using mapping functions:

       ```java
       table.summarize(dateColumn.year(), max, min);
       ```

    1. Support for table summaries that include non-numeric columns. For example, the code below applies *countTrue* to the boolean column and *standardDeviation* to the numeric column.

       ```java
       table.summarize(booleanColumn, numberColumn, countTrue, standardDeviation); 
       ```

    1. Support summarizing *by* "time windows" groups of n time units (days, weeks, years, etc).

       ```java
       table.summarize("quantity", mean, median).by(date.timeWindow(DAYS, 5));
       ```

    1. Support summarizing *by* named time units (months, for example):

       ```java
       table.summarize("quantity", mean, median).by(date.month());
       ```

    1. Both of the above are examples of a more general solution: Sub totals can per calculated for groups defined *by* any function that returns a column: 

       ```java
       table.summarize("quantity", sumOfSquares).by(strCol.substring(4, 7));
       ```

       ​
