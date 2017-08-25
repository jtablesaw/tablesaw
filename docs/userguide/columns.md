Columns
=======

Tablesaw is all about tables, of course, but you will often want to work with an individual column or vector of data. We show how to do that here. Here is the list of currently available column types:

* Boolean
* Category (Strings from a finite set)
* Float (4 byte floating point)
* Double (8 byte floating point)
* Integer (4 byte int)
* Local Date
* Local DateTime
* Local Time
* LongInt (8 byte int)
* ShortInt (2 byte int)

All column types support a common, standard set of operations, as well as a number of type specific operations. 
We'll begin by looking at the common operations. 

#### Create a Column

    DateColumn column = DateColumn.create("test");

#### Add an element to the end of the column

    DateColumn.append(LocalDate.of(2016, 2, 28));

#### Column IO

Generally, you will save data as table, but you may also want to save just a single column. You can do this using

Save a column as a CSV

### Other common operations:

Columns do all the things you expect, here’s an incomplete list of standard operations:

    size()                           // returns the number of elements
    isEmpty()                        // returns true if column has no data; false otherwise
    first() and last()               // returns the first and last elements, respectively
    first(n) and last(n)             // returns the first and last n elements
    max() and min()                  // returns the largest and smallest elements
    top(n) and bottom(n)             // returns the n largest and smallest elements
    name()                           // returns the name of the column
    type()                           // returns the ColumnType, e.g. LOCAL_DATE
    print()                          // returns a String representation of the column
    copy()
    emptyCopy()
    unique()
    countUnique()
    asSet()
    summary()
    sortAscending()
    sortDescending()
    append(Column)                         // Appends the data in other column to this one

These operations are available on nearly all column types, including date columns. Each operates on an entire column. To operate on the values of a column, you have two choices. You can work with individual values, or use column-wise operations to work with all the values in a column in the same way. To work with individual values, you can just iterate over the column:

    List<LocalDate> weekLater = new ArrayList<>();
    for (LocalDate date: dates) {
       weekLater.add(date.plusDays(7));
    }

Just about anything you can do with an individual LocalDate you can do with an entire DateColumn, using column-wise operations. For example, the above loop could be written as:

    DateColumn dc = dates.plusDays(7);

with the difference being that the result is a new DateColumn, rather than a List. This is an example of a mapping function. You can see the full list of date mapping functions in the interface DateMapUtils, most of the methods deal with adding and subtracting units of time (days, weeks, months, etc), and calculating the column-wise differences between two date columns.

### Filtering

You can filter two ways. The first is with the built-in predicates, like IsMonday

See the end of this post for a full list of the built-in predicates for LocalDateColumn.

You can filter a date column using #selectIf(IntPredicate).  For example, if you want only those dates after February 28, 2016.

    LocalDatePredicate after_2_28 = new LocalDatePredicate() {
      LocalDate date = LocalDate.of(2016, 2, 28);
      @Override
      public boolean test(LocalDate i) {
        return i.isAfter(date);
      }
    };
    
which you can use as:

    DateColumn filtered = dates.selectIf(after_2_28);

#### Built-in Date Predicates

There are numerous built-in date predicates. For example:

    DateColumn filtered = dates.isMonday();
    DateColumn filtered = dates.isInQ2();
    DateColumn filtered = dates.isLastDayOfTheMonth();
    
You can find a full list in the JavaDoc for DateColumn.

### Grouping

This code creates a splitter that groups dates by month. First we get a Splitter to divide the data.

    LocalDateSplitter monthSplitter = new LocalDateSplitter() {
    
      @Override
      public String groupKey(LocalDate date) {
        return groupKey(PackedLocalDate.pack(date));
      }
    
      @Override
      public String groupKey(int packedLocalDate) {
        return PackedLocalDate.getMonth(packedLocalDate).toString();
      }
    };
    LocalDateColumnGroup group 
        = new LocalDateColumnGroup(column, monthSplitter);
    List<LocalDateColumn> columns = group.getSubColumns();

### Aggregating

### Cleaning data

### Correcting values


See the Tables documentation for how to add and remove columns

 