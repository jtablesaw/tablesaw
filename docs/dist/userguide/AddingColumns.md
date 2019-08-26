## How to add a new Column Type

Note: This is a work in progress.

It may be useful to add custom column types. Possible applications include columns for geographic coordinates, zoned date-times, characters, sequence data, email addresses, urls, distances (with units), specialized numeric types, lists, JSON, XML, or whatever objects are important in your application. 

There are several steps to be performed. This document walks you through them. 

- Add a class that implements ColumnType.
- Add String parsing support
- Add a class that implements Column
- Add functions for filtering, mapping, and summarizing
- Extending Row
- Handling SQL results sets
- Misc. 

## Add a class that implements ColumnType

To add a new ColumnType you implement the ColumnType interface. The simplest way is to subclass AbstractColumnType.

## Add a StringParser

StringParsers are used by the CsvReader to load data from CSV files. StringParser is an abstract class. 

### Update CsvReader for type detection

To use automatic type detection for the column type, it must be accessible to CsvReader. 

## Add a class that implements Column

To add a new column class you must implement the Column interface. One way is to subclass AbstractColumn.

### Other interfaces you might implement

Consider making your column implement CategoricalColumn. 

### Numeric columns 

If the type is numeric, on the other hand it would be preferable to subclass NumberColumn, which is a subclass of AbstractColumn.

## Add functions for filtering, mapping, and summarizing



## Extending Row



## Supporting Joins



## Other changes

Handling SQL Result Sets