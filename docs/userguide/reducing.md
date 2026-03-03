[Contents](https://jtablesaw.github.io/tablesaw/userguide/toc)

Summarizing data with aggregate functions
================

An important set of table operations compute totals and subtotals. In the relational world, these are implemented by combining summary operations (*sum, max*…) and *group by*. Data scientists often refer to these as Split-Apply-Combine functions. Tablesaw makes calculating summaries easy, using the *summarize()* method. The general form of *summarize()* is 

```Java
t.summarize(column, functions...)
```

## Calculating multiple values for one column

The *column* parameter is the column you want to summarize, and the *functions* parameter is an array of  AggregateFunction, so you can calculate multiple statistics with a single method invocation as shown below. 

 ```Java
import static tech.tablesaw.aggregate.AggregateFunctions.*;
...
NumberColumn age = t.nCol("age");    
t.summarize(age, mean, max, min);
 ```

In this example, the *mean*, *max*, and *min* functions are instances of AggregateFunction, which we access via a static import of AggregateFunctions. AggregateFunctions is a static utility class that defines many common (and uncommon) functions. 

The return value from summarize is a SummaryFunction, to get the results in the form of a table, we use apply():

```Java
Table results = t.summarize(approval, mean, max, min).apply();
```

The results table is shown below. As you can see, there is a column for each of the functions. The default column name is  `function-name [summarized-column-name] `

```
                     bush.csv summary                      
  Mean [approval]   |  Max [approval]  |  Min [approval]  |
-----------------------------------------------------------
 64.88235294117646  |            90.0  |            45.0  |
```

## Calculating values for multiple columns

You can also summarize multiple columns in a single call, using one of the following variants:

```Java
t.summarize(column1, column2, function...)	
t.summarize(column1, column2, column3, function...)
t.summarize(column1, column2, column3, column4, function...)	
```

Usually, you'll be summarizing numeric columns, but some functions are available for other column types. BooleanColumn, for example, supports *countTrue()* and *countFalse()*. All column types support *countMissing()*. 

What if you want to apply *countTrue()* to a boolean column, while calculationg the standard deviation of a numeric column. You can achieve this with one of the options shown above:

```java
t.summarize(booleanColumn, numericColumn, standardDeviation, countTrue)
```

The summary function will compute the results for every column type that supports the given function, so in this example, booleanColumn returns a value for *countTrue*, and numericColumn returns a value for *standardDeviation*.

In the above examples, we've been using columns as the first n arguments to *summarize().* You can also refer to the column by its name.

`t.summarize(columnName...)`

## Computing subtotals with by()

In this example, we’ll use a Tornado dataset from the NOAA’s Storm Prediction Center. It contains records of every recorded US tornado from 1950-2014.  Once we’ve loaded the data, computing stats on subgroups is easy.

The argument to *by()* is a column or columns, which may be given by the column's name. In the example below, we calculate the average number of injuries, subtotaling by the tornado's *scale* attribute. 

```Java
Table avgInjuries = table.summarize("Injuries", mean).by("Scale");
```

That’s all there is to it. Note that when we use *by()*, we no longer need to call *apply()* to get the table. We give the result table a more descriptive name, and then to see the result, we can use *print()*:

    avgInjuries.setName("Average injuries by Tornado Scale");
    avgInjuries.print();
    
    Average injuries by Tornado Scale 
     Scale  |    Mean [Injuries]     |
    ----------------------------------
      -9.0  |    0.1052631578947369  |
       0.0  |  0.028963191083737013  |
       1.0  |   0.34967825609816594  |
       2.0  |    1.7487066593285947  |
       3.0  |      9.95538461538462  |
       4.0  |     59.61855670103088  |
       5.0  |    195.23170731707316  |

In this dataset, missing scale values are indicated by a -9. A scale of 0 to 5 indicates the size of the storm, with 5 being the largest/most severe. As you can see, injuries increase dramatically with the most severe storms.

If we provide multiple columns to the *by()* method, the resulting table has a row for each unique combination. To total the number of fatalities by state and scale, we would write:

    sumFatalities = table.summarize("Fatalities", sum).by("State", "Scale");

which produces:

    data/1950-2014_torn.csv summary
     State  |  Scale  |  Sum [Injuries]  |
    --------------------------------------
        AL  |    0.0  |            16.0  |
        AL  |    1.0  |           454.0  |
        AL  |    2.0  |          1091.0  |
        AL  |    3.0  |          2422.0  |
        AL  |    4.0  |          3617.0  |
        AL  |    5.0  |          1612.0  |
        AR  |   -9.0  |             5.0  |
        AR  |    0.0  |             6.0  |
        AR  |    1.0  |           210.0  |
        AR  |    2.0  |           933.0  |
       ...  |    ...  |             ...  |

etc.

Since the result returned is also a Table, you can easily perform other operations. For example, to see only results for storms in Texas and Oklahoma, you could do the following.

    List states = Lists.newArrayList("TX", "OK");
    sumFatalities.selectIf(column("State").isContainedIn(states));

producing:

    data/1950-2014_torn.csv summary
    State Scale Sum Fatalities 
    OK    0.0   0.0            
    OK    1.0   5.0            
    OK    2.0   22.0           
    OK    3.0   71.0           
    OK    4.0   143.0          
    OK    5.0   96.0           
    TX    -9.0  0.0            
    TX    0.0   2.0            
    TX    1.0   21.0           
    TX    2.0   40.0           
    TX    3.0   88.0           
    TX    4.0   219.0          
    TX    5.0   174.0  

Data: The tornado dataset is from [NOAA's Storm Prediction Center Severe Weather GIS](http://www.spc.noaa.gov/gis/svrgis/).

## Grouping on calculated columns

It may seem limiting to only be able to subtotal on column values, but in practice it's quite flexible. The flexibility comes from using map functions to produce new columns on the fly, and then using those columns to compute the summaries. For example, you might calculate average salary for subgroups based on years of employment, as in the code below:

```java
t.summarize(salary, mean).by(yearsOfEmployment.bin(20));
```

This code assigns rows to one of 20 bins, based on one numeric column (*yearsOfEmployment*) and returns the average per bin of another (*salary*). The approach can be used with any kind of column or map function, but it is especially useful when working with dates and times. 

### Grouping on standard time units (month, year, hour, etc.)

Let's say, for example, that you have a table of sales data, and want to calculate the highest dollar sales by month and region. The table has columns for sales_datetime, amount, product, and region. The answer can be had by:

```java
t.summarize(amount, max).by(region, sales_datetime.month())
```

In this example, a temporary column containing the sales month is created and used to summarize the data, but not added to the original table.

### Grouping on constant time ranges

You are not limited to grouping on standard time units like a specific month or year. You can instead create TimeWindows based on some number n of standard units. One might, for example, want to look at sales data patterns during a day in 15 minute windows. The following code does this.

```java
t.summarize(amount, sales_datetime.timeWindows(ChronoUnit.MINUTE, 15)
```

## Summarizing calculated columns

If you can group on calculated columns, why not summarize calculated columns? Lets say that you are analyzing text. Each row contains a single sentence, and we would like to characterize the length of the sentences in our dataset. You could create a column called "sentence length"  and add it to the table, but might prefer to analyze the data directly. 

```java
t.summarize(sentence.length(), min, q1, q2, q3, max, range)
```

In this example, a standard map function (*length()*) creates a NumberColumn containing the number of characters in each value of the StringColumn sentence. Various statistics (*min*, *q1*, etc.) are calculated on the resulting column.
