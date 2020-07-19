[Contents](https://jtablesaw.github.io/tablesaw/userguide/toc)

Columns
=======

Tablesaw is all about tables and tables are made of columns. You'll often need to work with individual columns and Tablesaw provides a large collection of tools for that. We'll cover the basics here. 

Let's start with a definition. A column is a named vector of data, all of a single type. Some elements may be missing, and it's important to deal with those. We cover that later. 

Here are the supported column types. All of the concrete column types are in the api package. For the details on each kind see the appropriate Javadoc files. 

* [BooleanColumn](http://www.javadoc.io/page/tech.tablesaw/tablesaw-core/latest/tech/tablesaw/api/BooleanColumn.html), which holds true and false values
* [StringColumn](http://www.javadoc.io/page/tech.tablesaw/tablesaw-core/latest/tech/tablesaw/api/StringColumn.html), as in "Hello, World," or "RN183-15F", "charlie@gmail.com";
* [NumberColumn](http://www.javadoc.io/page/tech.tablesaw/tablesaw-core/latest/tech/tablesaw/api/NumberColumn.html): an interface for numeric data types.   
* [DateColumn](http://www.javadoc.io/page/tech.tablesaw/tablesaw-core/latest/tech/tablesaw/api/DateColumn.html): A "local date". That is, a date without a timezone. April 10, 2018, for example.
* [DateTimeColumn](http://www.javadoc.io/page/tech.tablesaw/tablesaw-core/latest/tech/tablesaw/api/DateTimeColumn.html): A local date and time combined. April 10, 2018 at 9:07.
* [TimeColumn](http://www.javadoc.io/page/tech.tablesaw/tablesaw-core/latest/tech/tablesaw/api/TimeColumn.html): A local time, like 12:47:03

There is currently one concrete type of NumberColumn, called [DoubleColumn](http://www.javadoc.io/page/tech.tablesaw/tablesaw-core/latest/tech/tablesaw/api/DoubleColumn.html). As you'd expect, it holds 8-byte floating point numbers, but is used also for integer types. 

We'll begin by looking at the operations that are common to all column types. 

#### Create a Column

Columns are usually created by importing a data file. They can also be instantiated by calling one of the static create() methods defined on the appropriate class. For example, you can create an empty DateColumn as follows:

```Java
DateColumn column = DateColumn.create("test");
```

The new column has the name "test", and a ColumnType of LOCAL_DATE. Names are important. We often ask a table for a column by name. All the columns within a given table must have unique names. You can always get the name of a column by calling *name()*, and its type by calling *type()*.

To create a column with data, you can initialize the column with an array:

```Java
double[] values = {1, 2, 3, 7, 9.44242, 11};
DoubleColumn column = DoubleColumn.create("my numbers", values);
```

Once you have a column, you can add it to a table using the addColumns() method on Table.

```Java
table.addColumns(column);
```

#### Adding, editing, and removing data

You can add data to columns as shown below, but  if your column is part of a table, you must take care to ensure that each column has the same number of elements.

```Java
DateColumn.append(LocalDate.of(2016, 2, 28));
```

To change the value of an element in a column you can use the *set(index, value)* method. This will replace the existing value at the given position with the new value.

```Java
doubleColumn.set(4, 123.2);
```

Normally, you don't remove data from a column in the normal sense. To remove elements from the middle of column would cause problems if the column is part of a table. However, if you do want to get rid of some elements you have two choices. The first is to set the value to missing as shown below.

```Java
doubleColumn.setMissing(4);
```

Your other option is to create a new column without the offending data elements. This is done with filters as described below.

### Other common operations:

Columns do all the things you expect, here’s an incomplete list of standard operations:

```Java
name()                  // returns the name of the column
type()                  // returns the ColumnType, e.g. LOCAL_DATE
size()                  // returns the number of elements
isEmpty()               // returns true if column has no data; false otherwise
first(n) and last(n)    // returns the first and last n elements
max() and min()         // returns the largest and smallest elements
top(n) and bottom(n)    // returns the n largest and smallest elements
print()                 // returns a String representation of the column
copy()					// returns a deep copy of the column
emptyCopy()				// returns a column of the same type and name, but no data
unique()				// returns a column of only the unique values
countUnique()			// returns the number of unique values
asSet()                 // returns the unique values as a java Set
summary()				// returns a type specific summary of the data
void sortAscending()	// sorts the column in ascending order 
void sortDescending()	// sorts the column in ascending order 
append(value)    		// appends a single value to the column
appendCell(string) 		// converts the string to the correct type and appends the result    
append(otherColumn)     // Appends the data in other column to this one
removeMissing()			// returns a column with all missing values removed    
```

These operations are available on nearly all column types. Each operates on an entire column. 

To operate on the values of a column, you have two choices. You can work with individual values, or use column-wise operations to work with all the values in a column in the same way. To work with individual values, you can just iterate over the column:

```Java
DateColumn weekLater = DateColumn.create("Week Later");
for (LocalDate date: dates) {
   weekLater.append(date.plusDays(7));
}
```

Just about anything you can do with an individual LocalDate you can do with an entire DateColumn, using column-wise operations. For example, the above loop could be written as:

```Java
DateColumn weekLater = dates.plusDays(7);
```

This is an example of a mapping function. You can find the date mapping functions in the interface [DateMapFunctions](https://www.javadoc.io/doc/tech.tablesaw/tablesaw-core/latest/tech/tablesaw/columns/dates/DateMapFunctions.html). Many of the methods there deal with adding and subtracting units of time (days, weeks, months, etc), and calculating the column-wise differences between two date columns. Others provide access to elements of a date. The method *month()*, for example, returns a StringColumn containing the month for a given date. The methods *year()*, *dayOfWeek()*, *dayOfMonth()*, etc. function similarly.

Other columns have similar mapping functions. 

### Filtering

You can filter two ways. The first is with the built-in predicates, like IsMonday(). See the end of this post for a full list of the built-in predicates for LocalDateColumn.

#### Writing Predicates for filtering columns

You can write a Predicate class to filter a date column using  ```where(Predicate<LocalDate>)```.  For example, if you want all the leap days in a column, you could create this Java 8 predicate.

```Java
LocalDatePredicate leapDays = new Predicate<LocalDate>() {
  int dayOfMonth = 29;
  int monthValue = 2;
  @Override
  public boolean test(LocalDate i) {
    return i.getDayOfMonth() == dayOfMonth && i.getMonthValue() = 2;
  }
};
```

which you can use as:

    DateColumn filtered = dates.where(dates.eval(leapDays);

In the line above, the call to *dates.eval(aPredicate)* returns a Selection object holding the position of every element in the column that passes the predicate's *test()* method. The surrounding call to *where(aSelection)*, applies that selection to the column and returns a new column with all the passing values. 

#### Built-in Date Predicates

There are numerous built-in date predicates. For example:

```Java
DateColumn filtered = dates.isMonday();
DateColumn filtered = dates.isInQ2();
DateColumn filtered = dates.isLastDayOfTheMonth();
```

Perhaps not surprisingly, there are a number that find specific dates or date ranges: 

```java
LocalDate date1 = LocalDate.of(2016, 2, 20);
LocalDate date2 = LocalDate.of(2016, 4, 29);
DateColumn filtered = dates.isEqualTo(date1);
DateColumn filtered = dates.isAfter(date1);
DateColumn filtered = dates.isOnOrAfter(date1);
DateColumn filtered = dates.isBetweenIncluding(date1, date2);
```

The built-in method in this case is preferable as it has been optimized. But you *can* write your own if you need something not already provided.

You can find a full list in the JavaDoc for [DateColumn](http://www.javadoc.io/page/tech.tablesaw/tablesaw-core/latest/tech/tablesaw/api/DateColumn.html).

#### Using filters to conditionally edit data

The section on editing values above assumes you've identified the specific values you want to change. Often with large datasets, you know you want to change some values, without knowing where they are, or even how many are in the dataset. The easiest way to perform a bulk update of values meeting some condition is with `set(aSelection, aNewValue)`. Each column implements an appropriate variation of this method. DoubleColumn, for example, has a version that takes a double as the second argument, and StringColumn has a version that takes a string. 

You can use a built-in filter method like those discussed above to provide the selection. Here's one example:

```java
doubleColumn.set(doubleColumn.isGreaterThan(100), 100);
```

This would set any value above 100 to equal 100 exactly. This approach can be very helpful for dealing with missing data, which you might want to set to an average value for example. 

```java
double avg = doubleColumn.mean();
doubleColumn.set(doubleColumn.isMissing(), avg)
```

NOTE: When working with missing values, always test with the isMissing() method, rather than test using the column type's MISSING_VALUE constant. For doubles, MISSING_VALUE returns Double.NaN, and since Double.NaN does not equal Double.NaN, a test like `doubleValue == MISSING_VALUE` will fail to detect missing values.

### Formatting data 

You can print data as individual values, columns or tables. The output format can be controlled by setting a type-specific formatter on a column. For example, to change how numbers are displayed you can call setPrintFormatter() on a NumberColumn, passing in a NumberColumnFormatter. Each formatter serves two functions, displaying true values and handling of  missing ones. NumberColumnFormatter has several pre-configured options, including printing as currency or percents.


See the [Table](https://jtablesaw.github.io/tablesaw/userguide/tables) documentation for how to add and remove columns

 