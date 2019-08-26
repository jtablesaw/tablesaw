[Contents](https://jtablesaw.github.io/tablesaw/userguide/toc)

# CrossTabs

If you're interested in how frequently observations appear in different categories, you can use cross-tabulations, also known as contingency tables. Tablesaw supports one and two dimensional crossTabs.

The Table class contains the methods you need. 

## An example

In the example below we show the observation counts for each combination.

```java
// preparation: load the data, and add a string column to hold the months in the date col
Table table = Table.read().csv("../data/bush.csv");
StringColumn month = table.dateColumn("date").month();
month.setName("month");
table.addColumns(month);

// perform the crossTab operation
Table counts = table.xTabCounts("month", "who");
System.out.println(counts);
```

```java
                              Crosstab Counts: month x who                              
 [labels]   |  fox  |  gallup  |  newsweek  |  time.cnn  |  upenn  |  zogby  |  total  |
----------------------------------------------------------------------------------------
     APRIL  |    6  |      10  |         3  |         1  |      0  |      3  |     23  |
    AUGUST  |    3  |       8  |         2  |         1  |      0  |      2  |     16  |
  DECEMBER  |    4  |       9  |         4  |         3  |      2  |      5  |     27  |
  FEBRUARY  |    7  |       9  |         4  |         4  |      1  |      4  |     29  |
   JANUARY  |    7  |      13  |         6  |         3  |      5  |      8  |     42  |
      JULY  |    6  |       9  |         4  |         3  |      0  |      4  |     26  |
      JUNE  |    6  |      11  |         1  |         1  |      0  |      4  |     23  |
     MARCH  |    5  |      12  |         4  |         3  |      0  |      6  |     30  |
       MAY  |    4  |       9  |         5  |         3  |      0  |      1  |     22  |
  NOVEMBER  |    4  |       9  |         6  |         3  |      1  |      1  |     24  |
   OCTOBER  |    7  |      10  |         8  |         2  |      1  |      3  |     31  |
 SEPTEMBER  |    5  |      10  |         8  |         3  |      0  |      4  |     30  |
     Total  |   64  |     119  |        55  |        30  |     10  |     45  |    323  |
```

Note the total column on the right, which shows that 23 polls were conducted in April, etc., across all pollsters.
Similarly, the column totals at the bottom show that, Fox conducted 64 polls, Gallup 119, etc.

### Single variable totals

You can get single variable counts using the *xTabCounts()* method that takes only one column name argument . 

```java
Table whoCounts = table.xTabCounts("who");
```

producing:

```java
     Column: who      
 Category  |  Count  |
----------------------
    zogby  |     45  |
    upenn  |     10  |
 time.cnn  |     30  |
      fox  |     64  |
 newsweek  |     55  |
   gallup  |    119  |
```

### Calculating Percents

You may want to see the percent of polls conducted by each pollster, rather than raw counts.
The xTabPercents() method is used for that.

```java
Table whoPercents = table.xTabPercents("who");
```

Actually, percents is a misnomer. The results produced are the proportions in decimal format. To get percent-formatted
output we use a different NumericColumnFormatter.

```java
whoPercents
    .columnsOfType(ColumnType.DOUBLE) // format to display as percents
    .forEach(x -> ((NumberColumn) x).setPrintFormatter(NumberColumnFormatter.percent(0)));
```

```java
       Column: who       
 Category  |  Percents  |
-------------------------
    zogby  |       14%  |
    upenn  |        3%  |
 time.cnn  |        9%  |
      fox  |       20%  |
 newsweek  |       17%  |
   gallup  |       37%  |
```

### Table Percents

When you have two variables, you can display the percent that falls into each combination as shown below.

```java
Table tablePercents = table.xTabTablePercents("month", "who");
tablePercents
    .columnsOfType(ColumnType.DOUBLE)
    .forEach(x -> ((NumberColumn) x).setPrintFormatter(NumberColumnFormatter.percent(1)));
```

Because the percents are small, we updated the formatter to show a single fractional digit after the decimal point.

<br>

The output can best be understood by looking at an example. Of all the polls in the dataset, 1.9% were conducted by
Fox in April, 3.1% by Gallup in April, 0.9% by Fox in August, etc. 

```java
                               Crosstab Table Proportions:                                 
 [labels]   |   fox   |  gallup  |  newsweek  |  time.cnn  |  upenn  |  zogby  |  total   |
-------------------------------------------------------------------------------------------
     APRIL  |   1.9%  |    3.1%  |      0.9%  |      0.3%  |   0.0%  |   0.9%  |    7.1%  |
    AUGUST  |   0.9%  |    2.5%  |      0.6%  |      0.3%  |   0.0%  |   0.6%  |    5.0%  |
  DECEMBER  |   1.2%  |    2.8%  |      1.2%  |      0.9%  |   0.6%  |   1.5%  |    8.4%  |
  FEBRUARY  |   2.2%  |    2.8%  |      1.2%  |      1.2%  |   0.3%  |   1.2%  |    9.0%  |
   JANUARY  |   2.2%  |    4.0%  |      1.9%  |      0.9%  |   1.5%  |   2.5%  |   13.0%  |
      JULY  |   1.9%  |    2.8%  |      1.2%  |      0.9%  |   0.0%  |   1.2%  |    8.0%  |
      JUNE  |   1.9%  |    3.4%  |      0.3%  |      0.3%  |   0.0%  |   1.2%  |    7.1%  |
     MARCH  |   1.5%  |    3.7%  |      1.2%  |      0.9%  |   0.0%  |   1.9%  |    9.3%  |
       MAY  |   1.2%  |    2.8%  |      1.5%  |      0.9%  |   0.0%  |   0.3%  |    6.8%  |
  NOVEMBER  |   1.2%  |    2.8%  |      1.9%  |      0.9%  |   0.3%  |   0.3%  |    7.4%  |
   OCTOBER  |   2.2%  |    3.1%  |      2.5%  |      0.6%  |   0.3%  |   0.9%  |    9.6%  |
 SEPTEMBER  |   1.5%  |    3.1%  |      2.5%  |      0.9%  |   0.0%  |   1.2%  |    9.3%  |
     Total  |  19.8%  |   36.8%  |     17.0%  |      9.3%  |   3.1%  |  13.9%  |  100.0%  |
```

As you can see, this also gives you the 'total' percents by month and pollster.

### Column Percents and Row Percents

The final option is to show column percents or row percents. We'll start with column percents.
You calculate them as shown below.

```java
Table columnPercents = table.xTabColumnPercents("month", "who");
```

Look at the column for "fox", the values you see are the percentages for fox alone: 9% of fox's polls were conducted
in April, 5% in August, etc. 

Looking across the columns on the other hand is not very intuitive (or useful, probably)
until you get to the total, which shows the average across all pollsters by month.

```java
                              Crosstab Column Proportions:                               
 [labels]   |  fox   |  gallup  |  newsweek  |  time.cnn  |  upenn  |  zogby  |  total  |
-----------------------------------------------------------------------------------------
     APRIL  |    9%  |      8%  |        5%  |        3%  |     0%  |     7%  |     7%  |
    AUGUST  |    5%  |      7%  |        4%  |        3%  |     0%  |     4%  |     5%  |
  DECEMBER  |    6%  |      8%  |        7%  |       10%  |    20%  |    11%  |     8%  |
  FEBRUARY  |   11%  |      8%  |        7%  |       13%  |    10%  |     9%  |     9%  |
   JANUARY  |   11%  |     11%  |       11%  |       10%  |    50%  |    18%  |    13%  |
      JULY  |    9%  |      8%  |        7%  |       10%  |     0%  |     9%  |     8%  |
      JUNE  |    9%  |      9%  |        2%  |        3%  |     0%  |     9%  |     7%  |
     MARCH  |    8%  |     10%  |        7%  |       10%  |     0%  |    13%  |     9%  |
       MAY  |    6%  |      8%  |        9%  |       10%  |     0%  |     2%  |     7%  |
  NOVEMBER  |    6%  |      8%  |       11%  |       10%  |    10%  |     2%  |     7%  |
   OCTOBER  |   11%  |      8%  |       15%  |        7%  |    10%  |     7%  |    10%  |
 SEPTEMBER  |    8%  |      8%  |       15%  |       10%  |     0%  |     9%  |     9%  |
     Total  |  100%  |    100%  |      100%  |      100%  |   100%  |   100%  |   100%  |
```

Row percents show the opposite viewpoint. 

```java
Table rowPercents = table.xTabRowPercents("month", "who");
```

Here we see that, of all the polls conducted in April, fox conducted 26%, Gallup 43%, and The University of Pennsylvania
conducted 0% with rounding. 

```java
                               Crosstab Row Proportions:                                
 [labels]   |  fox  |  gallup  |  newsweek  |  time.cnn  |  upenn  |  zogby  |  total  |
----------------------------------------------------------------------------------------
     APRIL  |  26%  |     43%  |       13%  |        4%  |     0%  |    13%  |   100%  |
    AUGUST  |  19%  |     50%  |       12%  |        6%  |     0%  |    12%  |   100%  |
  DECEMBER  |  15%  |     33%  |       15%  |       11%  |     7%  |    19%  |   100%  |
  FEBRUARY  |  24%  |     31%  |       14%  |       14%  |     3%  |    14%  |   100%  |
   JANUARY  |  17%  |     31%  |       14%  |        7%  |    12%  |    19%  |   100%  |
      JULY  |  23%  |     35%  |       15%  |       12%  |     0%  |    15%  |   100%  |
      JUNE  |  26%  |     48%  |        4%  |        4%  |     0%  |    17%  |   100%  |
     MARCH  |  17%  |     40%  |       13%  |       10%  |     0%  |    20%  |   100%  |
       MAY  |  18%  |     41%  |       23%  |       14%  |     0%  |     5%  |   100%  |
  NOVEMBER  |  17%  |     38%  |       25%  |       12%  |     4%  |     4%  |   100%  |
   OCTOBER  |  23%  |     32%  |       26%  |        6%  |     3%  |    10%  |   100%  |
 SEPTEMBER  |  17%  |     33%  |       27%  |       10%  |     0%  |    13%  |   100%  |
     Total  |  20%  |     37%  |       17%  |        9%  |     3%  |    14%  |   100%  |
```

And that's all there is to Tablesaw CrossTabs. 
