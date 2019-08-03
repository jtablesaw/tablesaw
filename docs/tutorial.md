

# Introducing Tablesaw: A brief tutorial

Tablesaw is a fairly large library. In this section, we touch on the most common operations. If you haven't already done
so, we strongly recommend that you read the Getting Started guide, before continuing here. 

## Exploring Tornadoes 

To give a better sense of how Tablesaw works, we’ll use a tornado data set from NOAA. Here’s what we’ll cover:

* Reading and writing CSV files
* Viewing table metadata
* Adding and removing columns
* Printing the first few rows for a peak at the data
* Sorting
* Running descriptive stats (mean, min, max, etc.)
* Performing mapping operations over columns
* Filtering rows
* Computing cross-tabs

All the data is in the Tablesaw *data* folder. The code is taken from the TornadoExample test class.

### Read a CSV file

Here we read a csv file of tornado data. Tablesaw infers the column types by sampling the data.

    Table tornadoes = Table.read().csv("../data/tornadoes_1950-2014.csv");

Note that the file is addressed relative to the current working directory. You may have to change it for your code. 

### Viewing table metadata

Often, the best way to start is to print the column names for reference:

```java
tornadoes.columnNames();

>>	Date, Time, State, State No, Scale, Injuries, Fatalities, Start Lat, Start Lon, 	Length, Width]
```

The *shape()* method displays the row and column counts:

    tornadoes.shape();
    >>	59945 rows X 10 cols

*structure()* shows the index, name and type of each column

    tornadoes.structure();
    
    >>  Structure of data/tornadoes_1950-2014.csv
            Index Column Names Column Type 
            0     Date         LOCAL_DATE  
            1     Time         LOCAL_TIME  
            2     State        CATEGORY    
            3     State No     DOUBLE     
            4     Scale        DOUBLE     
            5     Injuries     DOUBLE     
            6     Fatalities   DOUBLE     
            7     Start Lat    DOUBLE       
            8     Start Lon    DOUBLE       
            9     Length       DOUBLE       
            10    Width        DOUBLE       

Like many Tablesaw methods, *structure()* returns a table.  You can then produce a string representation for display. For convenience, calling *toString()* on a table invokes *print()*, which produces a string representation of the table table. To display the table then, you can simply call. 

`System.out.println(table);`

You can also perform other table operations on it. For example, the code below removes all columns whose type isn’t DOUBLE:

    tornadoes.structure().selectWhere(column("Column Type").isEqualTo("DOUBLE"));
    
    >>  Structure of data/tornadoes_1950-2014.csv
            Index Column Name Column Type 
            3     State No    DOUBLE     
            4     Scale       DOUBLE     
            5     Injuries    DOUBLE     
            6     Fatalities  DOUBLE     

Of course, that also returned a table. We’ll cover selecting rows in more detail later.

### Previewing data

The *first(n)* method returns a new table containing the first n rows.

    table.first(3);
    
    >>  Date       Time     State Scale Injuries Fatalities Start Lat Start Lon Length Width 
        1950-01-03 11:00:00 MO    3     3        0          38.77     -90.22    9.5    150.0 
        1950-01-03 11:00:00 MO    3     3        0          38.77     -90.22    6.2    150.0 
        1950-01-03 11:10:00 IL    3     0        0          38.82     -90.12    3.3    100.0 

### Mapping operations

Mapping operations in Tablesaw take one or more columns as inputs and produce a new column as output. We can map arbitrary expressions onto the table, but many common operations are built in. You can, for example, calculate the difference in days, weeks, or years between the values in two date columns. The method below extracts the Month name from the date column into a new column.

```java
StringColumn month = tornadoes.dateColumn("Date").month();
```

Now that you have a new column, you can add it to a table:

```java
tornadoes.addColumn(month);
```

You can remove columns from tables to save memory or reduce clutter:

```java
tornadoes.removeColumn("State No");
```

### Sorting

Now lets sort the table in reverse order by the id column. The negative sign before the name indicates a descending sort.

```java
tornadoes.sortOn("-Fatalities");
```
### Descriptive statistics

Descriptive statistics are calculated using the summary() method:

```java
table.column("Fatalities").summary().print();

>>  Measure  Value     
    n        1590.0    
    Missing  0.0       
    Mean     4.2779875 
    Min      1.0       
    Max      158.0     
    Range    157.0     
    Std. Dev 9.573451
```

### Filtering

You can write your own methods to filter rows, but it’s easier to use the built-in filter classes as shown below:

```java 
result = tornadoes.selectWhere(DOUBLEColumn("Fatalities").isGreaterThan(0));

result = tornadoes.selectWhere(dateColumn("Date").isInApril());

result = tornadoes.selectWhere(either
           (DOUBLEColumn("Width").isGreaterThan(300)),   // 300 yards
           (DOUBLEColumn("Length").isGreaterThan(10)));  // 10 miles

result = tornadoes.select("State", "Date").where(column("Date").isInQ2());
```

The last example above returns a table containing only the columns named in *select()* parameters, rather than all the columns in the original.

### Totals and sub-totals

Column metrics can be calculated using methods like *sum()*, *product()*, *mean()*, *max()*, etc.

You can apply those methods to a table, calculating results on one column, grouped by the values in another.

```java
Table injuriesByScale = tornadoes.summarize("Injuries", median).by("Scale");
injuriesByScale.setName("Median injuries by Tornado Scale");
```
This produces the following table, in which Group represents the Tornado Scale and Median the median injures for that group:

    Median injuries by Tornado Scale
    >>  Group Median 
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
CrossTab.counts(t, t.stringColumn("State"), t.shortColumn("Scale"));
```

```java
>>  Crosstab Counts: State x Scale
          -9 0     1     2    3    4   5  total 
    AL    0  623   769   424  141  37  11 2005  
    AR    0  485   666   419  161  28  0  1759  
    AZ    0  145   70    15   2    0   0  232   
    ... snipped 
    WI    0  447   517   267  55   18  2  1306  
    WV    0  37    67    22   7    0   0  133   
    WY    0  405   178   51   11   0   0  645   
    Total 44 27253 20009 9067 2580 670 68 59691`
```

### Putting it all together

Now that you've seen the pieces, we can put them together to perform a more complex data analysis. Lets say we want to know how frequently Tornadoes occur in the summer. Here''s one way to approach that:

Let's start by getting only those tornadoes that occured in the summer. 

```java
DateColumn date = tornadoes.dateColumn("Date");

Filter summerFilter =
                anyOf( 	// from June 21st, through September 22nd       
                        both(date.month().isEqualTo("JUNE"), 
                            date.dayOfMonth().isGreaterThanOrEqualTo(21)),
    					date.month().isIn("JULY", "AUGUST"),
                        both(date.month().isEqualTo("SEPTEMBER"),
                            date.dayOfMonth().isLessThan(22)));

Table summer = tornadoes.selectWhere(summerFilter);
```

To get the frequency, we calculate the difference in days between successive tornadoes. The *lag()* method creates a column where every value equals the previous value (the prior row) of the source column. Then we can simply get the difference in days between the two dates. DateColumn has a method *daysUntil()* that does this.  It returns a NumberColumn that we'll call "delta". 

```java
summer = summer.sortAscendingOn("Date", "Time");
summer.addColumn(summer.dateColumn("Date").lag(1));

DateColumn summerDate = summer.dateColumn("Date");
DateColumn laggedDate = summer.dateColumn("Date lag(1)");

DOUBLEColumn delta = laggedDate.daysUntil(summerDate);
summer.addColumn(delta);
```

Now we simply caculate the mean of the delta column. Splitting on year keeps us from inadvertently including the time between the last tornado of one summer and the first tornado of the next.

```java
Table summary = summer.summarize(delta, mean, count).by(summerDate.year());
```

Printing summary gives us the answer by year. 

```
     Date year  |  Mean [Date lag(1) - Date]  |  Count [Date lag(1) - Date]  |
    --------------------------------------------------------------------------
        1950.0  |         1.9782608695652173  |                        47.0  |
        1951.0  |          4.684210526315789  |                        76.0  |
        1952.0  |          5.707692307692312  |                        65.0  |
        1953.0  |          4.805194805194803  |                        77.0  |
...
```

To get a DOUBLE for the entire period, we can take the average of the annual means. 

```
summary.nCol(1).mean();
>>	Average days between tornadoes in the summer: 1.761269943549373
```



### Saving your data

To save a table, you can write it as a CSV file:

```java
tornadoes.write().csv("data/rev_tornadoes_1950-2014.csv");
```

And that’s it for the introduction. Stay tuned for more info about advanced features.

