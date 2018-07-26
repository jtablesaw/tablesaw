[Contents](https://jtablesaw.github.io/tablesaw/userguide/toc)

# Bars, Pies, and Pareto charts

In [Part 1 of this series](https://dzone.com/articles/learn-data-science-with-java-and-tablesaw), we introduced [Tablesaw](https://github.com/jtablesaw/tablesaw), a platform for data science in Java and showed how Tablesaw can be used to filter and transform datasets, and produce cross-tabulations. Now we turn to visualization. For this discussion, we'll use a Tornado dataset from NOAA.

While Tablesaw is capable of creating publication-quality graphics. The visualization we discuss here helps you see what’s going on in the data while you’re doing your analysis. This process is called Exploratory Data Analysis, a discipline established by the brilliant statistician [John Tukey](https://en.wikipedia.org/wiki/John_Tukey). Among Tukey's other exploits, he coined the term "bit" to mean the smallest unit of data. 

Here we focus on some common plot types for working with univariate data:

- Bar charts
- Pie charts
- Pareto charts

When you're exploring data, you need plot creation to be as easy as possible. With Tablesaw's simple plot API you can usually create and display new charts in a line or two of code. 

First we load the Tornado dataset: 

```java
Table tornadoes = Table.read().csv("Tornadoes.csv");
```

### Univariate data: counts and distributions

#### Bar Plots

We start with the ubiquitous bar chart. Bar charts generally display data that has been summarized into groups. To create a bar chart, you need two things:

1. Some numeric values
2. Some categories to group them

We'll start by counting tornado-related fatalities according to the intensity of the tornadoes. 

```Java
Table fatalities1 = tornadoes.summarize("fatalities", AggregateFunctions.sum).by("scale");

Plot.show(
    HorizontalBarPlot.create(
                "fatalities by scale",		// plot title
                fatalities1,				// table
                "scale",					// grouping column name
                "sum [fatalities]"));		// numeric column name
```

![](https://jtablesaw.github.io/tablesaw/userguide/images/eda/fatalities_by_scale.png)

#### Plotting means, medians and other summary statistics

In the example above, we created plots that displayed the sum of values. We did this by first calling summarize() on our table, passing in the name of the column to summarize, the aggregation function *sum*, and applying a clause *by()* that specified how to group the data.

There are many aggregation functions defined in the class AggregationFunctions, such as *sum, mean, median, standardDeviation, percentile(n), max, kurtosis,* etc. We could have used any of those instead of sum. Here we'll look at the mean values:

```java
Table injuries1 = tornadoes.summarize("injuries", mean).by("scale");

Plot.show(
	HorizontalBarPlot.create("Average number of tornado injuries by scale", 
                       injuries1, "scale", "mean [injuries]"));
```

In our upcoming section on advanced plotting features, we'll cover how to create stacked and grouped bar plots. 

#### Stacking and Grouping



```Table murders = Table.read().csv("SHR76_16.csv");```



#### Pie Plots

Pie plots are simultaneously widely criticized and ubiquitous. As a general rule, bar plots are easier to interpret, and so, generally, are to be preferred. We'd be remiss, however to not provide support. This example shows a pie plot that displays the same data as the first bar plot above:

![Pie chart of Fatalities by State](https://jtablesaw.github.io/tablesaw/userguide/images/eda/pie.png)

Here's the code:

```java
Plot.show(
    PiePlot.create("fatalities by scale", fatalities1, "scale", "sum [fatalities]")); 
```

#### Pareto Plots

A simple variation on a bar plot is the Pareto Chart. In the plot below, fatality counts are summed by US state, and the results are sorted according to the totals in descending order. The Pareto class handles the sorting for us.  

```Java
Plot.show(
	Pareto.create("Tornado Fatalities by State", fatalities1, "state","sum[fatalities]"));
```

![Pareto of Fatalities by State](https://jtablesaw.github.io/tablesaw/userguide/images/eda/tornado_pareto.png)



