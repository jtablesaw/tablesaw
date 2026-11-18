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

### Rolling windows on tables

Rolling window operations move through the table one row at a time. For example, a rolling window with size of two, first looks at row 0 and row 1. Then it evaluates rows 1 and 2, and then rows 2 and 3. Any given row may be considered n times, where n is the size of the window.

### Rolling calculations on columns

Use `rolling(windowSize)` on a numeric column to calculate a trailing mean, sum, median, min, max, standard deviation, or another supported statistic. For example:

```java
DoubleColumn sales = DoubleColumn.create("sales", 10, 20, 30, 40, 50);
DoubleColumn mean3 = sales.rolling(3).mean();
Table table = Table.create("Sales", sales);
table.addColumns(mean3);
```

The result is a new column; the source values are unchanged. Each result belongs to the **last row of its window**, and includes that row. The window counts rows in their current order, not elapsed time. Sort the data into the desired order before calculating; windows do not automatically stop at group boundaries.

| Row (zero-based) | sales | mean3 | mean3.lag(1) |
| --- | --- | --- | --- |
| 0 | 10 | missing | missing |
| 1 | 20 | missing | missing |
| 2 | 30 | 20 | missing |
| 3 | 40 | 30 | 20 |
| 4 | 50 | 40 | 30 |

For a window of size `w`, row `i` summarizes rows `i - w + 1` through `i`. The first `w - 1` rows are missing because a complete window is not yet available. The result always has the same number of rows as the input: a window larger than the column produces an all-missing result, and an empty column produces an empty result. The window size must be positive.

Missing values inside a complete window are handled by the chosen aggregate function. For example, `mean()` ignores missing values, so a three-row window containing `10`, missing, and `30` has mean `20`. A complete window refers to the number of rows, not the number of non-missing values.

To exclude the current row and summarize the **previous three rows**, shift the completed rolling result by one row:

```java
DoubleColumn previous3 = sales.rolling(3).mean().lag(1);
previous3.setName("previous 3-row mean");
table.addColumns(previous3);
```

Here row `i` summarizes rows `i - 3` through `i - 1`. The first three rows are missing, as shown in the final column of the table above. This is a three-row trailing mean excluding the current row; it does not shorten the window to two rows.

### Stepping windows

A stepping window moves in steps. Each row is evaluated only once. A stepping window of size 2 first looks at rows 0 and 1, then rows 2 and 3, then rows 4 and 5. 

#### Working with Pairs 

Because working with Pairs of rows is common, it gets a bit of extra support that makes it a little easier to write these kind of operations.

```

```



Multi-row operations

 



