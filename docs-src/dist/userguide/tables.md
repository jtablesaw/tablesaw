[Contents](https://jtablesaw.github.io/tablesaw/userguide/toc)

Tables
======

Tables are the primary data-type and general focus of Tablesaw. Here we’ll provide an overview of the operations they provide. Coverage of most of the topics below is introductory. They often will have their own section of the User Guide where they are examined more fully. For still more detail, see the JavaDoc for tech.tablesaw.api.Table.

## Tables "all the way down"
Tablesaw has a huge number of methods for creating, querying, manipulating, displaying, and saving tables, so it makes sense that we use tables widely, and that many operations on tables return other tables. For example, when you ask a table to describe its structure, it returns a new table that contains the column names, types, and order. 

## Creating tables

You can create tables programmatically or by loading data from an external source.

### Create programmatically

```java
Table t = Table.create("name")
```

It's often convenient to add columns while you're creating the table. 

```java
Table t = Table.create("name", column1, column2, column3...)
```

You can also add columns later.

### Import data

Tablesaw can load data from character delimited text files (including CSV and Tab-separated files), from streams, and from any data source that can create a JDBC result set. As this includes essentially all relational databases (and many non-relational ones), most of the world’s structured data in can be loaded without a prior transformation. 

#### Import from a CSV file

You can load a table from a CSV file by providing the file name. 

    Table t = Table.read().csv("myFile.csv");

This simple method supplies default values for a number of parameters like the type of the separator character (a comma). It also attempts to infer the types for each column. If the inferred types are incorrect, you can specify the types at import time. See [Importing data](https://jtablesaw.github.io/tablesaw/userguide/importing_data) for other options and more detail.    

## Displaying data

The simplest way to display a table is to call "print()" on it, which return a formatted String representation.

    aTable.print();

The default implementation of print displays the first ten and last ten records. To specifically control the output, the methods first(n) and last(n) are available. These return a copy of the table that contains only the first n or last n rows respectively.

    aTable.first(3);
    aTable.last(4); 

Table overides toString() to return print(). This makes for rather funky output in a debugger, but during analysis, you frequently want to look at the table data so frequently that the shortcut is worth the hassle it causes people programming Tablesaw.

## Getting table metadata

There are a number of ways to get familiar with a new dataset. Here are some of the most useful.

*table.name()* returns its name, which defaults to the name of the file it was created from. You can change it if you like using *setName(aString).*

*t.columnNames()* returns an array of column-name strings.

*t.structure()* returns a list of columns with their position and types:

    Structure of data/tornadoes_1950-2014.csv
        Index Column Names Column Type 
        0     The Date     LOCAL_DATE  
        1     The Time     LOCAL_TIME  
        2     State        CATEGORY    
        3     State No     INTEGER     
        4     Scale        INTEGER 

table.shape() returns the table’s size in rows and columns:

    59945 rows X 10 cols

You can also get the *rowCount()* and *columnCount()* individually from a table.

## Add and remove columns

You can add one or more columns to a Table using the *addColumns()* method:

```java
t.addColumns(aColumn...)
```

You can also specify that the column be inserted at a particular place by providing an index:

```java
t.addColumn(3, aColumn);
```

As usual in java, column numbering begins at 0, rather than 1.

The column you add must either be empty or have the same number of elements as the other columns in the table.

To remove a column or columns:

```java
t.removeColumns(aColumn...)
t.removeColumns("columnName"...)
```

Columns can also be removed by referencing them by name. Alternately, you can specify just the columns to retain:

```java
t.retainColumns(aColumn);
```

Again you can specify the columns either directly, or by using their names.

While *addColumns()* and *removeColumns()* update the receiver in place, you can also create a new table with a subset of the columns in the receiver. This can be done by specifying the names of the columns that you want to retain, in the order you want them to appear.

```java
Table reduced = t.select("Name", "Age", "Height", "Weight");
```

You can also create a new table by specifying the columns in the current table that you don’t want, which might save some typing:

```java
Table reduced = t.rejectColumns("Street Address");
```

In this case the columns in the result table are in the same order as in the original.

## Selecting columns

Often you will want a reference to a column in the table. To get all the columns as a list:

```java
t.columns();
```

Columns can also be selected by index or name:

```java
t.columns("column1", "column2");
```

Often you want just one column, which you can get using *t.column(“columnName”)*.

Since Tablesaw columns are typed, you often need to cast the returned column to something more specific. For example:

```java
DoubleColumn dc = (NumberColumn) t.column();
```

as a convenience, tables have column accessors that are type specific: The do the casting for you.

```java
DoubleColumn dc = t.doubleColumn();
```

## Combining Tables

Tables can be combined in one of several ways.  The most basic is to append the rows of one table to another. This is only possible if the two tables have the same columns in the same order, but can be useful when, for example, you have the same data from two time periods.

```java
Table result = t.append(t2);
```

You can concatenate two tables, adding the columns of one to the other by using the *concat()* method.  The method returns the receiver rather than a new table. Two tables can be concatenated only if they have the same number of rows.

```java
t.concat(t2)
```

### Joining Tables

Tablesaw supports inner and outer joins between tables.



## Add and remove rows



## Filter

One of the most useful operations is filtering. Queries are created by forming expressions that produce a *Selection*, which effectively turns the query result into an object that can be used to filter by index. For example, the code below

```Java
Table result = t.where(t.stringColumn("Foo").startsWith("A"));
```

This would produce a table containing every row in t where the value in the column named "Foo" contains a string that starts with "A".

Filters are covered in detail in the section on [Filtering](https://jtablesaw.github.io/tablesaw/userguide/filters).   

## Reduce

There are numerous ways to summarize the data in a table. 

### Summarize

The summarize() method and its variants let you specify the columns to summarize.

```java
Table summary = 
    t.summarize("age", "weight", mean, median, range).apply();
```

Summarize returns a Summarizer object. 

The apply() method sent to summary above returns the result of applying the function to the table, and combining the results into a new table.  It computes one summary for the original table.

#### Groups

To calculate subtotals, we use *by()* instead of *apply().*

By takes a list of columns that are used to group the data. The example below calculates the average delay for each airport in the table. 

```java
Table result = t.summarize("delay", mean).by("airport");
```

### Cross Tabs 

Cross tabs (or cross-tabulations) are like groups, but return the data in a layout that faciliates interpretation. A cross tab in Tablesaw takes two grouping columns and returns the number of observations for each combination of the two columns. They can also produce the proportions, and subtotals by row or column. 

Cross Tabs are covered in detail in the section on [CrossTabs](https://jtablesaw.github.io/tablesaw/userguide/crosstabs). 

## Sort

Table can be sorted on any combination of columns, in any combination of ascending or descending order, or by supplying a comparator for complete flexibility. A simple example is shown below.

 ```java
t.sortDescending("column1","column2");
 ```

Sorting is covered in detail in the section on [Sorting](https://jtablesaw.github.io/tablesaw/userguide/sorting). 

## Rows

There are no real rows in Tablesaw. Data is organized in columns. The closest you get to an actual row is a table with one line. However, rows are useful abstractions in tabular data, so we provide a kind of virtual row that may be useful for table operations. 

### What we mean by a "virtual row"

A row in tablesaw is an iterable object that references a table and contains an index pointer. It lets you step through the table without copying any data or converting any data from its internal representation into something more familiar - unless you ask it to. This makes it possible work with a table a row or two at a time, without incurring any memory overhead, and with the minimal CPU use. 

Row handling is covered in detail in the section on [Rows](https://jtablesaw.github.io/tablesaw/userguide/rows), and in the section on [table processing without loops](https://jtablesaw.github.io/tablesaw/userguide/Table processing without loops). 

## Export

````java
table.write().csv("filename.csv");
````


