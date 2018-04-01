# Filters

Filters select a subset of the rows in a table. The Table class provides filter operations that return a Table like the original, but having only the rows that pass the filter criteria.

The main methods for applying filters are:

```java
Table t = table.selectWhere(aFilter);
```

which includes all rows for which the filter returns true. And

```java
Table t = table.rejectWhere(aFilter);
```
which excludes all rows for which the filter returns true. The syntax is designed to be "_fluent_"; to read as much like a natural language as possible.

The usual way to create a filter is to use a ColumnReference. A ColumnReference refers to a column in the target table, and implements a large number of built-in filters. To create one, you will generally do a static import of the method QueryHelper.column. The process is shown below:

```java
import static com.github.lwhite1.Tablesaw.api.QueryHelper.column;
    
ColumnReference statusRef = column("Status");
Table filtered = aTable.selectWhere(statusRef.isEqualTo("Ok"));
```

In general, though, you’ll create the reference directly in the selectWhere() call

```java
Table filtered = aTable.selectWhere(stringColumn("Status").isEqualTo("Ok"));
```

In the expression above, isEqualTo(“Ok”) is invoked on the new columnReference and returns a filter to be be used in the table method selectWhere(aFilter).

## Using built-in filters

One of the things that makes Tablesaw powerful is the number of filters that are built into the library.  There are filters for String values:

```java    filtered1 = unfiltered.selectIf(column("name").contains("Charles"));
filtered2 = unfiltered.selectIf(column("email").endsWith("google.com"));
```
There are filters for dates and times:
```java
filtered3 = unfiltered.selectIf(column("birthdate").isNot(inFebruary()));
filtered4 = unfiltered.selectIf(column("orderDate").isAfter(cutOffDate));
```
and so on. A list of the built-in filters is below.

## Writing your own filters

To write your own filter, you implement the Filter interface, which consists of a single method:

```java
public abstract Selection apply(Table relation);
```
Here’s an example:

```java
public class LocalDateIsAfter implements Filter {

    private LocalDate value; 
    private ColumnReference reference;

    public LocalDateIsAfter(ColumnReference reference, LocalDate value) { 
        this.reference = reference;
        this.value = value; 
    }

    @Override
    public RoaringBitmap apply(Table relation) { 
        DateColumn dateColumn = (DateColumn) relation.column(columnReference().getColumnName());
        return dateColumn.isAfter(value);
  }
}
```

## Combining filters

You can combine filters to query a table on the values in multiple columns.

```java
 Table filtered = aTable.selectWhere(
         both(
            column("Status").isEqualTo("Ok"),
            column("Age").isGreaterThan(21)));
```

## Excluding some columns from the result

You may want to exclude some of the columns in the original from the new table. To do this, you could simply execute the queries as above, and then eliminate columns from the new table as a separate step:

```java
filtered = aTable.selectWhere(column("Status").isEqualTo("Ok"));
filtered = filtered.removeColumns("startDate", "value");
```

Alternately, you could specify the desired subset of columns as part of the query:

```java
Table filtered = aTable.select("name", "status").where(column("Status").isEqualTo("Ok"));
```

Assuming the original table had four columns: name, startDate, value, and status, these two approaches would produce the same result.

Current list of filters provided

All the methods below return a boolean.

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
#### Logical and Compound Filters

```
is(Filter filter)
isNot(Filter filter)
anyOf(List filters)
allOf(List filters)
noneOf(List filters)
both(Filter a, Filter b)
either(Filter a, Filter b)
neither(Filter a, Filter b)
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
```
All of the filters provided for Dates and Times
```

#### Boolean (column) filters
```
isTrue()
isFfalse()
```