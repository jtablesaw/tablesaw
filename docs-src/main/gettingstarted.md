# Getting started with Tablesaw

Java is a great language, but it wasn't designed for data analysis. Tablesaw makes it easy to do data analysis in Java. 

This tutorial will help you get up and running, and introduce some of Tablesaw's basic functionality.

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

## Tables and Columns 

As you would expect, Tablesaw is all about tables, and tables are made of columns. We'll start with columns.

### Columns

A column is a named, one-dimensional collection of data. It may or may not be part of a table. All data in a column must be of the same type. 

Tablesaw supports columns for Strings, floats, doubles, ints, shorts, longs, booleans, LocalDates, LocalTimes, Instants, and LocalDateTimes. The date and time columns are comparable with the java.time classes introduced in Java 8. 

To create a column you can use one of its static *create()* methods:

```java
@@snip [create_column](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

which produces:

```java
@@snip [create_column](./output/tech/tablesaw/docs/GettingStarted.txt)
```

Each column has an associated 0-based index. To get an individual value call *get()* with the index. 

```java
@@snip [column_index](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```
which returns 3.0.

#### Array Operations

Tablesaw makes columns easy to work with. Operations that work on numbers in standard Java, for example, often work on *columns* of numbers in Tablesaw. To multiply every value in a column by 4, we use the *multiply()* method, which returns a new column like the original.

```java
@@snip [column_multiply](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```
producing:

```java
@@snip [column_multiply](./output/tech/tablesaw/docs/GettingStarted.txt)
```
As you can see, the values are 4x the values in the original. The new column's name is made by combining the original
"Test" and the operation (* 4). You can change it if you like using `setName(aString)`.

There are so many columnar operations in Tablesaw that, as a general rule, if you find yourself writing a for
loop to process a column or table, you may be missing something. 

#### Objects and Primitives

Many Java programs and programmers work exclusively with Objects, rather than primitives. In Tablesaw, we often use
primitives because they use *much* less memory than their boxed alternatives.  A Byte *object*, for example, uses as
much memory as a primitive double, even though bytes have a range of only 256 values. 

There is a price for this frugality. When you work with primitives, you forgo some common java capabilities, like the
use of standard Java 8 predicates. While Java thoughtfully provides some specialized predicate interfaces
(e.g. *IntPredicate*), they don't provide any primitive *BiPredicate* implementations, nor do their primitive
interfaces cover all primitive types. Without an IntBiPredicate, we can't implement operations like a < b.
So we were left to roll our own. You can find them in the package *tech.tablesaw.filtering.predicates*.
They work like the standard objects. 

This just covers the most basic information about columns. You can find more in the section on
[Columns](https://jtablesaw.github.io/tablesaw/userguide/columns), or in the Javadocs for the
[api package](http://www.javadoc.io/page/tech.tablesaw/tablesaw-core/latest/tech/tablesaw/api/package-summary.html)
and the [columns package](http://www.javadoc.io/page/tech.tablesaw/tablesaw-core/latest/tech/tablesaw/columns/package-summary.html).

### Selections

Before going on to tables, we should talk about *selections*. *Selections* are used to filter both tables and columns.
Often they work behind the scenes, but you can use them directly. For example, consider our `DoubleColumn` containing
the values {1, 2, 3, 4}. You can filter that column by sending it a message. For example: 

```java
@@snip [selection_one](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

This operation returns a *Selection*. Logically, it's a bitmap of the same size as the original column. The method above, effectively, returns 1, 1, 0, 0, since the first two values in the column are less than three, and the last two are not. 

What you probably wanted was not a `Selection` object, but a new `DoubleColumn` that contains only the values that passed the filter. To get this, you use the *where(aSelection)* method to apply the selection:

```java
@@snip [selection_two](./src/main/java/tech/tablesaw/docs/GettingStarted.java)

@@snip [selection_two](./output/tech/tablesaw/docs/GettingStarted.txt)
```
Doing this in two steps provides many benefits. For one, it lets us combine filters. For example: 

```java
@@snip [selection_three](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

If the methods returned columns directly, they couldn't be combined this way.  It also lets us use the same method for
filtering both tables and columns, as you'll see below.

##### Selecting by index

These examples show how to select using predicates. You can also use a selection to retrieve the value at a specific
index, or indexes. Both of the following are supported:

```java
@@snip [selection_by_index](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

If you have several columns of the same length as you would in a table of data, you can make a selection with one column
and use it to filter another:

```java
@@snip [selection_other_col](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

> **Key point:** Note the methods *startsWith(aString)*, *isLessThan(aNumber)*, and *isPositive()*. These were predefined
>for your use. There are many such methods that can be used in building queries. For StringColumn, they're defined in the
>[tech.tablesaw.columns.strings.StringFilters interface](http://www.javadoc.io/page/tech.tablesaw/tablesaw-core/latest/tech/tablesaw/columns/strings/StringFilters.html).
>It also includes *endsWith()*, *isEmpty()*, *isAlpha()*, *containsString()*[^1], etc. Each column has a similar set of
>filter operations. They can all be found in the filter interfaces located in sub-folders of tech.tablesaw.columns
> (e.g. tech.tablesaw.columns.dates.DateFilters).

#### Map functions

Map functions are methods defined on columns that return new Columns as their result. You've already seen one:
The column *multiply(aNumber)* method above is a map function with a scalar argument. To multiple the values in two
columns, use *multiply(aNumberColumn)*:


```java
DoubleColumn newColumn = nc1.multiply(nc2);
```

```java
@@snip [multiply_two_columns](./src/main/java/tech/tablesaw/docs/GettingStarted.java)

@@snip [multiply_two_columns](./output/tech/tablesaw/docs/GettingStarted.txt)
```

Each value in column nc1 is multiplied by the corresponding value in nc2, rather than by a scalar value in the earlier example.

There are many map functions built-in for the various column types. Here are some examples for StringColumn:

```java
@@snip [string_map_examples](./src/main/java/tech/tablesaw/docs/GettingStarted.java)

```

As you can see, for many String methods that return a new String. StringColumn provides an equivalent map method that
returns a new StringColumn. It also includes other helpful methods found in Guava's String library and in the
Apache Commons String library.

> **Key point:** Every column type has a set of map operations like *multiply(aNumber)*. For StringColumn,
>these methods are defined in the [*tech.tablesaw.columns.strings.StringMapFunctions*](http://www.javadoc.io/page/tech.tablesaw/tablesaw-core/latest/tech/tablesaw/columns/strings/StringFilters.html) interface. It includes many methods beyond those shown above. Methods for all column types can all be found in their filter interfaces located in the sub-folders of tech.tablesaw.columns (e.g. [*tech.tablesaw.columns.dates.DateMapFunctions*](http://www.javadoc.io/page/tech.tablesaw/tablesaw-core/latest/tech/tablesaw/columns/strings/StringFilters.html), which provides date methods like *plusDays(anInt)*, *year()*, and *month()*).
>

#### Reduce (aggregate) functions: Summarizing a column 

Sometimes you want to derive a singly value that summarizes in some sense the data in a column. Aggregate functions do
just that. Each such function scan all the values in a column and returns a single scalar value as a result. 
All columns support some aggregate functions: *min*() and *max*(), for example, plus *count()*, *countUnique()*, and *countMissing()*.
Some also support type-specific functions. BooleanColumn, for example, supports *all()*, which returns *true* if all
of the values in the column are *true*. The functions *any()*, and *none()*,  return true if any or none the values in
the column are true, respectively. The functions *countTrue()*, and *countFalse()* are also available.

NumberColumn has many more aggregate functions. For example, to calculate the standard deviation of the values in a column, you would call:

```java
@@snip [stdev](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

> **Key point:** NumberColumn supports many aggregation functions, including many of the most useful.
>Among those available are *sum*, *count*, *mean*, *median*, *percentile(n)*, *range*, *variance*, *sumOfLogs*, and so on.
>These are defined in the [NumericColumn](http://www.javadoc.io/page/tech.tablesaw/tablesaw-core/latest/tech/tablesaw/api/NumericColumn.html) class. 
>

When we discuss tables below, we'll show how to calculate sub-totals in one or more numeric columns by the values in one or more grouping columns.

### Tables
A table is a named collection of columns. All columns in the table must have the same number of elements, although
missing values are allowed. A table can contain any combination of column types.

#### Creating Tables

You can create a table in code. Here we create a table and add two new columns to it:

```java
@@snip [tables](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

#### Importing data

More frequently, you will load a table from a CSV or other delimited text file. 

```java
@@snip [read_bush_table](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

Tablesaw does a pretty good job at guessing the column types for many data sets, but you can specify them
if it guesses wrong, or to improve performance. Numerous other options are available, such as specifying whether or
not there's a header, using a non-standard delimiter, supplying a custom missing value indicator, and so on. 

***Note:*** Getting data loaded is sometimes the hardest part of data analysis. Advanced options for loading data are
described in the documentation on [Importing Data](https://jtablesaw.github.io/tablesaw/userguide/importing_data).
That section also shows how you can read data from a database, a stream, or an HTML table. The stream interfaces
lets you read data from a Web site or an S3 bucket.  

#### Exploring Tables

Because Tablesaw excels at manipulating tables, we use them whenever we can.  When you ask tablesaw for the structure
of a table, the answer comes back in the form of another table where one column contains the column names, etc. 
The methods ` structure()`, `shape()`, `first(n)`, and `last(n)` can help you get to know a new data set. Here are some examples.

```java
@@snip [table_structure](./src/main/java/tech/tablesaw/docs/GettingStarted.java)

@@snip [table_structure](./output/tech/tablesaw/docs/GettingStarted.txt)
```

```java
@@snip [table_shape](./src/main/java/tech/tablesaw/docs/GettingStarted.java)

@@snip [table_shape](./output/tech/tablesaw/docs/GettingStarted.txt)
```

```java
@@snip [first_three](./src/main/java/tech/tablesaw/docs/GettingStarted.java)

@@snip [first_three](./output/tech/tablesaw/docs/GettingStarted.txt)
```

```java
@@snip [last_three](./src/main/java/tech/tablesaw/docs/GettingStarted.java)

@@snip [last_three](./output/tech/tablesaw/docs/GettingStarted.txt)
```

Table's *toString()* method returns a String representation like those shown above. It returns a limited number of
rows by default, but you can also use *table.printAll()*, or *table.print(n)* to get the output you want.

Of course, this is just the beginning of exploratory data analysis. You can also use numeric and visual tools to
explore your data. These facilities are described in the documentation on statistics and
[plotting](userguide/Introduction_to_plotting.md), respectively.

#### Working with a table's columns

Often you'll work with specific columns in a table. Here are some useful methods:

```java
@@snip [working_with_columns_examples](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```
In tablesaw, column names are case-insensitive. You get the same column if you ask for any of these:

```java
@@snip [get_column_case_insensitive](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

remembering column names is enough of a burden without having to remember exactly which characters are capitalized. 

##### Getting specific column types from a table

Columns can be retrieved from tables by name or position. The simplest method *column()* returns a object of type Column.
This may be good enough, but often you want to get a column of a specific type. For example, you would need to cast the
value returned to a NumberColumn to use its values in a scatter plot. 

```java
@@snip [get_column](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

When a variable type is "Column" it only provides methods that are available on *all* columns. You can't perform math
or do a string replace directly on a Column type. If you need a StringColumn you could cast the column, for example: 

```java
@@snip [get_column_cast](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

Table also supports methods that return columns of the desired type directly:

```java
@@snip [get_typed_columns](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

> **Key point:** You may want a specific kind of column to work with. Either use the standard *column()* method and
>cast the result or use one of the type specific methods (like *numberColumn()*) that handle the cast for you.
>There are also methods or getting columns of a specific type. 

####  Working with rows

As with columns, many options exist for working with tables in row-wise fashion. Here are some useful ones:

```java
@@snip [row_wise_examples](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

You can also perform arbitrary operations on each row in the table.  One way is to just iterate over the rows and
work with each column individually.

```java
@@snip [for_loop](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

Another approach lets you skip the iteration and just provide a Consumer for each row.

```java
@@snip [stream](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

If you need to process more than one row at a time, there are several methods to help. 

```java
@@snip [stepping_rolling_stream](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

See [Rows](https://jtablesaw.github.io/tablesaw/userguide/rows) for more information and other options. 

#### Sorting

To sort a table, you can just use the `sortOn()` method and give it one or more column name:

```java
@@snip [sort](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

See [Sorting](https://jtablesaw.github.io/tablesaw/userguide/sorting) for more information and other options. 

#### Filtering

Query filters can be combined using the logical operations *and*, *or*, and *not*.
These are implemented on the `QuerySupport` class. 

```java
@@snip [static_boolean_operators_import](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

Each method accepts a function with the following signature `Function<Table, Selection>`. Lambadas work nicely.
```java
@@snip [filtering](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

#### Summarizing

```java
@@snip [static_aggregate_function_import](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

The usual way to calculate values is  to use the *summarize()* method: 

```java
@@snip [summarize_basic](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

It's important to recognize, that the column need not exist when summarize is invoked. Any map function can be used
in the *by()* statement to group on calculated values. A common use case is in handling dates. You can summarize sales
by day-of-week, as follows:

```java
@@snip [summarize_calculated_column](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

which says "return the mean and median sales by day of week."

> **Key point**: Tables are usually split based on columns, but the columns can be calculated on the fly

See the documentation on [Summarizing](https://jtablesaw.github.io/tablesaw/userguide/reducing) data, and the classes in
the [aggregate package](http://www.javadoc.io/page/tech.tablesaw/tablesaw-core/latest/tech/tablesaw/aggregate/package-summary.html)
for more detail.

##### Cross-Tabulations (AKA contingency tables)

If you're only interested in how the frequently observations appear in different categories, you can use cross-tabulations.
In the example below we show the table percents, but you can also get row and column percents and raw counts.

```java
@@snip [crosstab](./src/main/java/tech/tablesaw/docs/GettingStarted.java)
```

The formatted output is shown below.

```java
@@snip [crosstab](./output/tech/tablesaw/docs/GettingStarted.txt)
```

See the section on [Cross Tabs](https://jtablesaw.github.io/tablesaw/userguide/crosstabs), and the JavaDocs for the
[CrossTab](http://www.javadoc.io/page/tech.tablesaw/tablesaw-core/latest/tech/tablesaw/aggregate/CrossTab.html) class. 

## Conclusion

We've covered a lot of ground. To learn more, please take a look at the
[User Guide](https://jtablesaw.github.io/tablesaw/userguide/toc) or API documentation
([Java Docs](http://www.javadoc.io/page/tech.tablesaw/tablesaw-core/latest/index.html)).

[^1]: Note that containsString(String subString) is different from contains(). The first method looks at each
string in the column to see if it conains the substring. The second method looks at every row in the column and returns
true if any matches the entire string. In other words, contains is like contains as defined on List<String>. , etc.
