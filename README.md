Tablesaw
=======
   
Tablesaw is the replacement for Outlier. It's a high-performance, in-memory, table-like data structure, 
a set of tools for data manipulation, and an integrated, column-oriented storage format. 

With Tablesaw, you can import, sort, transform, filter, and summarize tables of over 100 million rows on a laptop. 
(Some tests work today on columns of 1 billion records; more tests at this scale will be added.)
To get here, we've taken tools and tricks developed for high-frequency trading (e.g. primitive-backed arrays) and 
data warehouses (e.g. compressed, column-oriented storage and data structures), 
and used them to maximize what you can do in a single VM.

The intent is to make all but the biggest data wrangling jobs approachable without the complexity of distributed computing (HDFS, Hadoop, etc.). 
Analysis is more productive because there's less engineering overhead and shorter iteration cycles, and the fluent API is designed to let developers express 
operations in a concise and readable fashion. 

While Tablesaw provides general-purpose analytic support, the goal is to provide especially rich functionality for 
working with time-series, including specialized column types for local dates, times and datetimes (combined). 

At the same time, we want to provide a degree of usability that is at least the equal of Pandas and R data-frames.

Better yet, with Java 9, you'll be able to do your work interactively in the REPL, without writing a bunch of code before you get started. 

For more information and examples see: https://javadatascience.wordpress.com

## An introduction in 9 lines of code

Here's an example. In 9 lines of trivial code, we will:

* Read a CSV file
* Print the first few rows for a peak at the data
* Sort the table by column name
* Run descriptive statistics (mean, min, max, etc.) on a column
* Remove a column
* Create a new column as the sum of the values in two existing columns
* Filter some rows
* Save the new version as a file

### Read a CSV file
Here we read a csv file of bus stop data. First, we say what column types are present.

```java

    ColumnType[] types = {INTEGER, TEXT, TEXT, FLOAT, FLOAT};
    Table table = CsvReader.read("data/bus_stop_test.csv", types);

```

### Viewing data
Take a look at some data. The head(n) method returns the first n rows.

```java

    table.head(3);
    
```

produces:

    data/bus_stop_test.csv
    stop_id stop_name                stop_desc                                                stop_lat  stop_lon   
    66      4925 CRAIGWOOD/FM 969    Southeast corner of CRAIGWOOD and FM 969 - Nearside      30.28417  -97.65985  
    252     200 TRINITY/2ND          Northeast corner of TRINITY and 2ND - Mid-Block          30.263842 -97.740425 
    462     851 RUTLAND/PARK VILLAGE Southeast corner of RUTLAND and PARK VILLAGE - Mid-Block 30.36547  -97.69752  

### Sorting by column
Now that we've some some data, lets sort the table in reverse order by the id column

```java

    table.sortDescendingOn("stop_id");
```

### Removing a column
Let's remove the "stop_desc" column:

```java

    table.removeColumn("stop_desc");
```    
### Descriptive statistics
Let's take a look at the longitude column:

```java

    table.column("stop_lon").describe();
```

This outputs:

    Descriptive Stats 
    n: 2729
    missing: 0
    min: -97.9911
    max: -97.37039
    range: 0.62070465
    mean: -97.73799133300781
    std.dev: 0.049913406
    variance: 0.0024913481902331114

### Create new columns from the data in existing columns

Now let's add a column derived from the existing data. We can map arbitrary lambda expressions
onto the data table, but many common operations (add, subtract, multiply, max, etc.) are built in. For example, 
for a column-wise addition:

```java

    Column total = add(table.get("stop_lat", "stop_lon"));
```

(Yeah, I know that's a stupid example. Imagine it was two columns you'd actually want to add.)

### Filtering Rows

Let's filter out records that don't have Stop IDs between 524 and 624. Filters can also be arbitrary
lambda expressions, but it's easier to use the filter classes as shown below:

```java

Table f = table.selectIf(column("stop_id").isBetween(524, 624)));
```

New table "f" has all the columns from "table", but only rows where the value of stop_id 
is in the range x where 524 < x < 624.

### Write the new CSV file to disk

```java

CsvWriter.write("filtered_bus_stops.csv", f);
```

And there you have it. 

This is just the beginning of what Tablesaw can do. Other features include:

* Powerful Group-by functionality (aka: Split, Aggregate, Combine) 
* Map arbitrary lambda expressions over tables

More advanced operations are described on the project web site:
 https://javadatascience.wordpress.com
 
## A work-in-progress
Tablesaw is in an experimental state, with a production release planned for late 2016. 
Not all of the functionality of Outlier has been implemented yet, 
 and a great deal of additional functionality is planned, including window operations (like rolling averages), 
 outlier detection, and integrated machine-learning.