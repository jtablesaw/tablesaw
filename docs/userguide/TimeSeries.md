# Time Series, Line charts, and Area charts 

You can create time series plots easily. The x axis adjusts the scale according to the size of the display area and the number of points displayed. Here's an example:



```java
Table bush = Table.read().csv("bush.csv");
bush = bush.where(bush.stringColumn("who").equalsIgnoreCase("fox"));
DateColumn x = bush.dateColumn("date");
NumberColumn y = bush.nCol("approval");
TimeSeriesPlot.show("Fox approval ratings","date", x, "rating", y);
```

