[Contents](https://jtablesaw.github.io/tablesaw/userguide/toc)

Map functions
=============

A map is a function that when applied to one or more columns, produces a new Column with the same number of elements.

One way to think about them, is based on how many columns are involved in the function. 

## Unary

Unary map functions operate on a single column, the method receiver. For example:

    StringColumn column = columnA.upperCase();

produces a new column that contains the values in columnA, but converted to upper case.

While unary mappers operate only on a single Column, they may have an additional parameter (or parameters) that are not Columns, for example:

    StringColumn column = columnA.substring(startingPosition);
    
    // or
    
    StringColumn column1 = columnA.startsWith("foo");

## Binary

Binary mappers operate on two columns. In the example below, the result produced is a new column that contains the row-wise sums of the values in the receiver (columnB) and the parameter (columnC):

    NumberColumn column = columnB.add(columnC);

## n-Ary:

N-ary mappers operate on an Array of columns:

## Adding the new Columns to the table

The new Column is not added to the original columns table by default. To add it, use the addColumn() method defined on Table:

    StringColumn newColumn = table.column("Name").upperCase();
    table.addColumn(newColumn);

## Custom map functions 

You can create functions that build new columns using *doWithEach():*

```java
Table table = Table.read().csv("../data/bush.csv");

DateColumn dc1 = table.dateColumn("date");
DateColumn dc2 = DateColumn.create("100 days later");

dc1.doWithEach(localDate -> dc2.append(localDate.plusDays(100)));
```

The method *doWithEach(Consumer<T>)* is defined on AbstractColumn. It takes a Consumer that accepts values of the same type as the column holds: LocalDates in the case of DateColumn. If the lambda syntax above is a bit too cryptic, here's a version that does the same thing with the Consumer written out as an anonymous class:

```java
DateColumn dc2 = DateColumn.create("100 days later");

dc1.doWithEach(new Consumer<LocalDate>() {
    @Override
    public void accept(LocalDate localDate) {
        if (localDate == null) {
            dc2.appendMissing();
        } else {
        	dc2.append(localDate.plusDays(100));
        }
    }
});
```

The accept() method here calls *plusDays(100)*  on each value in the column that receives the doWithEach method invocation and appends each result to the column d2. The results are added to the column dc2 declared just before the method is called. 

In writing your own map functions it's good practice to handle missing data as we do above.

## Columns are iterable

You can also use a for loop to create new columns based on the values of existing ones. All columns in Tablesaw are iterable.

 For example: 

```java
StringColumn season = StringColumn.create("Season");

for (LocalDate date : dateColumn) {    
	if (date == null) {        
		newColumn.append(StringColumn.MISSING_VALUE);    
	}   
	else if(date.month.equals("May") {        
		newColumn.append("Flower Season");    
	}     
	else {   
    	newColumn.append("Not Flower Season");    
	}
}
myTable.addColumns(season);
```



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
