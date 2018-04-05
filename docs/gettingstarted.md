

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

## The Tao 

As you would expect, Tablesaw is all about tables, and tables are made of columns. That's where we'll start.

### Columns

A column is a named, one-dimensional collection of data. All data in a column must be of the same type. 

Currently, Tablesaw supports columns for Strings, double-precision floating point numbers, booleans, LocalDates, LocalTimes, and LocalDateTimes. The date and time types are based on the java.time classes introduced in Java 8.

To create a column of numbers you can use one of its *create()* methods:

```java
double[] numbers = {1, 2, 3, 4};
NumberColumn nc = NumberColumn.create("Test", numbers);
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

### Selections

Before going on to tables, we should talk about selections. Selections are used to filter both tables and columns. Often they work behind the scenes, but sometimes you work with the directly.  For example, lets go back to our NumberColumn containing the values {1, 2, 3, 4}. You can filter that column by sending it a message. For example: 

```java
nc.isLessThan(3);
```

This operation returns a *Selection*. You can think of selections as a bitmap of the same size as the original column or table. The method above returns a selection that, effectively, contains 1, 1, 0, 0, since the first two values in the column are less than three, and the last two are not. 

In this case, what you probably wanted was not a Selection object, but a new NumberColumn that contains only the values that passed the filter. To get this, you use the *selectWhere(aSelection)* method:

```java
NumberColumn filtered = nc.selectWhere(nc.isLessThan(3));
```

This extra step is a necessary evil. It's a bit tedious, but it lets us combine filters. For example: 

```java
NumberColumn filtered = nc.selectWhere(nc.isLessThan(3).and(nc.isOdd());
```

If the methods returned columns, the couldn't be combined in the same way. 

##### Selecting by index

These examples show how to select using predicates. You can also use a selection to retrieve the value at a specific index, or indexes. All of the following are supported:

```
selectWhere(Selection.with())
```



#### Map functions

There is nothing special about map operations; they're simply methods on columns that return new Columns as their result. You've already seen one: The column *multiply(aNumber)* operation above is a map function.

#### Reduce functions: Summarizing a column 

Sometimes you want to derive a value that summarizes in some sense the data in a column. Aggregate functions do just that. All columns support some aggregate functions: *min*() and *max*(), for example, plus *count()*, *countUnique()*, and *countMissing()*.  

NumberColumn supports many kinds, as you would expect: *sum*, *count*, *range*, *variance*, *sumOfLogs*, and many others. Boolean columns supports relatively few: *all()*, which return *true* if all of the values in the column are *true*. The functions *any()*, and *none()*,  returns true if any or none the values in the column are *true*, respectively. The functions *countTrue()*, and *countFalse()* are also available.

#### Groups

Sometimes you want to summarize by group, rather than across the entire column of values.  

### Tables
As described above, a table is a named collection of columns. All columns in the table must be the same size, although missing values are allowed. A table can contain any combination of column types.

Because Tablesaw excels at manipulating tables, we use them whenever we can.  When you ask tablesaw for the structure of a table, the answer comes in the form of a table.

Tables also use selections to perform filtering. 

#### Groups in tables

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



[^1]: The method shown does not actually "produce" any output For that you would call *System.out.println()*. For brevity, output will be shown going forward indented by one tab beneath the code that produced it.





