Tablesaw
=======
   
Tablesaw is a high-performance, in-memory data table, plus tools for data manipulation and a column-oriented storage format. In Java.

__With Tablesaw, you can import, sort, transform, filter, and summarize tables with hundreds of millions of rows on a laptop.__ 
Tablesaw uses tricks from high-frequency trading apps (e.g. primitive collections) and 
data warehouses (e.g. compressed, column-oriented storage and data structures), to maximize what you can do in a single VM.

The goal is to make all but the biggest data wrangling jobs approachable without distributed computing (HDFS, Hadoop, etc.). 
Analysis is more productive with less engineering overhead and shorter iteration cycles. A fluent API lets developers express operations in a concise and readable fashion. 

Tablesaw provides general-purpose analytic support, with rich functionality for working with time-series, 
including specialized column types for dates, times, and timestamps. 

It's designed for ease-of-use, and with Java 9, you'll be able to work interactively in the REPL. 

For more information and examples see: https://javadatascience.wordpress.com

## Getting started
Tablesaw uses maven, but we're not on Maven Central yet.
        
Download the latest release from:

    https://github.com/lwhite1/tablesaw/releases

Build and install: 

    mvn clean install
    
Then add a dependency to your pom file:
    
    <dependency>
        <groupId>com.github.lwhite1</groupId>
        <artifactId>tablesaw</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    
Tablesaw requires Java 8 or newer.    

## An introduction to Tablesaw

Lets explore a tornado data set using Tablesaw. Here's what we'll cover:

* Read and writing CSV files
* Viewing table metadata
* Adding and removing columns
* Printing the first few rows for a peak at the data
* Sorting 
* Running descriptive stats (mean, min, max, etc.)
* Performing mapping operations over columns
* Filtering rows 
* Using groups and reduce functions to compute stats for subsets
* Storing tables in Tablesaw's compressed columnar storage format

All the data is in the projects _data_ folder.

### Read a CSV file
Here we read a csv file of tornado data. First, we say what column types are present.

```java

    ColumnType[] CT = {LOCAL_DATE, LOCAL_TIME, CATEGORY, INTEGER, INTEGER, INTEGER, 
                       INTEGER, CATEGORY, FLOAT, FLOAT, FLOAT, FLOAT, FLOAT};
    Table tornadoes = Table.fromCSV(CT, "data/1950-2014_torn.csv");
```
Specifying the column types is _the_ most tedious part of using Tablesaw. That requirement will be removed soon.

### Viewing table metadata

Often, the best way to start is to print the column names for reference:

```java
    
    tornadoes.columnNames();
```
which produces: 

```java

	[Date, Time, State, State No, Scale, Injuries, Fatalities, Start Lat, Start Lon, Length, Width]
```

The _shape()_ method displays the row and column counts: 

```java
    
    tornadoes.shape();
    
    >> 59945 rows X 10 cols

```
So, this table has nearly 60,000 rows and ten columns. 

The _structure()_ method shows the index, name and type of each column

```java

    tornadoes.structure().print();
    
    >> Structure of data/tornadoes_1950-2014.csv
	Index Column Names Column Type 
	0     Date         LOCAL_DATE  
	1     Time         LOCAL_TIME  
	2     State        CATEGORY    
	3     State No     INTEGER     
	4     Scale        INTEGER     
	5     Injuries     INTEGER     
	6     Fatalities   INTEGER     
	7     Start Lat    FLOAT       
	8     Start Lon    FLOAT       
	9     Length       FLOAT       
	10    Width        FLOAT       

        
```
Note the print() method in _tornadoes.structure().print()_.
Like many Tablesaw methods, _structure()_ returns a table object; print() produces a 
string representation for display. Because structure returns a table, you can perform other operations on it, like:

```java
    
    tornadoes.structure().selectWhere(column("Column Type").isEqualTo("INTEGER"));
    
    >> Structure of data/tornadoes_1950-2014.csv
	Index Column Name Column Type 
	3     State No    INTEGER     
	4     Scale       INTEGER     
	5     Injuries    INTEGER     
	6     Fatalities  INTEGER     

    
```
Of course, that _also_ returned a table, this one containing only records for columns of INTEGER type. 
We'll cover filtering or selecting rows in more detail later.

### Viewing data
The head(n) method returns the first n rows.

```java

    table.head(3);
    >>
    Date       Time     State Scale Injuries Fatalities Start Lat Start Lon Length Width 
	1950-01-03 11:00:00 MO    3     3        0          38.77     -90.22    9.5    150.0 
	1950-01-03 11:00:00 MO    3     3        0          38.77     -90.22    6.2    150.0 
	1950-01-03 11:10:00 IL    3     0        0          38.82     -90.12    3.3    100.0 

```

### Mapping operations

Now let's add a column derived from the existing data. We can map arbitrary lambda expressions
onto the data table, but many, many common operations are built in. You can, 
for example, calculate the difference in days, weeks, or years between the values in two date columns.
The method below extracts the Month name from the date column into a new column.

```java

    CategoryColumn month = tornadoes.localDateColumn("Date").month();
```
Mapping operations in Tablesaw take one or more columns as inputs and produce an output column. 

Once you have a new column, you can add it to a table:

```java

    tornadoes.addColumn(2, month);
```
You can also remove columns from tables, if you need to save memory or reduce clutter. 

```java

    tornadoes.removeColumn("State No);
```


### Sorting by column
Now lets sort the table in reverse order by the id column

```java

    tornadoes.sortDescendingOn("Fatalities");
```

### Descriptive statistics

Descriptive statistics are calculated using the _describe()_ method:

```java

    table.column("Fatalities").describe();
```

This outputs:

		Measure  Value     
		n        1590.0    
		Missing  0.0       
		Mean     4.2779875 
		Min      1.0       
		Max      158.0     
		Range    157.0     
		Std. Dev 9.573451  

### Filtering Rows

To filter rows you can use arbitrary logic, but it's easier to use the built-in filter classes as shown below:

```java

    tornadoes.selectWhere(column("Fatalities").isGreaterThan(0));
    
    tornadoes.selectWhere(column("Date").isInApril());
    
    tornadoes.selectWhere(either(column("Width").isGreaterThan(300)),   // 300 yards
    			 	(column("Length").isGreaterThan(10)));    // 10 miles
    							
    tornadoes.select("State", "Date", "Scale").where(column("Date").isInQ2());
    
```
The last example above returns a table containing only the three columns named in select() parameters.

### Performing totals and sub-totals

Column metrics can be calculated using methods like sum(), product(), mean(), max(), etc.

You can also apply those methods to a table, calculating results on one column, 
grouped by the values in another.

```java

    Table injuriesByScale = tornadoes.reduce("Injuries", "Scale", median);
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

### Write the new CSV file to disk

```java

    tornadoes.exportToCsv("data/rev_tornadoes_1950-2014.csv");
```
### Read and write data from the Tablesaw format

Once you've imported data you can use Tablesaw's own disk format to save it. 
In Tablesaw format, reads and writes are an order of magnitude faster than optimized CSV operations.

```java

    String dbName = tornadoes.save("/tmp/tablesaw/testdata");

    Table tornadoes = Table.readTable(dbName);
```
This is just the beginning of what Tablesaw can do. More information is available on the project web site:
 https://javadatascience.wordpress.com
 
## A work-in-progress
__Tablesaw is moving towards stability of the core functionality and APIs__. A production release planned for early Q3 2016. 
A great deal of additional functionality will follow the initial release, including window operations (like rolling averages), outlier detection, database integration, and integrated machine-learning.
