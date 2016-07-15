Tablesaw
=======
   
Tablesaw is a high-performance in-memory data table in Java. 

###On Maven Central:

    <dependency>
        <groupId>com.github.lwhite1</groupId>
        <artifactId>tablesaw</artifactId>
        <version>0.7.1</version>
    </dependency>

###What makes it unique:
Tablesaw's design is driven by two ideas: 
First, few people need distributed analytics. On a single server, Tablesaw lets you work _interactively_ with a 2,000,000,000 row table. (I plan to raise that ceiling, btw.)
Second, it should be super easy to use: To that end I happily steal ideas from everything from spreadsheets to specialized column stores like KDB.

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
* Store tables in a very-fast, compressed columnar storage format

### What Tablesaw will do:
* Interactive graphics
* Integrated machine learning
* More specialized column types and operations: (lat/lon, time interval, money)

### Current performance:
In its current state, some areas of Tablesaw perform better than others. To give you a sense of where we're going, you can now load a 500,000,000 row, 4 column csv file (35GB on disk) entirely into about 10 GB of memory. If it's in Tablesaw's .saw format, you can load it in 22 seconds. You can query that table in 1-2 ms: fast enough to use as a cache for a Web app.

BTW, those numbers were achieved on a laptop.

### Easy to Use is Easy to Say
The goal in this example is to identify the production shifts with the worst performance. These few lines demonstrate __data import__, column-wise operations (__differenceInSeconds()__), filters (__isInQ2()__) grouping and aggegating (__median()__ and __.by()__), and (__top(n)__) calculations. 

```java
    Table ops = Table.create("data/operations.csv");                             // load data
    
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
