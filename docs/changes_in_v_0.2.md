Changes from Tablesaw 0.12.0

1. Increased test coverage from 44% in core, to 70%.
1. Removed Float, Int, Short, Long column types
1. Removed TableGroup, SubTable, and NumericSummaryTable
1. Removed Smile integration
1. Removed experimental time interval support
1. Removed several index types
1. Removed Saw file persistence
1. Removed all deprecated methods
1. Removed methods of limited or unclear utility 
1. Removed Lombok dependency
1. Removed duplicate comparator implementations 
1. Renamed CategoryColumn to StringColumn
1. Renamed NumericColumn interface to NumberColumn
1. Improved filtering support
    1. Standardized naming on filtering methods for tables and columns
    1. Extended table filtering to use column filter methods (e.g. "col.startsWith("foo")) in select where
    1. Added support for using Java 8 predicates to filter columns
    1. Added support for chaining "selections" so filters can be readily combined
1. Improved join support
    1. Added support for left and right outer joins
    1. Added support for joining on doubles (after rounding to ints)
1. Reading and printing formatted data
    1. Added support for formatted printing of tables and columns (esp., number and time columns)
    1. Added support for applying a locale for CSV file import
1. Standardize column instantiation
    1. Always use static method create() rather than public constructors
    1. Standardize support for instantiating from lists and arrays
1. Added support for lag and lead methods on all column
1. Extended PackedLocalTime and PackedLocalDate to be essentially compatible with java's LocalDate and LocalTime
1. Improved Aggregation/Summarization
    1. Support summarizing by groups of n days, weeks, years, etc.
    1. Support summarizing by groups of named time units (months, for example)
    1. Support for grouping on any function that returns a column 
