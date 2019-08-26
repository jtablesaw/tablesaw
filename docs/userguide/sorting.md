[Contents](https://jtablesaw.github.io/tablesaw/userguide/toc)

Sorting
=======

Sorting is one of the most common operations on tables and columns. Tablesaw makes sorting easy, yet offers complete flexibility. We'll start here with the simplest approaches, then show how to build more complex sorts.

In the examples below, we use a dataset with four columns (recipe, mix, temp, y) from an analysis of what causes cracks in chocolate cakes. 

## Ascending and Descending Sorts

The simplest way to sort is just to provide the name of the sort column or columns as arguments to a method that specifies the order. For example, to sort from lowest to highest:

```java
Table ascending = t.sortAscending("mix", "temp");
```

This sorts the table in ascending order, in the order the column names are listed. To see the result, we can use the first() method:

```java
ascending.first(8);
```

which produces:

```
recipe mix temp y 
1 1 175 42 
2 1 175 39 
3 1 175 46 
1 1 185 46 
2 1 185 46 
3 1 185 44 
1 1 195 47 
2 1 195 51
```

The method *sortDescending()* provides the analogous operation:

```java
Table descending = t.sortDescending("mix", "temp");
```

*sortAscending()* and *sortDescending()* are limiting in that you must use the same order for every column. The advantage is that the sort logic is perfectly clear when reading the code. 

## Mixing Ascending and Descending Order  

If you need to sort such that some columns are order high-to-low, and others low-to-high, you can use *sortOn()*. 

```java
Table sorted = t.sortOn("mix", "temp");
```

*sortOn()* sorts in ascending order by default, so this code produces the same results as *sortAscending()*. To sort one of the columns in descending order, simply preface the column name with a minus sign ‘-‘, as in:

    table.sortOn("-recipe","y", "mix");

This sorts the table first on recipe in descending order, then on y and mix, in ascending order.

## Using a Comparator

Finally, if you need complete control over sorting, you can create an Compator<VRow> and pass that to the *sort()* method. As Tablesaw is column-oriented, it does not have true rows. Materializing each row into some grouping of standard Java objects would be very inefficient. A VRow is a kind of virtual row that serves as a proxy for a single row in a table.

For example, a row comparator that sorts in ascending temperature order can be constructed and used as shown below:

```java
Comparator<VRow> tempComparator = new Comparator<VRow>() {
	@Override 
    public int compare(VRow o1, VRow o2) {
    	return Double.compare(o1.getDouble("temp"), o2.getDouble("temp"));
    }
};
```

It can be used to sort the cake table by:

```java
Table t = table.sortOn(tempComparator);
```

Obviously, a custom comparator isn't needed in this example, but the ability is there when you need it. With  a Comparator<VRow>, sort logic can include transformations and combinations of the data when making ordering decisions. 

Data: The cake dataset is from http://www.stats4stem.org/r-cake-data.html.

