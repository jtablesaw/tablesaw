# Getting started with Tablesaw
Tablesaw is a fairly large library. In this section, we touch on a few basic ideas and common operations.

## Setup

Tablesaw is available on Maven Central. To use it, you need to add the maven dependency to your pom:

````
<dependency>
    <groupId>tech.tablesaw</groupId>
    <artifactId>tablesaw-core</artifactId>
    <version>LATEST</version>
</dependency>

````

It requires Java 9 or newer.

## Columns
A column is a named, one-dimensional collection of data. All data in a column must be of the same type. 

Currently, Tablesaw supports columns for strings, numbers (doubles), booleans, LocalDates, LocalTimes, and LocalDatTimes 
To create a column of numbers you can use:

```java
double[] numbers = {1, 2, 3, 4};
NumberColumn nCol = NumberColumn.create("Test", numbers);
out(nCol.print());

```
which produces: 
```java
Column: Test
1.0
2.0
3.0
4.0
```
Each column has an associated index, 0-based, so that to get the third value, you call
```java
nCol.get(2);
```
which returns 3.0.
As you might expect, operations that work on numbers in standard Java, work on columns in Tablesaw. To multiply every value in this column by 4, we use the __multiply()__ method, which returns a new column of the same size as the original.

```java
NumberColumn nc2 = nc.multiply(4);

```
producing: 
```java
Column: Test * 4.0
4.0
8.0
12.0
16.0
```
Generally, Tablesaw will provide a name for columns created this way. If you don't like the name, you can change it by
calling __setName("new name")__ on the column.

## Tables
A table is a collection of columns. All columns in the table must be the same size, although missing values are allowed.



## Tornado Warning

We’ll use a tornado data set from NOAA. Here’s what we’ll cover:

* Read and writing CSV files
* Viewing table metadata
* Adding and removing columns
* Printing the first few rows for a peak at the data
* Sorting
* Running descriptive stats (mean, min, max, etc.)
* Performing mapping operations over columns
* Filtering rows
* Computing cross-tabs
* Storing tables in Tablesaw’s compressed columnar storage format

All the data is in the projects data folder. The code is taken from the TornadoExample test class.

### Read a CSV file

Here we read a csv file of tornado data. Tablesaw infers the column types by sampling the data.

    Table tornadoes = Table.createFromCSV("data/tornadoes_1950-2014.csv");

### Viewing table metadata

Often, the best way to start is to print the column names for reference:

    tornadoes.columnNames();

which produces:

    Date, Time, State, State No, Scale, Injuries, Fatalities, Start Lat, Start Lon, Length, Width]

The shape() method displays the row and column counts:

    tornadoes.shape();
    >> 59945 rows X 10 cols

The structure() method shows the index, name and type of each column

    tornadoes.structure();
    
    >> Structure of data/tornadoes_1950-2014.csv
        Index Column Names Column Type 
        0     Date         LOCAL_DATE  
        1     Time         LOCAL_TIME  
        2     State        CATEGORY    
        3     State No     NUMBER     
        4     Scale        NUMBER     
        5     Injuries     NUMBER     
        6     Fatalities   NUMBER     
        7     Start Lat    NUMBER       
        8     Start Lon    NUMBER       
        9     Length       NUMBER       
        10    Width        NUMBER       

Like many Tablesaw methods, structure() returns a table; You can then call print() to produce a string representation for display, or perform any other table operations on it, like the following, which removes all columns whose type isn’t NUMBER:

    tornadoes.structure().selectWhere(column("Column Type").isEqualTo("NUMBER"));
    >> Structure of data/tornadoes_1950-2014.csv
        Index Column Name Column Type 
        3     State No    NUMBER     
        4     Scale       NUMBER     
        5     Injuries    NUMBER     
        6     Fatalities  NUMBER     

Of course, that also returned a table. We’ll cover selecting rows in more detail later.

### Previewing data

The first(n) method returns the first n rows.

    table.first(3);
    >>
    Date       Time     State Scale Injuries Fatalities Start Lat Start Lon Length Width 
    1950-01-03 11:00:00 MO    3     3        0          38.77     -90.22    9.5    150.0 
    1950-01-03 11:00:00 MO    3     3        0          38.77     -90.22    6.2    150.0 
    1950-01-03 11:10:00 IL    3     0        0          38.82     -90.12    3.3    100.0 

### Mapping operations

Mapping operations in Tablesaw take one or more columns as inputs and produce a new column as output. We can map arbitrary expressions onto the table, but many common operations are built in. You can, for example, calculate the difference in days, weeks, or years between the values in two date columns. The method below extracts the Month name from the date column into a new column.

    CategoryColumn month = tornadoes.dateColumn("Date").month();

Once you have a new column, you can add it to a table:

    tornadoes.addColumn(month);

You can remove columns from tables to save memory or reduce clutter:

    tornadoes.removeColumn("State No);

### Sorting

Now lets sort the table in reverse order by the id column. The negative sign before the name indicates a descending sort.

    tornadoes.sortOn("-Fatalities");
### Descriptive statistics

Descriptive statistics are calculated using the summary() method:

    table.column("Fatalities").summary().print();

This outputs:

    Measure  Value     
    n        1590.0    
    Missing  0.0       
    Mean     4.2779875 
    Min      1.0       
    Max      158.0     
    Range    157.0     
    Std. Dev 9.573451

### Filtering

You can write your own methods to filter rows, but it’s easier to use the built-in filter classes as shown below:

```java 
result = tornadoes.selectWhere(numberColumn("Fatalities").isGreaterThan(0));

result = tornadoes.selectWhere(dateColumn("Date").isInApril());

result = tornadoes.selectWhere(either
           (numberColumn("Width").isGreaterThan(300)),   // 300 yards
           (numberColumn("Length").isGreaterThan(10)));  // 10 miles

result = tornadoes.select("State", "Date").where(column("Date").isInQ2());
```

The last example above returns a table containing only the columns named in select() parameters.

### Totals and sub-totals

Column metrics can be calculated using methods like sum(), product(), mean(), max(), etc.

You can apply those methods to a table, calculating results on one column, grouped by the values in another.

```java
Table injuriesByScale = tornadoes.summarize("Injuries", median).by("Scale");
injuriesByScale.setName("Median injuries by Tornado Scale");
```
This produces the following table, in which Group represents the Tornado Scale and Median the median injures for that group:

    Median injuries by Tornado Scale
    Group Median 
    -9    0.0    
    0     0.0    
    1     0.0    
    2     0.0    
    3     1.0    
    4     12.0   
    5     107.0  

### Cross Tabs

Tablesaw lets you easily produce two-dimensional cross-tabulations (“cross tabs”) of counts and proportions with row and column subtotals. Here’s a count example where we look at the interaction of tornado severity and US state:
```java
CrossTab.xCount(t, t.stringColumn("State"), t.shortColumn("Scale"));
```

    Crosstab Counts: State x Scale
          -9 0     1     2    3    4   5  total 
    AL    0  623   769   424  141  37  11 2005  
    AR    0  485   666   419  161  28  0  1759  
    AZ    0  145   70    15   2    0   0  232   
    ... snipped 
    WI    0  447   517   267  55   18  2  1306  
    WV    0  37    67    22   7    0   0  133   
    WY    0  405   178   51   11   0   0  645   
    Total 44 27253 20009 9067 2580 670 68 59691`

Write the new CSV file to disk

    tornadoes.write().csv("data/rev_tornadoes_1950-2014.csv");

And that’s it for the introduction. Stay tuned for more info about advanced features.

