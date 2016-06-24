Tablesaw
=======
   
Tablesaw is a high-performance, in-memory data table, plus tools for data manipulation and a column-oriented storage format. In Java.

With Tablesaw, you can crunch tables with hundreds of millions of rows on a laptop. Analysis is more productive with less engineering overhead and faster iterations. 

Tablesaw provides general-purpose analytic support, with rich functionality for working with time-series. With Java 9, you'll be able to work interactively in the REPL. 

##What makes Tablesaw unique:
Tablesaw's design is driven by two ideas: 
First, almost no-one needs distributed analytics. On a single server, table saw will let you work _interactively_ with a 2,000,000,000 row table. 
Second, it should be super easy to use: To that end, we strive for readable analysis code, and happily steal ideas not just from dataframes like Pandas, but from everything from spreadsheets to specialized column stores like KDB.

For more information and examples see: https://javadatascience.wordpress.com

You can find an introductory tutorial here: https://javadatascience.wordpress.com/2016/06/24/an-introduction-to-tablesaw/

##What Tableswaw does today: 
* Import data from RDBMS and CSV files 
* Viewing table metadata
* Adding and removing columns
* Sorting 
* Filtering  
* Grouping
* Performing map and reduce operations
* Displaying
* Running descriptive stats (mean, min, max, etc.)
* Storing tables in Tablesaw's compressed columnar storage format

## What Tablesaw will do in the future:
* Load data remotely from HDFS, S3, and off the Web using HTTP
* Interactive graphics
* Integrated machine learning
* More specialized column types and operations: (lat/lon, time interval, money)

## Current performance:
In it's current pre-release state, some areas of Tablesaw performa better than others, but to give you some idea of where we're going, you can now load a half billion rows (4 column) csv file (35GB on disk) entirely into about 10 GB of memory. If you do load it from Tablesaw's .saw format, you can load that dataset in about 22 seconds. Using the new indexing features you can then query the table in around 1-2 ms.

And that's on a laptop.

## Easy to Use is Easy to Say
To give you a sense of the API, here's an example:

```
Check this spot in a day or two for a real-worldy example.
```

## A work-in-progress
__Tablesaw is moving quickly towards stability of the core functionality and APIs__. A production release planned for Q3 2016. A great deal of additional functionality will follow the initial release.
