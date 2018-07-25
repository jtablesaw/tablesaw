# 2D and 3D Scatter Plots and Bubble Plots

Scatter plots are among the most ubiquitous and most useful visualization options. We will use a wine dataset to demonstrate, starting with a simple scatter plotting California champagne vintages against prices. You can show complex relationships between up to five variables (four numeric and one categorical).

First we get our dataset and filter it.

```java
Table restaurants = Table.read().csv("wine_test.csv");

Table champagne =
    wines.where(
    	wines.stringColumn("wine type").isEqualTo("Champagne & Sparkling")
    		.and(wines.stringColumn("region").isEqualTo("California")));
```

The plotting code is straightforward. This line creates and displays the plot.

```Java
ScatterPlot.show("Champagne prices by vintage", champagne, "mean retail", "vintage");
```

![](https://jtablesaw.github.io/tablesaw/userguide/images/eda/wine_simple_scatter.png)

Adding a categorical column to the plot produces a series for each category.

```Java
ScatterPlot.show(
    "Wine prices and ratings", wines, "Mean Retail", "highest pro score", "wine type");
```

![](https://jtablesaw.github.io/tablesaw/userguide/images/eda/wine_category_scatter.png)

To plot three numeric variables we can use a bubble plot or a 3D scatter. First we'll use the bubble 

```Java
BubblePlot.show("Average retail price for champagnes by vintage and rating",
                champagne,				// table
                "highest pro score",  	// x
                "vintage", 				// y
                "Mean Retail"); 		// bubble size
```

![](https://jtablesaw.github.io/tablesaw/userguide/images/eda/wine_simple_bubble.png)

The size of the bubble is given by the last column "mean retail." By default, values are mapped to the diameter of the bubble, but it's possible to use area when creating a custom scatter plot.  

To represent the same data in a 3D Scatter, you would use the Scatter3DPlot instead of BubblePlot. The rest is the same. The bubble size variable is represented on the z axis now:

```Java
Scatter3DPlot.show("Average retail price for champagnes by vintage and rating",
                champagne,				// table
                "highest pro score",  	// x
                "vintage", 				// y
                "Mean Retail"); 		// z 
```

![](https://jtablesaw.github.io/tablesaw/userguide/images/eda/wine_simple_3dScatter.png)

We can't show it here, but these plots are rotatable in 3D space, as well as supporting panning and zooming like 2D plots. Hovering produces a label, and draws a box connecting the highlighted point to the three axes. 

We can add a categorical variable to either the Bubble or the 3D scatter. First we'll show the bubble version.

```Java
BubblePlot.show("Average retail price for champagnes by vintage and rating",
                champagne,
                "highest pro score",
                "vintage",
                "Mean Retail",
                "appellation");
```

The grouping column is added to the end of the method. The result is shown below.

![](https://jtablesaw.github.io/tablesaw/userguide/images/eda/wine_bubble_with_groups.png)

Let's see the same four variables using a 3D scatter. First the code, and then the plot:

```Java
Scatter3DPlot.show("Average retail price for champagnes by vintage and rating",
                champagne,
                "highest pro score",
                "vintage",
                "Mean Retail",
                "appellation");
```

![](https://jtablesaw.github.io/tablesaw/userguide/images/eda/wine_category_3dScatter.png)

Finally, we take one step further, adding another numeric variable to the categorical 3D scatter plot above. As with the 2D scatter, we use point size for the new numeric variable. 

```Java
Scatter3DPlot.show("Highest & lowest retail price for champagnes by vintage and rating",
                champagne,
                "highest pro score",
                "vintage",
                "highest retail",
                "lowest retail",
                "appellation");
```

![](https://jtablesaw.github.io/tablesaw/userguide/images/eda/wine_bubble_3d.png)