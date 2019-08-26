[Contents](https://jtablesaw.github.io/tablesaw/userguide/toc)

# CrossTabs

If you're interested in how frequently observations appear in different categories, you can use cross-tabulations, also known as contingency tables. Tablesaw supports one and two dimensional crossTabs.

The Table class contains the methods you need. 

## An example

In the example below we show the observation counts for each combination.

```java
@@snip [intro_block](./src/main/java/tech/tablesaw/docs/userguide/CrossTabs.java)
```

```java
@@snip [intro_block](./output/tech/tablesaw/docs/userguide/CrossTabs.txt)
```

Note the total column on the right, which shows that 23 polls were conducted in April, etc., across all pollsters.
Similarly, the column totals at the bottom show that, Fox conducted 64 polls, Gallup 119, etc.

### Single variable totals

You can get single variable counts using the *xTabCounts()* method that takes only one column name argument . 

```java
@@snip [who_counts](./src/main/java/tech/tablesaw/docs/userguide/CrossTabs.java)
```

producing:

```java
@@snip [who_counts](./output/tech/tablesaw/docs/userguide/CrossTabs.txt)
```

### Calculating Percents

You may want to see the percent of polls conducted by each pollster, rather than raw counts.
The xTabPercents() method is used for that.

```java
@@snip [who_percents](./src/main/java/tech/tablesaw/docs/userguide/CrossTabs.java)
```

Actually, percents is a misnomer. The results produced are the proportions in decimal format. To get percent-formatted
output we use a different NumericColumnFormatter.

```java
@@snip [who_percents_format](./src/main/java/tech/tablesaw/docs/userguide/CrossTabs.java)
```

```java
@@snip [who_percents_format](./output/tech/tablesaw/docs/userguide/CrossTabs.txt)
```

### Table Percents

When you have two variables, you can display the percent that falls into each combination as shown below.

```java
@@snip [table_percents](./src/main/java/tech/tablesaw/docs/userguide/CrossTabs.java)
```

Because the percents are small, we updated the formatter to show a single fractional digit after the decimal point.

<br>

The output can best be understood by looking at an example. Of all the polls in the dataset, 1.9% were conducted by
Fox in April, 3.1% by Gallup in April, 0.9% by Fox in August, etc. 

```java
@@snip [table_percents](./output/tech/tablesaw/docs/userguide/CrossTabs.txt)
```

As you can see, this also gives you the 'total' percents by month and pollster.

### Column Percents and Row Percents

The final option is to show column percents or row percents. We'll start with column percents.
You calculate them as shown below.

```java
@@snip [column_percents](./src/main/java/tech/tablesaw/docs/userguide/CrossTabs.java)
```

Look at the column for "fox", the values you see are the percentages for fox alone: 9% of fox's polls were conducted
in April, 5% in August, etc. 

Looking across the columns on the other hand is not very intuitive (or useful, probably)
until you get to the total, which shows the average across all pollsters by month.

```java
@@snip [column_percents](./output/tech/tablesaw/docs/userguide/CrossTabs.txt)
```

Row percents show the opposite viewpoint. 

```java
@@snip [row_percents](./src/main/java/tech/tablesaw/docs/userguide/CrossTabs.java)
```

Here we see that, of all the polls conducted in April, fox conducted 26%, Gallup 43%, and The University of Pennsylvania
conducted 0% with rounding. 

```java
@@snip [row_percents](./output/tech/tablesaw/docs/userguide/CrossTabs.txt)
```

And that's all there is to Tablesaw CrossTabs. 