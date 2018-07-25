#Histograms and Box Plots

Understanding the distribution of data within a column is often essential. Tablesaw provides one and two dimensional histograms, as well as box plots.  

We start by loading the data we'll use in our examples below. 

```Java
Table property = Table.read().csv("sacremento_real_estate_transactions.csv");
```

##Histograms

A one dimensional histogram of property prices is shown below. 

![Histogram of prices](https://jtablesaw.github.io/tablesaw/userguide/images/eda/histogram1.png)

This plot shows the distribution of injury counts for the most powerful tornadoes. To produce it, we simply filter the table to include only level 5, and call Histogram.show();

```Java
Histogram.show("Distribution of prices", property.numberColumn("price"));
```

We also take a histogram of sizes, after setting any sizes of 0 square feet to "missing".

```java
NumberColumn sqft = property.numberColumn("sq__ft");
sqft.set(sqft.isEqualTo(0), DoubleColumn.MISSING_VALUE);

Histogram.show("Distribution of property sizes", property.numberColumn("sq__ft"));
```

![Histogram of Property sizes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/histogram2.png)

##2D Histograms

It may be useful to look at the relationship of two distributions, you can do that with a 2D Histogram. 

```Java
Histogram2D.show("Distribution of price and size", property,"price", "sq__ft");
```

![Histogram of price and size](https://jtablesaw.github.io/tablesaw/userguide/images/eda/histogram2d.png)

##Box plots: Comparing the distributions of sub-groups

Comparing distributions of sub-groups is also really useful.  The box plot is ideal for that:  

![Box plot of price by type](https://jtablesaw.github.io/tablesaw/userguide/images/eda/box1.png)

And here's the code:

```java
BoxPlot.show("Prices by property type", property, "type", "price");
```

