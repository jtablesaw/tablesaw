[Contents](https://jtablesaw.github.io/tablesaw/userguide/toc)

# Filters

## where()

Filters select a subset of the rows in a table. Given a filter, a table will (usually) return a table like itself, but having only the rows that pass the filter criteria.

The main methods for applying filters are:

```java
Table t = table.where(aSelection);
Column x = column.where(aSelection);
```

which includes all rows for which the filter returns true. And

```java
table.dropWhere(aSelection)
```
which excludes all rows for which the filter returns true. 

As you can see, for any given selection *dropWhere()* returns the complement of the records  returned by *where()*.  

There are several other methods like where to explore. Before digging into table filters, though, we should look *Selections*, and at the column filters table filters build on.

**Key point**: One way that where() in tablesaw differs from the where clause in SQL, is that Tablesaw always returns records in the order they appear in the original column or table. This can be used to good advantage when working with time-series data.

## Selections

Both columns and tables are filtered using *selections*. A selection is a logical bitmap (like a boolean array) with an entry for each element in the column. For any given element, if the corresponding bitmap entry is "true", the element is included in the result.  

Here's what you need to know about selections.

1. Selections are like bitmaps, in which 'true' means include 'include the corresponding value(s)' in the result.
2. They're usually applied to columns, but are used to filter tables as well
3. Columns have many built-in selection methods
4. Any operation that returns an appropriately-sized bitmap can be used
5. You can write your own selection methods
6. Selections are readily combined, using their *and()*, *or()*, and *andNot()*. 

Lets take a look at each of these.

#### Applying Selections to columns

Imagine a *student* table with a column named "birth date" and that we want to find all the birth dates in the year 2011. It turns out that this is easy to do, because there is a built in method (*isInYear(anInt)*) for that.

```Jave
DateColumn bd = student.dateColumn("birth date");
Selection bdYear = bd.isInYear(2011);
```

Lets say that column bd contains these values:

```
Jan 22, 2011
April 14, 2010
March 9, 2011
August 4, 2010
```

The selection bdYear above contains 1, 0, 1, 0, since the first and third birth dates in the column are in year 2011 and the others are not. 

To return a DateColumn containing birth dates in 2011, we could apply the selection to bd using the *where()* method, and passing the selection *bdYear* as an argument.

```Java
DateColumn bd2011 = bd.where(bdYear);
```

Generally, a filtered column, rather than a selection, is the result you actually want. You can, of course, inline the call:

```java
DateColumn bd2011 = bd.where(bd.isInYear(2011));
```

This begs the question, why not just have isInJanuary() return a filtered column?  There are several reasons. The next section covers the first.

#### Selections are used to filter tables as well as columns

Because DateColumn method *isInYear(anInt)* return a Selection, rather than a new column, we can use the same method to filter either the column itself or the table that contains the column. 

To filter the entire student table we simply apply the same selection to the table, again using the method where(aSelection).

```java
Table studentsBornIn2011 = students.where(bdYear)
```

The only constraint is that the column used to create the Selection and the table where it's applied must have the same number of rows. 

#### Columns have many built-in selection methods

Much of Tablesaw's power comes from the number and range of filters built into the library.  When you need one, there's often a method that does what you want. StringColumn, for example,  has the methods *startsWith(aString)* and *endsWith(aString)*, both returning a Selection.  

```java    filtered1 = unfiltered.selectif(column("name").contains("charles"));
StringColumn email = unfiltered.stringColumn("email");
filtered = unfiltered.where(email.endsWith("google.com"));
```

StringColumn has other methds as well, while both DateColumn and DateTimeColumn support *isInJanuary()*. It works as follows:

```java
DateColumn januaries = dateColumn.where(dateColumn.isInJanuary());
```

In fact, the general approach to filtering table rests on column filters, using the logical operators *and()*, *or()*, and *andNot()* to combine them into complex, multi-column queries. This is illustrated below.

#### (4) Selections are readily combined, using *and()*, *or()*, and *andNot()*. 

Selections are easy to combine to create more complex selection.  You can, for example, get only the birth dates from January that were also on Monday.  

```java
bd.isInJanuary().and(bd.isMonday())
```

or, all the birth dates in January, and all the Mondays:

```java
bd.where(bd.isInJanuary().or(bd.isMonday()))
```

or, all the dates in January that were not Mondays:

```java
bd.where(bd.isInJanuary().andNot(bd.isMonday()))
```

A list of the built-in filters is below.

Finally, you can combine these "where clauses" with methods that filter by index. For example:

```java
Table t1 = t.where(Selection.withRange(100, 300).and(sc.startsWith("Foo")));
```

first selects the rows in the range 100 to 300, and then intersects that result with the query `sc.startsWIth("Foo")`.

## Writing your own filter methods

To write a custom filter method for a column, you first create a predicate, and then pass it to an eval() method on your column. Here's an example with NumberColumns. 

```java
public abstract Selection apply(Table relation);
```
Here’s an example. We write a filter that only selects prime numbers:

```java
// first we create a predicate 
    DoublePredicate isPrime = new DoublePredicate() {

        @Override
        public boolean test(double value) {
            // is it's not an int return false
            if (!((value == Math.floor(value)) && !Double.isInfinite(value))) {
                return false;
            }
            int n = (int) value;

            if (n < 2 || n % 2 == 0)
                return false;
            // only odd factors need to be tested up to n^0.5
            for (int i = 3; i * i <= value; i += 2) {
                if (value % i == 0)
                    return false;
            }
            return true;
        }
    };
// then use eval to return a selection

```

## Combining filters

You can combine filters to query a table on the values in multiple columns.

```java
 Table filtered = aTable.where(
            aTable.stringColumn("Status").isEqualTo("Ok")
     			.and(aTable.numberColumn("Age").isGreaterThan(21)));
```

### Filter by index

You can select rows by specifying the index (zero-based):

```java
t.rows(i...)
```

You can also select by range:

```java
t.inRange(start, end)
```

You can also select a random sample of data. See the section on Sampling for more detail.

### Sampling

The line below returns a table containing 50 randomly sampled rows from table t.

```java
Table sample = t.sampleN(50); 
```

Alternately, you can specify the sample size as a proportion of the table size using sampleX(aDouble):

```java
Table sample = t.sampleX(.40);
```

You can also divide the table in two, assigning rows randomly to each, and return both sub-tables in an array. The code below puts ~ 1/3 of the rows in the results[0], and the other 2/3rds in results[1].  This is handy for separating data into a training and test subsets  for machine learning applications. 

```java
Table[] results = Table.sampleSplit(.333);
```



## Excluding some columns from the result

You may want to exclude some of the columns in the original from the new table. To do this, you could simply execute the queries as above, and then eliminate columns from the new table as a separate step:

```java
filtered = aTable.where(aTable.stringColumn("Status").isEqualTo("Ok"));
filtered = filtered.removeColumns("startDate", "value");
```

Alternately, you could specify the desired subset of columns as part of the query:

```java
Table filtered = aTable.select("name","status")
    .where(aTable.stringColumn("Status").isEqualTo("Ok"));
```

Given a list of columns as arguments, the *select()* statement returns a table containing only those columns. By chaning *select()* and *where()*, you get something that looks a lot like a sql statement that returns a subset of the data in the original table. 

## Current list of provided column filters 

All the methods below return a Selection.

#### General Filters (apply to all types)

```
isEqualTo(Comparable c)
greaterThan(Comparable c)
greaterThanOrEqualTo(Comparable c)
lessThan(Comparable c)
lessThanOrEqualTo(Comparable c)
between(Comparable a, Comparable b)
isIn(List aList)
isMissing()
isNotMissing()

```
#### String Filters

```
equalToIgnoringCase(String string)
startsWith(String string)
endsWith(String string)
contains(String string)
matchesRegex(String string)
isEmpty(String string)
isAlpha()
isNumeric()
isAlphaNumeric()
isUpperCase()
isLowerCase()
hasLengthEqualTo(int lengthChars)
hasLengthLessThan(int lengthChars)
hasLengthGreaterThan(int lengthChars)
```

#### Number Filters
```
isPositive()
isNegative()
isNonNegative()
isZero()
isEven()
isOdd()
isCloseTo(float target);
isCloseTo(double target)
```

#### Date Filters
```
equalTo(LocalDate date)
before(LocalDate date)
after(LocalDate date)
inYear(int fourDigitYear)
inQ1()
inQ2()
inQ3()
inQ4()
inJanuary(), inFebruary(), …, inDecember()
sunday(), monday(), …, saturday()
firstDayOfMonth()
lastDayOfMonth()
```

#### Time Filters
```
midnight()
AM()
PM()
```

#### DateTime Filters

All of the filters provided for Dates and Times are available for DateTimeColumns.

#### Boolean (column) filters
```
isTrue()
isFfalse()
```