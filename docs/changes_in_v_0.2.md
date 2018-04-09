Changes from Tablesaw

1. Increased test coverage from 44% in core, to 70%.
1. Removed Float, Int, Short, Long column types
1. Removed TableGroup, SubTable, and NumericSummaryTable
1. Removed Smile integration
1. Removed experimental time interval support
1. Removed several index types
1. Removed Saw file persistence
1. Removed all deprecated methods
1. Removed methods of unclear utility 
1. Removed Lombok dependency
1. Removed duplicate comparator implementations 
1. Renamed CategoryColumn to StringColumn
1. Renamed NumericColumn interface to NumberColumn
1. Reorganized filtering to better unify column and table filtering
1. Added support for applying a locale for CSV file import
1. Extended Join to support left and right outer joins
1. Extended Join to handle doubles (after rounding to ints)
1. Added support for formatted printing of tables and columns (esp., number and time columns)
1. Modified all columns to use static method create() rather than public constructors
1. Added support for lag and lead methods on all column
1. Extended PackedLocalTime and PackedLocalDate to be essentially compatible with java's LocalDate and LocalTime
1. Standardize column create method support for lists and arrays
1. Summarization
    1. Support summarizing by groups of n days, weeks, years, etc.
    1. Support summarizing by groups of named time units (months, for example)


Todo:
1. Standardize number, time, and dateTime to incorporate locales
1. Set date and time formats on import (port from new Tablesaw code)
1. Implement spreadsheet columns
1. Unify Projection and TemporaryView
1. Extend PackedLocalDateTime to be compatible with java LocalDateTime
1. Reconsider which table and column operations update-in-place vs return new versions. 
