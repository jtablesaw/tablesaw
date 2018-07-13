# Filters

## where()

Filters select a subset of the rows in a table. Given a filter, a table will (usually) return a table like itself, but having only the rows that pass the filter criteria.

The main methods for applying filters are:

```java
table.where(aSelection)
```

which includes all rows for which the filter returns true. And

```java
table.dropWhere(aSelection)
```
which excludes all rows for which the filter returns true. 

Before digging too deep into table filters, though, we should look *Selections*, and at the column filters table filters build on.

## Selections

Both columns and tables are filtered using *selections*. A selection is a bitmap (like a boolean array) with an entry for each element in the column. For any given element, if the corresponding bitmap entry is "true", the element is included in the result.  

Often there's a method on the column that provides the selection you want. StringColumn, for example,  has a method called *startsWith(aString)*, which returns a selection, while DateColumn supports *inJanuary()*. 

Selections are readily combined, using *and()*, *or()*, and *andNot()*. 

## Column Filters 

As mentioned, columns have many methods that return selections. The method *isInJanuary()* is implemented on both DateColumn and DateTimeColumn, and works as follows:

```java
Selection selection = dateColumn.isInJanuary();
DateColumn januaries = dateColumn.where(selection);
```

Since it's often the filtered column you want, you may inline the call:

```java
DateColumn januaries = dateColumn.where(dateColumn.isInJanuary());
```

This begs the question, why not just have isInJanuary() return a filtered column?  There are three reasons. The first is that selections are easier to combine.  You can, for example, get only the dates from January that  were also on Monday.  

```Java
dateColumn.where(dateColumn.isInJanuary().and(dateColumn.isMonday()))
```

or, all the dates in January, and all the Mondays:

```Java
dateColumn.where(dateColumn.isInJanuary().or(dateColumn.isMonday()))
```

or, all the dates in January that were not Mondays:

```java
dateColumn.where(dateColumn.isInJanuary().or(dateColumn.isMonday()))
```

The second reason for returning selections is that the column methods that return selections can be used to filter tables. Given a Table t, with a StringColumn sc, you can filter the table using the column method as shown below:

```java
Table t1 = t.where(sc.startsWith("Foo"));
```

In fact, the general approach to filtering table rests on column filters, using the logical operators *and()*, *or()*, and *andNot()* to combine them into complex, multi-column queries:

Table t1 = t.where();

Finally, you can combine these "where clauses" with methods that filter by index. For example:

```java
Table t1 = t.where(Selection.withRange(100, 300).and(sc.startsWith("Foo")));
```

first selects the rows in the range 100 to 300, and then intersects that result with the query `sc.startsWIth("Foo")`.

Writing your own Column Filters



## ColumnReferences and method chaining 

The usual way to create a filter is to use a ColumnReference. A ColumnReference refers to a column in the target table, and implements a large number of built-in filters. To create one, you will generally do a static import of the method QueryHelper.column. The process is shown below:

```java
import static com.github.lwhite1.Tablesaw.api.QueryHelper.column;
    
ColumnReference statusRef = column("Status");
Table filtered = aTable.selectWhere(statusRef.isEqualTo("Ok"));
```

In general, though, you’ll create the reference directly in the where() call

```java
Table filtered = aTable.where(stringColumn("Status").isEqualTo("Ok"));
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
    public Selection apply(Table table) { 
        DateColumn dateColumn = 
            (DateColumn) table.column(columnReference().getColumnName());
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