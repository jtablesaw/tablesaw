

# Getting started with Tablesaw

Java is a great language, but it wasn't designed for data analysis. Tablesaw makes it easy to do data analysis in Java, but to accomplish that, we did things a little differently than you might expect. After we get you up and running, we tell you everything you need to know to use Tablesaw happily and productively.

## Setup

First, Tableasaw requires Java 8 or newer. 

Second, you need to add the dependency to your pom file. It's available on Maven Central.

````
<dependency>
    <groupId>tech.tablesaw</groupId>
    <artifactId>tablesaw-core</artifactId>
    <version>LATEST</version>
</dependency>
````

That's it for setup. On to design

## Tables and Columns (all the way down) 

As you would expect, Tablesaw is all about tables, and tables are made of columns. We'll start with columns.

### Columns

A column is a named, one-dimensional collection of data. All data in a column must be of the same type. 

Currently, Tablesaw supports columns for Strings, double-precision floating point numbers, booleans, LocalDates, LocalTimes, and LocalDateTimes. The date and time types are based on the java.time classes introduced in Java 8.

To create a column of numbers you can use one of its *create()* methods:

```java
double[] numbers = {1, 2, 3, 4};
NumberColumn nc = DoubleColumn.create("Test", numbers);
out(nc.print());

```
which produces: 
```java
Column: Test
1.0
2.0
3.0
4.0
```
Each column has an associated 0-based index. To get an individual value call *get()* with the index. 
```java
nc.get(2);
```
which returns 3.0.

#### Array Operations

Because columns are so important, Tablesaw makes them easy to work with. Many operations that work on numbers in standard Java, for example, work on columns of numbers in Tablesaw. To multiply every value in a column by 4, we use the *multiply()* method, which returns a new column of the same size as the original.

```java
NumberColumn nc2 = nc.multiply(4);
```
producing: [^1] 

```java
Column: Test * 4.0
4.0
8.0
12.0
16.0
```
There are so many columnar operations in Tablesaw that, as a general rule, if you find yourself writing a for loop to process a column or table, you may be missing something. 

Generally, Tablesaw will provide a name for columns created this way. If you don't like the name, you can change it by calling *setName("new name")* on the column.

#### Objects and Primitives

Many Java programs and programmers work exclusively with Objects, rather than primitives. In Tablesaw, we use primitives whenever possible because they use so much less memory than their boxed alternatives.  A Byte object, for example, uses as much memory as a primitive double, even though bytes have a range of only 256 values. 

There is a price for this frugality. When you work with primitives, you forgo some common java capabilities, like the use of standard Java 8 predicates. While Java thoughtfully provides some specialized predicate interfaces (e.g. *IntPredicate*), they don't provide any primitive *BiPredicate* implementations. Without an IntBiPredicate, we can't implement operations like a < b. So we were left to roll our own. You can find them in the package *tech.tablesaw.filtering.predicates*. 

For that reason, the syntax for Tablesaw may sometimes seem a bit odd relative to typical Java, but it doesn't take long to learn. 

### Selections

Before going on to tables, we should talk about selections. *Selections* are used to filter both tables and columns. Often they work behind the scenes, but sometimes you use them directly.  For example, consider our NumberColumn containing the values {1, 2, 3, 4}. You can filter that column by sending it a message. For example: 

```java
nc.isLessThan(3);
```

This operation returns a *Selection*. Logically, it's a bitmap of the same size as the original column or table. The method above, effectively, returns 1, 1, 0, 0, since the first two values in the column are less than three, and the last two are not. 

What you probably wanted was not a Selection object, but a new NumberColumn that contains only the values that passed the filter. To get this, you use the *where(aSelection)* method:

```java
NumberColumn filtered = nc.where(nc.isLessThan(3));
```

This extra step is a necessary evil. It's a bit tedious, but it lets us combine filters. For example: 

```java
NumberColumn filtered = nc.where(nc.isLessThan(3).and(nc.isOdd());
```

If the methods returned columns, the couldn't be combined in the same way. 

##### Selecting by index

These examples show how to select using predicates. You can also use a selection to retrieve the value at a specific index, or indexes. All of the following are supported:

```java
nc.where(Selection.with(4, 42));  				// returns two rows with the given indexes
nc.where(Selection.selectNRowsAtRandom(500));
nc.where(Selection.withRange(10, 110));
nc.where(Selection.withoutRange(10, 50));
```

Obviously, if you have several columns of the same size (i.e. length) as you would in a table of data, you can make a selection with one column and use it to filter another:

```java
NumberColumn result = firstColumn.where(someOtherColumn.startsWith("foo"));
```

#### Map functions

There is nothing special about map operations; they're simply methods on columns that return new Columns as their result. You've already seen one: The column *multiply(aNumber)* method above is a map function. To multiple two columns, use:

```java
nc1.multiply(nc2);
```

In this case, each value in column nc1 is multiplied by the corresponding value in nc2, rather than by a scalar constant as in the example above.

There are many map functions built-in for the various column types. Here are some examples for StringColumn:

```java
s = aStringColumn.upperCase();
s = s.replaceFirst("foo", "bar")
s = s.substring(3, 10);
s = s.padEnd(4, 'x');						// put 4 x chars at the end of each string

// this returns the common prefix of each row in two columns
y = s.commonPrefix(anotherStringColumn);

// this returns a measure of the similarity (levenshtein distance) between two columns
nc = s.distance(anotherStringColumn);
```

There are many others. 

#### Reduce functions: Summarizing a column 

Sometimes you want to derive a value that summarizes in some sense the data in a column. For tables, aggregate functions do just that. All columns support some aggregate functions: *min*() and *max*(), for example, plus *count()*, *countUnique()*, and *countMissing()*.  These are described below. 

NumberColumn supports aggregation directly. Many functions are available: *sum*, *count*, *range*, *variance*, *sumOfLogs*, and many others. Boolean columns supports relatively few: *all()*, which return *true* if all of the values in the column are *true*. The functions *any()*, and *none()*,  returns true if any or none the values in the column are *true*, respectively. The functions *countTrue()*, and *countFalse()* are also available.

To calculate the standard deviation of a column, you would call:

```java
nc.standardDeviation();			// returns the standard deviation of all values
```

When we discuss tables below, we'll show how to summarize a column to create sub-totals by the values in one or more grouping columns.

### Tables
As described above, a table is a named collection of columns. All columns in the table must be the same size, although missing values are allowed. A table can contain any combination of column types.

#### Creating Tables

You can create a table in code. For example:

```java
String[] animals = {"bear", "cat", "giraffe"};
double[] cuteness = {90.1, 84.3, 99.7};

Table cuteAnimals = Table.create("Cute Animals)
	.addColumns(
		StringColumn.create("Animal types", animals),
		DoubleColumn.create("rating", cuteness));
```

More frequently, you will load a table from a CSV or other delimited text file. 

```java
Table x = Table.read().csv("testing.csv");
```

Tablesaw does a pretty good job at guessing the column types for many data sets, but you can specify them    if it guesses wrong, or to improve performance. Numerous other options are available, such as specifying whether or not there's a header, using a non-standard delimiter, and so on. These are described in the section on IO.

The IO section also shows how you can read data from a database.  

#### Exploring Tables

Because Tablesaw excels at manipulating tables, we use them whenever we can.  When you ask tablesaw for the structure of a table, the answer comes back in the form of a table where one column contains the column names, etc.  The structure() method is one of several that are great for getting to know your table. Here are some examples.

```java
Table structure = bushTable.structure();

>	          Structure of bush.csv          
	 Index  |  Column Name  |  Column Type  |
	-----------------------------------------
	     0  |         date  |   LOCAL_DATE  |
	     1  |     approval  |       NUMBER  |
	     2  |          who  |       STRING  |

String shape = bushTable.shape();
>	323 rows X 3 cols

Table head = bushTable.first(3);
>	             bush.csv              
	    date     |  approval  |  who  |
	-----------------------------------
	 2004-02-04  |      53.0  |  fox  |
	 2004-01-21  |      53.0  |  fox  |
	 2004-01-07  |      58.0  |  fox  |
                     
Table tail = myTable.last(3);
> etc.
```

Table's toString() method returns a String representation like those shown above. It returns a limited number of rows by default, but you can also use *table.printAll()*, or *table.print(n)* to get the output you want.

Of course, this is just the beginning of exploratory data analysis. You can also use numeric and visual tools to explore your data. These facilities are described in the documentation on statistics and plotting, respectively.

#### Working with a table's columns

Often you'll want to work with specific columns in a table. Here are some useful methods:

```java
table.columnNames();  			// returns all column names
table.column("Foo");			// returns the column named 'Foo' if it's in the table.
table.column(0);				// returns the first column in the table;

// removing columns
table.removeColumns("Foo");			// keep everything but foo
table.retainColumns("Foo", "Bar");  // only keep foo and bar
table.removeColumnsWithMissingValues();

// adding columns
table.addColumns(column1, column2, column3);
```

There are other relevant operations in Table. See the API documentation on Table for the full details on all of its functionality.

####  Working with rows

As with columns, many options exist for working with tables in row-wise fashion. Here are some useful ones:

```java
Table result = table.dropDuplicateRows();
result = table.dropRowsWithMissingValues();
table.addRow(43, sourceTable);	// adds row 43 from sourceTable to the receiver
table.sample(200);				// select 200 rows at random from table 
int[] indexes = table.rows();	// returns an int array from 0 to table.rowCount()
```

You can also perform arbitrary operations on each row in the table.  One way is to just iterate over the rows and work with each column separately:

```java
for (Row row : table) {
    System.out.println(column1.get(row.rowNumber())); // etc.
}
```

There are better ways, however. Another approach lets you skip the iteration and just provide a Consumer for each row.

```java
// Create a consumer as an object or lambda (show below)
Consumer<Row> doable = row -> {
    if (row.getRowNumber() < 5) {
        System.out.println("On "
                           + row.getDate("date")
                           + ": "
                           + row.getDouble("approval"));
    }
};
// apply the lambda
table.doWithRows(doable);
```

If you need to process more than one row at a time, there are several methods to help. 

```java
// work with a sliding window of rows
// for example 0, 1, and 2, then 1, 2, and 3. etc.
table.rollWithRows(consumer, 3);		

// work with a shifting window of rows
// for example rows 0 through 4, then 5 through 9, etc.
table.stepWithRows(consumer, 5);		

table.doWithRowPairs(Pairs)	// easy syntax for working with each pair of rows
```



#### Sorting

To sort a table, you can just use the sort() function and give it a column name (or two):

```java
Table sorted = table.sort("foo", "bar", "bam");
```

The above code sorts in ascending order by default.  Other options are shown below:

```java
sorted = table.sortDescending("foo");
sorted = table.sortAscending("bar"); 	// just like sort(), but makes order explicit.

/* sort on foo ascending, then bar descending. Note the minus sign preceding the name of column bar. */
sorted = table.sort("foo", "-bar");		 
```



#### Filtering tables with selections

Tables also use selections to perform filtering. The basic approach is similar.

```java
Table t = Table.create("test").addColumns(nc1, nc2);
Table result = t.where(nc1.isGreaterThan(4));
```

Note that the *where()* method for tables also takes a selection as its argument. The result is a new table like the original, except that it only contains the rows where the value in the *nc1* column is > 4.

##### Combining filters into complex queries

Query filters can be combined using the logical operations *and*, *or*, and *not*. These are implemented on the table class. The *not()* method takes a single selection as its argument, while *and()* and *or()* take a comma separated list of them. The rather contrived code below shows all three logical operators combined.

```java
Table result = t.where(
	t.and(nc1.isGreaterThan(4),
         t.or(t.not(nc2.isLessThanOrEqualTo(5)),
         		nc2.isEven()));
```

##### Selecting columns in a query statement 

If you don't need all the columns from the filtered table in the result, you can limit columns as well as rows, using *select().where()*.

```java
Table result = t.select(nc1).where(nc2.isEven());
```

 The *select()* method takes a column or array of columns as arguments. 

#### Summarizing tables

##### Groups

Tables can be "sliced" for calculating subtotals. The method *splitOn(CategoricalColumns)* and *splitOn(CategoricalColumnNames)* both return an object called TableSliceGroup. A TableSlice is, effectively, a window into a backing table. TableSliceGroup is a collection of these windows, each of which looks and feels like its own table. 

For the most part, tables are sliced according to the value of a column or columns. For example, if you have a string column called "province", and another called "status," there will be a slice for each combination of provence and status in the table. 

The usual way to calculate values is  to use the *summarize()* method: 

```java
Table summary = table.summarize("sales", mean, sum, min, max).by("province", "status");
```

It's important to recognize, that the column need not exist when summarize is invoked. Any map function can be used in the *by()* statement to group on calculated values. A common use case is in handling dates. You can summarize sales by day-of-week, as follows:

```java
Table summary = table.summarize("sales", mean, median)
     .by(table.dateColumn("sales date").dayOfWeek());
```

which says "return the mean and median sales by day of week."

> **Key point**: Tables are usually split based on columns, but the columns can be calculated on the fly

#### Chaining table operations

Consider the case where you filter a table and want to filter the result. For example, 

```java
double average = t.selectWhere(t.stringColumn("foo")
	.startsWith("bar"))
     .selectWhere(stringColumn("bam").endsWith("bas"))
      .nCol("age").mean();
```



[^1]: The method shown does not actually "produce" any output For that you would call *System.out.println()*. For brevity, this "faux" output will be shown indented by one tab beneath the code that produced it.





