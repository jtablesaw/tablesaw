

# Tablesaw: A brief tutorial

Tablesaw is a large library. We'll use Tablesaw to look at data about Tornadoes. If you haven't already done so,
we strongly recommend that you read the Getting Started guide, before continuing here. 

## Exploring Tornadoes 

To give a better sense of how Tablesaw works, we’ll use a tornado data set from NOAA. Here’s what we’ll cover:

* Reading and writing CSV files
* Viewing table metadata
* Adding and removing columns
* Printing the first few rows for a peak at the data
* Sorting
* Running descriptive stats (mean, min, max, etc.)
* Performing mapping operations over columns
* Filtering rows
* Computing cross-tabs

All the data is in the Tablesaw *data* folder. The code is taken from the TornadoExample test class.

### Read a CSV file

Here we read a csv file of tornado data. Tablesaw infers the column types by sampling the data.

```java
@@snip [table_read](./src/main/java/tech/tablesaw/docs/Tutorial.java)
```

**Note:** that the file is addressed relative to the current working directory. You may have to change it for your code. 

### Viewing table metadata

Often, the best way to start is to print the column names for reference:

```java
@@snip [table_columns](./src/main/java/tech/tablesaw/docs/Tutorial.java)

@@snip [table_columns](./output/tech/tablesaw/docs/Tutorial.txt)
```


The *shape()* method displays the row and column counts:

```java
@@snip [table_shape](./src/main/java/tech/tablesaw/docs/Tutorial.java)

@@snip [table_shape](./output/tech/tablesaw/docs/Tutorial.txt)
```

*structure()* shows the index, name and type of each column

```java
@@snip [table_structure](./src/main/java/tech/tablesaw/docs/Tutorial.java)

@@snip [table_structure](./output/tech/tablesaw/docs/Tutorial.txt)
```

Like many Tablesaw methods, *structure()* returns a table.  You can then produce a string representation for display.
For convenience, calling *toString()* on a table invokes *print()*, which produces a string representation of the table
table. To display the table then, you can simply call. 

```java
@@snip [print_table](./src/main/java/tech/tablesaw/docs/Tutorial.java)
```

You can also perform other table operations on it. For example, the code below removes all columns whose type isn’t DOUBLE:
            
```java
@@snip [filter_structure](./src/main/java/tech/tablesaw/docs/Tutorial.java)

@@snip [filter_structure](./output/tech/tablesaw/docs/Tutorial.txt)
```


Of course, that also returned a table. We’ll cover selecting rows in more detail later.

### Previewing data

The *first(n)* method returns a new table containing the first n rows.
        
```java
@@snip [first_n](./src/main/java/tech/tablesaw/docs/Tutorial.java)

@@snip [first_n](./output/tech/tablesaw/docs/Tutorial.txt)

```

### Mapping operations

Mapping operations in Tablesaw take one or more columns as inputs and produce a new column as output. We can map
arbitrary expressions onto the table, but many common operations are built in. You can, for example, calculate the
difference in days, weeks, or years between the values in two date columns. The method below extracts the Month name
from the date column into a new column.

```java
@@snip [date_col](./src/main/java/tech/tablesaw/docs/Tutorial.java)
```

Now that you have a new column, you can add it to the table:

```java
@@snip [add_date_col](./src/main/java/tech/tablesaw/docs/Tutorial.java)
```

You can remove columns from tables to save memory or reduce clutter:

```java
@@snip [remove_col](./src/main/java/tech/tablesaw/docs/Tutorial.java)
```

### Sorting

Now lets sort the table in reverse order by the id column. The negative sign before the name indicates a descending sort.

```java
@@snip [sort_on](./src/main/java/tech/tablesaw/docs/Tutorial.java)
```
### Descriptive statistics

Descriptive statistics are calculated using the summary() method:

```java
@@snip [summary](./src/main/java/tech/tablesaw/docs/Tutorial.java)

@@snip [summary](./output/tech/tablesaw/docs/Tutorial.txt)
```

### Filtering

You can write your own methods to filter rows, but it’s easier to use the built-in filter classes as shown below:

```java
@@snip [filtering](./src/main/java/tech/tablesaw/docs/Tutorial.java)

@@snip [filtering](./output/tech/tablesaw/docs/Tutorial.txt)

```
The last example above returns a table containing only the columns named in *select()* parameters,rather than all the
columns in the original.

### Totals and sub-totals

Column metrics can be calculated using methods like *sum()*, *product()*, *mean()*, *max()*, etc.

You can apply those methods to a table, calculating results on one column, grouped by the values in another.

```java
@@snip [totals](./src/main/java/tech/tablesaw/docs/Tutorial.java)

```

This produces the following table, in which Group represents the Tornado Scale and Median the median injures for that group:

```
@@snip [totals](./output/tech/tablesaw/docs/Tutorial.txt)
```

### Cross Tabs

Tablesaw lets you easily produce two-dimensional cross-tabulations (“cross tabs”) of counts and proportions with row
and column subtotals. Here’s a count example where we look at the interaction of tornado severity and US state:

```java
@@snip [crosstabs](./src/main/java/tech/tablesaw/docs/Tutorial.java)

```

```java
@@snip [crosstabs](./output/tech/tablesaw/docs/Tutorial.txt)
```

### Putting it all together

Now that you've seen the pieces, we can put them together to perform a more complex data analysis. Lets say we want
to know how frequently Tornadoes occur in the summer. Here''s one way to approach that:

Let's start by getting only those tornadoes that occurred in the summer. 

```java
@@snip [all_together_where](./src/main/java/tech/tablesaw/docs/Tutorial.java)
```

To get the frequency, we calculate the difference in days between successive tornadoes. The *lag()* method creates a
column where every value equals the previous value (the prior row) of the source column. Then we can simply get the
difference in days between the two dates. DateColumn has a method *daysUntil()* that does this. 
It returns a NumberColumn that we'll call "delta". 

```java
@@snip [all_together_lag](./src/main/java/tech/tablesaw/docs/Tutorial.java)
```

Now we simply calculate the mean of the delta column. Splitting on year keeps us from inadvertently including the time
between the last tornado of one summer and the first tornado of the next.

```java
@@snip [all_together_summarize](./src/main/java/tech/tablesaw/docs/Tutorial.java)
```

Printing summary gives us the answer by year. 

```java
@@snip [all_together_summarize](./output/tech/tablesaw/docs/Tutorial.txt)
...
```

To get a DOUBLE for the entire period, we can take the average of the annual means. 

```java
@@snip [all_together_single_col_summary](./src/main/java/tech/tablesaw/docs/Tutorial.java)

// Average days between tornadoes in the summer:
@@snip [all_together_single_col_summary](./output/tech/tablesaw/docs/Tutorial.txt)

```

### Saving your data

To save a table, you can write it as a CSV file:

```java
@@snip [write_csv](./src/main/java/tech/tablesaw/docs/Tutorial.java)
```

And that’s it for the introduction. Please see the User Guide for more information.