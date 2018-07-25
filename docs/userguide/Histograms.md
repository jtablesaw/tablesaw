#Histograms and Box Plots

Understanding the distribution of data within a column is often essential. Tablesaw provides one and two dimensional histograms, as well as box plots.  

We start by loading the data we'll use in our examples below. 

```Java
Table property = Table.read().csv("sacremento_real_estate_transactions.csv");
```

##Histograms

A one dimensional histogram is shown below. 

![Histogram of Injuries for level 5 tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/histogram.png)

This plot shows the distribution of injury counts for the most powerful tornadoes. To produce it, we simply filter the table to include only level 5, and call Histogram.show();

```Java
Histogram.show("Distribution of injuries for Level 5", level5, "injuries");
```

![Histogram of Injuries for level 5 tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/histogram.png)

```

```

![Histogram of Injuries for level 5 tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/histogram.png)

##2D Histograms

```Java
Histogram2D.show("Distribution of price and size", property,"price", "sq__ft");
```

![Histogram of Injuries for level 5 tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/histogram.png)

##Box plots: Comparing the distributions of sub-groups

Comparing distributions of sub-groups is also really useful.  The box plot is ideal for that:  

![](https://jtablesaw.github.io/tablesaw/userguide/images/eda/tornado_box.png)

Here is the code:

​```java
BoxPlot.show("Tornado Injuries by Scale", tornadoes, "injuries", "scale");
​```