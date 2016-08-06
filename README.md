Tablesaw
=======
   
Tablesaw is a high-performance, in-memory data table in Java. 

Tablesaw's design is driven by two ideas: First, few people need distributed analytics. 
On a single server, Tablesaw lets you work _interactively_ with a 2,000,000,000 row table. 
(I plan to raise that ceiling, btw.) Second, it should be super easy to use: To that end I happily 
steal ideas from everything from spreadsheets to specialized column stores like KDB.

###On Maven Central:

    <dependency>
        <groupId>com.github.lwhite1</groupId>
        <artifactId>tablesaw</artifactId>
        <version>0.7.1</version>
    </dependency>

###Getting started?:

* You can find an introductory __tutorial__ here: https://jtablesaw.wordpress.com/an-introduction/ The early drafts of a __User Guide__ are also available on that site
* The __JavaDoc__ can be found here: http://lwhite1.github.io/tablesaw/apidocs/
* If you have __questions of any kind__: Ask them in the Issues section of this Repo.
 
A 1.0 release is planned for early September.  

### What Tablesaw does today: 
* Import data from RDBMS and CSV files, local or remote (http, S3, etc.)
* Add and remove columns
* Sort 
* Filter
* Group
* Map and reduce operations
* Descriptive stats (mean, min, max, median, etc.)
* Plotting for exploratory data analysis and model checking
* Store tables in a very-fast, compressed columnar storage format

### What Tablesaw will do:
* Integrated machine learning
* More specialized column types and operations: (lat/lon, time interval, money)

### Plotting
You can't understand a dataset unless you can _see_ it. Tablesaw currently supports a variety of plot types:
* Scatter
* Line
* Vertical Bar
* Horizontal Bar
* Histogram 
* Box plots
* Quantile Plots
* Pareto Charts

We'll continue to increase both the number of plot types and the supported options. Meanwhile, here's an example where we use [XChart](https://github.com/timmolter/XChart) to map the locations of tornadoes: 
![Alt text](https://jtablesaw.files.wordpress.com/2016/07/tornados2.png?w=809)

The goal is to seamlessly integrate Tablesaw's data manipulation facilities with plotting libraries to make visualization as easy as possible. We'll take the same approach when it comes to integrating machine learning tools. You can see examples and read more about plotting in Tablesaw here: https://jtablesaw.wordpress.com/2016/07/30/new-plot-types-in-tablesaw/.

### Current performance:
In its current state, some areas of Tablesaw perform better than others. To give you a sense of where we're going, you can now load a 500,000,000 row, 4 column csv file (35GB on disk) entirely into about 10 GB of memory. If it's in Tablesaw's .saw format, you can load it in 22 seconds. You can query that table in 1-2 ms: fast enough to use as a cache for a Web app.

BTW, those numbers were achieved on a laptop.

### Easy to Use is Easy to Say
The goal in this example is to identify the production shifts with the worst performance. These few lines demonstrate __data import__, column-wise operations (__differenceInSeconds()__), filters (__isInQ2()__) grouping and aggegating (__median()__ and __.by()__), and (__top(n)__) calculations. 

```java
    Table ops = Table.createFromCsv("data/operations.csv");                             // load data
    DateTimeColumn start = ops.dateColumn("Date").atTime(ops.timeColumn("Start"));
    DateTimeColumn end = ops.dateColumn("Date").atTime(ops.timeColumn("End");
    LongColumn duration = start.differenceInSeconds(end);                        // calc duration
    duration.setName("Duration");
    ops.addColumn(duration);
    
    Table filtered = ops.selectWhere(                                            // filter
          allOf
              (column("date").isInQ2(),
              (column("SKU").startsWith("429")),
              (column("Operation").isEqualTo("Assembly"))));
   
    Table summary = filtered.median("Duration").by("Facility", "Shift");         // group medians
    FloatArrayList tops = summary.floatColumn("Median").top(5);                  // get "slowest"

```
If you see something that can be improved, let us know.

n = 50      Actual 0 Actual 1 
Predicted 0 30       4        
Predicted 1 1        15       
