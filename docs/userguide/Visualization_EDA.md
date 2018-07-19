[Contents](https://jtablesaw.github.io/tablesaw/userguide/toc)

# Visualization for Exploratory Data Analysis

In [Part 1 of this series](https://dzone.com/articles/learn-data-science-with-java-and-tablesaw), we introduced [Tablesaw](https://github.com/jtablesaw/tablesaw), a platform for data science in Java and showed how Tablesaw can be used to filter and transform datasets, and produce cross-tabulations. Now we turn to visualization.

While Tablesaw is capable of creating publication-quality graphics. The visualization we discuss here helps you see what’s going on in the data while you’re doing your analysis. This process is called Exploratory Data Analysis, a discipline established by the brilliant statistician [John Tukey](https://en.wikipedia.org/wiki/John_Tukey). Among Tukey's other exploits, he coined the term "bit" to mean the smallest unit of data. 



When you're exploring data, you need plot creation to be as easy as possible. With Tablesaw's simple plot API you can usually create and display new charts in a line or two of code. 

For our examples, we'll use a Tornado dataset from NOAA. 

```java
Table tornadoes = Table.read().csv("Tornadoes.csv");
```

### Univariate data: Counts and distributions

We start with the ubiquitous bar chart. To create a bar chart, you need two things:

1. Some numeric values
2. Some categories to group them

We'll start by counting tornado-related fatalities according to the intensity of the tornadoes. 

```Java
Table fatalities1 = tornadoes.summarize("fatalities", AggregateFunctions.sum).by("scale");

BarPlot.showHorizontal(
                "fatalities by scale",		// plot title
                fatalities1,				// table
                "scale",					// grouping column name
                "sum [fatalities]");		// numeric column name
```

![](https://jtablesaw.github.io/tablesaw/userguide/images/eda/fatalities_by_scale.png)

A simple variation is the Pareto Chart. In the plot below, fatality counts are summed by US state, and the results are sorted according to the totals in descending order. The Pareto class handles the sorting for us.  

```Java
Pareto.show("Tornado Fatalities by State", fatalities1, "state", "sum[fatalities]);
```

![](https://jtablesaw.github.io/tablesaw/userguide/images/eda/tornado_pareto.png)

### Distributions

Understanding the distribution of data within a column is often essential. Tablesaw provides several ways.  The most common is the Histogram, which is shown below.



This plot shows the distribution of injury counts for the most powerful tornadoes. To produce it, we simply filter the table to include only level 5, and call Histogram.show();

```Java
NumberColumn scale = tornadoes.numberColumn("scale");
Table level5 = tornadoes.where(scale.isEqualTo(5));

Histogram.show("Distribution of injuries for Level 5", level5, "injuries");
```



#### Distributions of sub-groups

Comparing distributions of sub-groups is also really useful.  The box plot is ideal for that:  

![](https://jtablesaw.github.io/tablesaw/userguide/images/eda/tornado_box.png)

```java
BoxPlot.show("Tornado Injuries by Scale", tornadoes, "injuries", "scale");
```

### Multivariate Data: Scatter Plots, Bubble Plots, Line Plots