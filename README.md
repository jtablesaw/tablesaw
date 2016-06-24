Tablesaw
=======
   
Tablesaw is a high-performance in-memory data table, plus tools for data crunching and a column-oriented storage format. In Java. 

With Tablesaw, analysis is more productive, and has less engineering overhead and faster iterations. 

###What makes it unique:
Tablesaw's design is driven by two ideas: 
First, almost no-one needs distributed analytics. On a single server, table saw will let you work _interactively_ with a 2,000,000,000 row table. (We plan to raise that ceiling, btw.)
Second, it should be super easy to use: To that end we happily steal ideas from everything from spreadsheets to specialized column stores like KDB.

For more information and examples see: https://javadatascience.wordpress.com

You can find an introductory tutorial here: https://javadatascience.wordpress.com/2016/06/24/an-introduction-to-tablesaw/

###What Tablesaw does today: 
* Import data from RDBMS and CSV files 
* View table metadata
* Add and remove columns
* Sort 
* Filter
* Group
* Perform map and reduce operations
* Display
* Run descriptive stats (mean, min, max, median, etc.)
* Store tables in Tablesaw's compressed columnar storage format

### What Tablesaw will do in the future:
* Load data remotely from HDFS, S3, and off the Web using HTTP
* Interactive graphics
* Integrated machine learning
* More specialized column types and operations: (lat/lon, time interval, money)

### Current performance:
In its current pre-release state, some areas of Tablesaw perform better than others. To give you a sense of where we're going, you can now load a 500,000,000 row, 4 column csv file (35GB on disk) entirely into about 10 GB of memory. If it's in Tablesaw's .saw format, you can load it in 22 seconds. Using the new indexing features you can then query the table in around 1-2 ms. That's fast enough that you could use tablesaw as a very flexible cache for a Web app.

BTW, those numbers were achieved on a laptop.

### Easy to Use is Easy to Say
To give you a sense of the API, here's an example:

```
Check this spot in a day or two for a real-world-y example.
```

### A work-in-progress
__Tablesaw is moving quickly towards stability of the core functionality and APIs__. A production release planned for Q3 2016. A great deal of additional functionality will follow the initial release.
