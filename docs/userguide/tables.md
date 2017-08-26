Tables
======
Tables are the primary data-type and general focus of Tablesaw. Here we’ll provide an overview of the operations they provide. 
Most of the sections below are for illustrative purposes, and typically will have their own section of the User Guide where they are examined more fully. For still more detail, see the JavaDoc for tech.tablesaw.api.Table.

## Tables "all the way down"
Tablesaw has a huge number of methods for creating, querying, manipulating, displaying, and saving tables. So it makes sense that we use tables widely, and many operations on tables return other tables. For example, when you ask a table to describe its structure, it returns a new table that contains the column names, types, and order. 

## Creating tables


### Import data

Tablesaw can load data from character delimited text files (including CSV and Tab-separated files), from streams, and from any data source that can create a JDBC result set. As this includes essentially all relational databases (and many non-relational ones), most of the world’s structured data in can be loaded without a prior transformation. 

#### Import from a CSV file

You can load a table from a CSV file by providing the file name. 

    Table t = Table.read().csv("myFile.csv");
    
This simple method supplies default values for a number of parameters like the type of the separator character. See 
[Importing data](https://jtablesaw.github.io/tablesaw/userguide/importing_data) for other options and more detail.    

## Displaying data

The simplest way to display a table is to call "print()" on it, which return a formatted String representation.
 
    aTable.print();
    
Often, that produces too much output, so the methods first(n) and last(n) are available. These return a copy of the table that contains only the first n or last n rows respectively.

    aTable.first(3);
    aTable.last(4); 

## Getting table metadata

There are a number of ways to get familiar with a new dataset. Here are some of the most useful.

table.name() returns its name, which defaults to the name of the file it was created from. You can change it if you like.

table.columnNames() returns an array of column-name strings: 

table.structure() returns a list of columns with their position and types:

    Structure of data/tornadoes_1950-2014.csv
        Index Column Names Column Type 
        0     The Date     LOCAL_DATE  
        1     The Time     LOCAL_TIME  
        2     State        CATEGORY    
        3     State No     INTEGER     
        4     Scale        INTEGER 
            
           
table.shape() returns the table’s size in rows and columns:

    59945 rows X 10 cols

You can also get the rowCount() and columnCount() individually from a table.

## Add and remove columns

You can add a column to a Table using the addColumn() method:

    table.addColumn(aColumn);
    
You can also specify that the column be inserted at a particular place by providing an index:

    table.addColumn(3, aColumn);
    
As usual in java, column numbering begins at 0, rather than 1.

The column you add must either be empty or have the same number of elements as the other columns in the table.

To remove a column or columns:

    table.removeColumn(aColumn);
    table.removeColumn("columnName");
    
Columns can also be removed by referencing them by name. Alternately, you can specify just the columns to retain:

    table.retainColumns(aColumn);
    
Again you can specify the columns either directly, or by using their names.

While addColumn() and removeColumn() update the receiver in place, you can also create a new table with a subset of the columns in the receiver. This can be done by specifying the names of the columns that you want to retain, in the order you want them to appear.

    Table reduced = oldTable.selectColumns("Name", "Age", "Height", "Weight");

You can also create a new table by specifying the columns in the current table that you don’t want, which might save some typing:

    Table reduced = oldTable.rejectColumns("Street Address");

In this case the columns in the result table are in the same order as in the original.

## Selecting columns

Often you will want a reference to a column in the table. To get all the columns as a list:

    table.columns();
    
Columns can also be selected by index or name:

    table.columns("column1", "column2");

Often you want just one column, which you can get using table.column(“column name”).

Since Tablesaw columns are typed, you often need to cast the returned column to something more specific. For example:

    IntColumn ic = (IntColumn) table.column();

as a convenience, tables have column accessors that are type specific: The do the casting for you.

    IntColumn ic = table.intColumn();
    
Add and remove rows

 
## Filter

 

## Sort

 

## Export

 

## Save

If you use a large table more than once, you may want to save it in Tablesaw’s compressed columnar “.saw” format.

In .saw format, reads and writes are (at least) an order of magnitude faster than the equivalent CSV operations, and disk usage is also greatly reduced.

To save the table, you provide the path to the directory where the data will reside.  The save method returns a string based on the table name that can be used to reload it. When you reload the data, use that string.

    String dbName = tornadoes.save("/tmp/tablesaw/testdata");
    Table tornadoes = Table.readTable(dbName);
