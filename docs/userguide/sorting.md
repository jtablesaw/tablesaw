Sorting
=======

Sorting is one of the most common operations on tables. Tablesaw makes sorting easy.

In this example, we use a dataset with four columns (recipe, mix, temp, y) from an analysis of what causes cracks in chocolate cakes. The simplest way to sort is just to provide the name of the sort column or columns:

    Table sorted = table.sortOn("mix", "temp");

It sorts the table in ascending order, in the order the column names are listed. To see the result, we can use the first() method:

    sorted.first(8).print();

which produces:

    recipe mix temp y 
    1 1 175 42 
    2 1 175 39 
    3 1 175 46 
    1 1 185 46 
    2 1 185 46 
    3 1 185 44 
    1 1 195 47 
    2 1 195 51

To sort a column in descending order, simply preface the column name with a minus sign ‘-‘:

    table.sortOn("-recipe","y");

This sorts the table first on recipe in descending order, then on y, in ascending order.

Finally, if you need complete control over sorting, you can create a RowComparator and pass that to the sort method.

For example, a row comparator that (merely) sorts in ascending temperature order can be constructed and used as shown below:

    class TempComparator implements Comparator {
    
      @Override
      public int compare(Row o1, Row o2) {
        return o1.getInt("temp").intValue () - o2.getInt("temp").intValue();
      }
    }
    
It can be used to sort the cake table by:

    Table t = table.sortOn(new TempComparator());

Data: The cake dataset is from http://www.stats4stem.org/r-cake-data.html.

