[Contents](https://jtablesaw.github.io/tablesaw/userguide/toc)

# Rows

For efficiency reasons, Tablesaw is column-oriented, but sometimes it's important to work on rows, rather than individual columns. Generally speaking, you can do this, by iterating the indexes on the table and getting values from the columns it contains:

```java
for (int i = 0; i < t.rowCount(); i++) {
    String s = t.stringColumn("s").get(i);
    LocalDate d = t.dateColumn("d").get(i);
    System.out.println(s + " happened on " + d);
}
```

This is not super convenient, however. 

## Sorting a table using row comparison

To provide a slightly easier interface, we have provided a *Row* class. Row makes it easier to do things like sort using a comparator.

## Simplified Iteration 

You can also use them for iteration with somewhat less boilerplate code:
```java
for (Row row : aTable) { 
    String s = row.getString("s");			       
    LocalDate d = row.getDate("d");
    System.out.println(s + " happened on " + d);   
}
```







