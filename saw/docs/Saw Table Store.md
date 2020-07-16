# Saw Table Store

Saw is a simple, pure Java table storage system for Tablesaw designed to be fast, small, and convenient. 

For example, an 805 MB CSV file with 1M rows and 130 columns was converted to Saw. The file takes 45 seconds to load in Tablesaw from the CSV. In Saw format it takes 3 seconds to read and 2 seconds to write, and uses only 185 MB on the file system. This is important because it means you can move a table in Saw format across the network quickly.

Saw has other useful features.

- It maintains metadata abut tables, so you can query their shape and structure without loading the file into memory.
- You can provide a list of columns and load only the ones you want, further improving the IO speed and saving memory 

Planned enhancements include control over which rows from the table are loaded using Selections:

- Load the first n rows
- Load specific rows (e.g. every 10th row)
- Load a random sample of rows for preliminary analysis or predictive modeling 

Ideally, you might write an arbitrary query in tablesaw and load only the results, but it seems very difficult to do in one pass.

Other planned enhancements include: 

- Save formatting information with the table. For example you could specify that floating point columns should be printed as percentages with 2 decimal places. 
- An implementation that uses S3 instead of a file system.
- Ability to store indexes with the table
- Ability to store queries with the table (in query format, or as selections?)
- Ability to encrypt the data on disk and on the wire between the disk and memory
- Ability to wrap the entire table in a single tar file

What Saw is not:

- Thread-safe. It is designed for use by a single analyst or process
- A database. 



