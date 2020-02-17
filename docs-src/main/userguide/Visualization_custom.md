[Contents](https://jtablesaw.github.io/tablesaw/userguide/toc)

# Creating custom visualizations

## Creating Plots

To take full advantage of plot.ly's power requires a few steps:

- Create a *layout* to define the overall appearance of the plot (optional, but generally useful)
- Create one or more *traces*, that represent the data to plot. In a multi-series plot, each series gets its own trace.
- Put them together in a *figure*.  A figure is plot.ly-speak for a plot. 
- Show the *figure*

Here's a simple example, where we create a ScatterPlot from two numeric columns (colX and colY). A default layout is used because we didn't supply one.

```java
Trace trace = ScatterTrace.builder(colX, colY).build();
Plot.show(new Figure(trace));
```

Not so bad, right?

## Rendering in a Web page

The call to Plot.show() above opens your local browser on the plot. If you prefer to serve your plots with an engine like Jetty, or in a Web framework like Spark Java, you can call the *asJavascript()* method on your *figure*. This returns the JavaScript code as a String. Send it where you will. 

## Some details

### Layouts

A layout controls the appearance of a figure. A simple default layout is supplied if you don't provide one. To create one, call Layout.builder(), and then supply the builder with the options you want. I could easily spend weeks documenting Layout, but his should get you started. When you need more information, you can use the Plot.ly documentation.

#### Basics

The most basic layout features are setting the figure's **title** and the **height** and **width**. 

```java
Layout layout = Layout.builder()
		.title("My Title")
		.height(500)
		.width(650)
		.build();
```

To use this layout, we again construct a figure and (for interactive use) show it:

```java
Trace trace = ScatterTrace.builder(colX, colY).build();
Plot.show(new Figure(layout, trace));
```

This would show the same plot as above, but with a title and different dimensions.

Other simple options include setting **background colors** and **margins**.

```java
Layout layout = Layout.builder()
   		.plotBgColor("blue")
		.paperBgColor("red")
		.build();
```

These attributes change the background color of the plot and the surrounding area (paper). Colors can be specified by name or as hex strings. Hex strings, of course, will let specify colors that aren't so jarring as a blue plot on a red background.

#### Fonts

To change to font for the title, specify a **titleFont**. You create one with a builder

```java
Font f = Font.builder()
		.family("Arial")
		.size("24")
		.color("green")
		.build();
Layout layout = Layout.builder()
		.title("Your plot title here")
		.titleFont(f)
		.build();
```

The same approach we use here to format the plot title is used to set fonts anywhere text is displayed, such as a in a legend, an axis title, for hover text, or in a tick label. 

#### Axes

Custom axes are extremely useful, and the many options are available. 

```java
Axis xAxis = Axis.builder()
		.title("date")
		.build();
```

Besides customizing the axes themselves, the Axis class is used to manage the display of a grid on the plot, and any "spikes" you want to use. *Spikes* are the lines drawn from a point to an axis when you hover over the point. 

##### Grids

A pale grid is displayed by default. You can turn off it for a given axis by calling showGrid(false) on the Axis builder. Here's an example of a custom grid format:

```java
Axis xAxis = Axis.builder()
		.gridColor("grey")
		.gridWidth(1)
		.build();
```

##### Spikes

Here we create vertical spikes. You could reuse the spikes object created in a yAxis to get horizontal spikes as well. The general process of constructing and using spikes is shown below. 

```java
Spikes spikes = Spikes.builder()
		.dash("solid")
		.color("yellow")
		.build();
// put the spikes in the xAxis so we get vertical spikes
Axis xAxis = Axis.builder()
    	.spikes(spikes)
    	.build();
// put the xAxis in the builder
Layout layout = Layout.builder()
    	.xAxis(xAxis)
    	.hoverMode(HoverMode.CLOSEST)
    	.build();
// define your trace
Trace trace = ScatterTrace.builder(xCol, yCol).build();
// put the builder in your figure
Figure plot = new Figure(layout, trace);
```

* **Note** that Spikes only work when the ***HoverMode*** on layout is set to **CLOSEST**. That's what we do above.

##### Ticks

Like most things, there is a good default setting for ticks, so this is only needed if you want to customize your display. When you do, they're also very flexible. Options are available for defining the ticks and labels either by a **range**, or by passing an **array of positions and optional labels**. You can also set their visibility, length, width, color, placement (inside or outside), and many other options. See the **TickSettings** class for details. 

```Java
TickSettings ticks = TickSettings.builder()
    	.color("red")
    	.placement(Placement.OUTSIDE)
    	.build();
Axis yAxis = Axis.builder().tickSettings(ticks).build();
// etc. 
```

## Some sample plots

### Scatters

```java
double[] x = {1, 2, 3, 4, 5, 6};
double[] y = {0, 1, 6, 14, 25, 39};
String[] labels = {"a", "b", "c", "d", "e", "f"};

ScatterTrace trace = ScatterTrace.builder(x, y)
        .text(labels)
        .build();
        
Plot.show(new Figure(trace));
```

### Line Plots

You can make the example above a line plot by adding a *mode* to the trace.

```Java
ScatterTrace trace = ScatterTrace.builder(x, y)
    .mode(ScatterTrace.Mode.LINE)
    .text(labels)
    .build();
```



### Histograms

```java
double[] y = {1, 4, 9, 16, 11, 4, -1, 20, 4, 7, 9, 12, 8, 6};

HistogramTrace trace = HistogramTrace.builder(y).build();
Plot.show(new Figure(trace));
```

Note that you can overlay two histograms by adding another trace, setting opacity, and providing a simple layout:

```Java
double[] y1 = {1, 4, 9, 16, 11, 4, 0, 20, 4, 7, 9, 12, 8, 6, 28, 12};
double[] y2 = {3, 11, 19, 14, 11, 14, 5, 24, -4, 10, 15, 6, 5, 18};

HistogramTrace trace1 = 	
    HistogramTrace.builder(y1).opacity(.75).build();
HistogramTrace trace2 =
    HistogramTrace.builder(y2).opacity(.75).build();

Layout layout  = Layout.builder()
    .barMode(Layout.BarMode.OVERLAY)
    .build();
Plot.show(new Figure(layout, trace1, trace2));
```

### 2-D Histograms



### 3-D Scatters



### Bar plots

```java
Object[] x = {"sheep", "cows", "fish", "tree sloths"};
double[] y = {1, 4, 9, 16};

BarTrace trace = BarTrace.builder(x, y).build();
Plot.show(new Figure(trace));
```

To render the above plot horizontally, we modify the trace slightly:

```
BarTrace trace = BarTrace.builder(x, y)
	.orientation(BarTrace.Orientation.HORIZONTAL)
	.build();
```

### Box plots

```JavaScript
Object[] x = {"sheep", "cows", "fish", "tree sloths", 
	"sheep", "cows", "fish", "tree sloths", 
	"sheep", "cows", "fish", "tree sloths"};
double[] y = {1, 4, 9, 16, 3, 6, 8, 8, 2, 4, 7, 11};

BoxTrace trace = BoxTrace.builder(x, y).build();
Plot.show(new Figure(trace));
```