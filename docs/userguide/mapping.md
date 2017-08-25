Map functions
=============

A map is a function that when applied to one or more columns, produces a new Column with the same number of elements.

Map functions come in three forms: Unary, Binary and n-ary.

## Unary

Unary map functions operate on a single column, the method receiver. For example:

    StringColumn column = columnA.upperCase();
    
produces a new column that contains the values in columnA, but converted to upperCase.

While unary mappers operate only on a single Column, they may have an additional parameter (or parameters) that are not Columns, for example:

    StringColumn column = columnA.substring(startingPosition);

## Binary

Binary mappers operate on two columns. In the example below, the result produced is a new column that contains the row-wise sums of the values in the receiver (columnB) and the parameter (columnC):

    FloatColumn column = columnB.add(columnC);

## n-Ary:

N-ary mappers operate on an Array of columns:

## Adding the new Columns to the table

The new Column is not added to the original columns table by default. To add it, use the addColumn() method defined on Table:

    StringColumn newColumn = table.column("Name").upperCase();
    table.addColumn(newColumn);

## List of Current Map Functions

### String Mappers

#### Unary

    upperCase()
    lowerCase()
    trim()
    replaceAll(String regex, replacementString)
    replaceFirst(String regex, String replacement)
    substring(int startPosition)
    substring(int startPosition, int endPosition)
    abbreviate()
    padStart(int minimumLength, paddingCharacter)
    padEnd(int minimumLength, char paddingCharacter)

#### Binary

    commonPrefix(StringColumn c)
    commonSuffix(StringColumn c)
    join(StringColumn c, String delimiter)
    distance(StringColumn c) // returns the Levenshtein distance between two strings

### Date Mappers

#### Unary

    atStartOfDay()
    atTime(LocalTime time)
    plusDays(int days)
    plusWeeks(int weeks)
    plusMonths(int months)
    plusYears(int years)
    minusDays(int days)
    minusWeeks(int weeks)
    minusMonths(int months)
    minusYears(int years)
    year()
    dayOfYear()
    monthName()
    monthNumber()
    dayOfMonth()
    dayOfWeek()

#### Binary

    atTime(TimeColumn c)
    differenceInDays(DateColumn c)
    differenceInWeeks(DateColumn c)
    differenceInMonths(DateColumn c)
    differenceInYears(DateColumn c)

### Float and Double Mappers

#### Unary

    abs()
    logN()
    log1p()
    log10()
    round()
    square()
    sqrt()
    cube()
    cubeRoot()
    
#### Binary

    subtract(RealColumn column)
    divideBy(RealColumn column)
    mod(RealColumn column)
    
#### n-Ary

    add(RealColumn[] columns)
    multiply(RealColumn[] columns)
    min(RealColumn[] columns)
    max(RealColumn[] columns)
