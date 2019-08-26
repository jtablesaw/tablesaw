[Contents](https://jtablesaw.github.io/tablesaw/userguide/toc)

# Table processing without loops

Tablesaw has long supported column-wise operations that allow you process all the data in a column in a single operation, without an explicit loop. For example, they support 

mapping: 

```java
StringColumn sc = myStringColumn.concat("foo");
```

reducing:

```
double mean = numberColumn.mean();
```

and filtering

```java
NumberColumn filtered = numberColumn.where(numberColumn.isLessThan(4);
```

in this way. If you don't find in the library a function that does exactly what you want, you can write one and use it as if it were built in. 

With tables, the most common use of the analogous "table-wise" operations is for SQL-like operations. However, tables also support arbitrary operations on individual rows and on collections of contiguous rows. 


## Performing arbitrary operations on Rows


## Window functions on Tables

While do() applies an operation to every row in the table individually, it is often useful to work with sets of contiguous rows: Calculating the difference in values between pairs of rows is a common example. These type of operations are called window operations. The size of the window refers to the number of rows it considers in evaluating the operation. Tablesaw supports two approaches, one for rolling windows, and one for stepping windows.

### Rolling windows

Rolling window operations move through the table one row at a time. For example, a rolling window with size of two, first looks at row 0 and row 1. Then it evaluates rows 1 and 2, and then rows 2 and 3. Any given row may be considered n times, where n is the size of the window.

### Stepping windows

A stepping window moves in steps. Each row is evaluated only once. A stepping window of size 2 first looks at rows 0 and 1, then rows 2 and 3, then rows 4 and 5. 

#### Working with Pairs 

Because working with Pairs of rows is common, it gets a bit of extra support that makes it a little easier to write these kind of operations.

```

```



Multi-row operations

 



