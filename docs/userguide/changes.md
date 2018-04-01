Changes from earlier version of Tablesaw

1. Changed Pom to build using Java 9
1. Re-added support for building shaded jar (core-only) to facilitate use in java shell
1. Removed Float, Int, Short, Long column types
1. Removed TableGroup, SubTable, and NumericSummaryTable
1. Removed Smile integration
1. Removed experimental time interval support
1. Renamed DoubleColumn to NumberColumn
1. Renamed CategoryColumn to StringColumn
1. Extended Join to handle doubles (after rounding to ints)
1. Reduced the number of index types
1. Removed Saw file persistence
1. Removed Beaker support
1. Removed aggregate as a separate maven project
1. Removed all deprecated methods
1. Removed methods of unclear utility 
1. Removed redundant comparator implementations 
1. Reorganized filtering to better unify column and table filtering
1. Added support for formatted printing of tables and columns (esp., number and time columns)
1. Modified all columns to use static method create() rather than public constructors
1. Added support for lag and lead methods on all column
1. Removed Lombok
1. Extended PackedLocalTime and PackedLocalDate to be essentially compatible with java's LocalDate and LocalTime
1. Significantly extended test coverage (currently 57% on core,vs. 44% on Tablesaw). 
1. Standardize column create method support for lists and arrays


Todo:
1. Standardize number, time, and dateTime to incorporate locales
1. Support summarizing by groups of n days
1. Support summarizing by groups of named time units (months, for example)
1. Implement outer joins
1. Set date and time formats on import (port from new Tablesaw code)
1. Implement spreadsheet columns
1. Unify Projection and TemporaryView
1. Extend PackedLocalDateTime to be compatible with java LocalDateTime
1. Continue to extend test coverage. 
1. Reconsider which table and column operations update-in-place vs return new versions. 
