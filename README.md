Tablesaw
=======
   
Tablesaw is a high-performance in-memory data table, tools for data crunching, and a column-oriented storage format. In Java. 

###What makes it unique:
Tablesaw's design is driven by two ideas: 
First, almost no-one needs distributed analytics. On a single server, table saw will let you work _interactively_ with a 2,000,000,000 row table. (We plan to raise that ceiling, btw.)
Second, it should be super easy to use: To that end we happily steal ideas from everything from spreadsheets to specialized column stores like KDB.

You can find an introductory tutorial here: https://jtablesaw.wordpress.com/2016/06/24/an-introduction-to-tablesaw/

###What Tablesaw does today: 
* Import data from RDBMS and CSV files 
* Add and remove columns
* Sort 
* Filter
* Group
* Perform map and reduce operations
* Run descriptive stats (mean, min, max, median, etc.)
* Store tables in a compressed columnar storage format

### What Tablesaw will do in the future:
* Load data remotely from HDFS, S3, and off the Web using HTTP
* Interactive graphics
* Integrated machine learning
* More specialized column types and operations: (lat/lon, time interval, money)

### Current performance:
In its current pre-release state, some areas of Tablesaw perform better than others. To give you a sense of where we're going, you can now load a 500,000,000 row, 4 column csv file (35GB on disk) entirely into about 10 GB of memory. If it's in Tablesaw's .saw format, you can load it in 22 seconds. You can query that table in 1-2 ms: fast enough to use as a cache for a Web app.

BTW, those numbers were achieved on a laptop.

### Easy to Use is Easy to Say
To give you a sense of the API, here's an example. The goal in this analysis is to identify the production shifts with the slowest performance. Our table has data from all facilities, operations, products, and shifts for the past year. We're only interested in assembly operations in the second quarter for model 429.

```java
   // Load the data, inferring the column types from the data read
    Table ops = Table.create("data/operations.csv");

    // Combine the date and time fields so that we don't miscalculate on jobs that cross date bounderies
    DateTimeColumn start = ops.dateColumn("Date").atTime(ops.timeColumn("Start"));
    DateTimeColumn end = ops.dateColumn("Date").atTime(ops.timeColumn("End"));
    
    // Hand the date cross-overs
    for (int row : ops) {
      if (ops.timeColumn("End").get(row).isBefore(ops.timeColumn("Start").get(row))) {
        end.get(row).plusDays(1);
      }
    }

    // Calculate the durations from the start and end times
    LongColumn duration = start.differenceInSeconds(end);
    
    // give the new column a name and add it to the table
    duration.setName("Duration");
    ops.addColumn(duration);
    
    // filter the table by quarter, model and operation
    Table q2_429_assembly = ops.selectWhere(
          allOf
              (column("date").isInQ2(),
              (column("SKU").startsWith("429")),
              (column("Operation").isEqualTo("Assembly"))));
   
    // Calulate the median peformance by facility and group.
    Table durationByFacilityAndShift = q2_429_assembly.reduce("Duration", median, "Facility", "Shift");
    
    // Get the top "worst" peforming 
    FloatArrayList tops = durationByFacilityAndShift.floatColumn("Median").top(5);

```

I hope you can see how the code reflects the intent, and how column-wise operators like _differenceInSeconds(dateTimeColumn)_, _isInQ2()_ and  _startsWith(aString)_ make data operations easy to express. If you see something that can be improved, let us know.

### A work-in-progress
__Tablesaw is moving quickly towards stability of the core functionality and APIs__. A production release planned for Q3 2016. A great deal of additional functionality will follow the initial release.
