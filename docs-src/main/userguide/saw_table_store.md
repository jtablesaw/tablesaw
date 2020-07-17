# Saw Table Store

Reading CSV files is a normal part of most analytic workflows. Unfortunately, file parsing and column type detection take time. If you run your analysis in an IDE, that means waiting for files to load on every iteration.

Saw is an alternative pure-Java table store. It's simple, fast, small, and convenient. 

For example, an 800 MB CSV file with 1M rows and 130 columns took 45 seconds to load, even with the column types specified in advance. In Saw format, it takes 3 seconds to read. On disk, it uses only 185 MB of storage. That's 15 times faster and 4 times smaller than the original. 

Saw has other advantages over CSV files.

- It maintains metadata, so you can query a table's shape and structure without loading the data.
- You can provide a list of columns and load only the ones you want, further improving the IO speed and saving memory. 

## How it works

Saw streams data to the file system, with each column written as a separate file. The streams avoid the overhead of transforming the data if possible. Dates, for example, are stored as ints, as they are in memory, rather than converted to LocalDates or Strings, first. For StringColumns, the dictionary encoding is stored in its entirety so that it needn't be recalculated when reading. Columns are compressed as they're written using a fast compression algorithm, and thread pools are used to read and write in parallel. 

## Future enhancements

Planned enhancements include control over which rows from the table are loaded using Selections:

- Load the first n rows
- Load a random sample of rows for preliminary analysis or predictive modeling 

Other possible enhancements include: 

- Save formatting information with the table. For example, you could specify that floating-point columns should be printed as percentages with 2 decimal places. 
- Ability to store indexes with the table
- Ability to store queries with the table (in query format, or as selections?)
- Ability to encrypt the data while it's streaming, so it's secure on the wire and on disk
- Ability to wrap the entire table in a single tar file to make it easier to move around

## Limitations

- Saw is not thread-safe. It is designed for use by a single analyst or process, so the table cannot be updated while it's being written. This is, of course, true for most Tablesaw operations.
- The data format is non-standard, not human-readable, and not directly usable by other applications.

### Enhancements that were considered, but rejected:

- An implementation that uses S3 (or GCP Cloud Storage) instead of a file system. Neither of these Object Stores support writing via output streams. This means you would either have to buffer the table in memory as byte arrays or write it to the file system before transfer.

- The ability to write queries in Tablesaw and load only the results. This is attractive, but would be difficult to do in one pass. You would need to stream the query columns once to create a Selection and then stream the table a second time, applying the selection to each column.

  



