# Plotting 

We're retiring the existing plotting code in Tablesaw. It will be deprecated in the .20 release. For doing quick visualizations the plotting library served its purpose, but it fell far short of what the best visualization tools provide. Unfortunately, none of those tools are written in Java. 

We are never-the-less switching to a new framework, which provides a Java wrapper around the Plot.ly open source Javascript visualization library. The advantages are huge; these are among the most important:

- It supports a much greater range of visualization types, including real time-series, geographic maps, area charts, heat maps, 2D histograms, Contour plots, 3D scatterplots, etc. 
- It provides a single, consistent API. With the current approach based on multiple supporting libraries this was not possible. 
- It provides a single, consistent, and professional appearance to the rendered plots. 
- Each chart renders with the same interactive tools for saving, printing, selecting points, zooming, etc. 
- The range of supported customizations is enormous, including, fonts, legends, custom axis, spikes, hover effects, and on, and on...
- And, of course, you can use the output in a Web page.

The approach we've taken is to provide a Java wrapper that makes it easier to construct plots, providing builders and treating options as type safe enums, for example. We've also ensured that you can render Tablesaw columns without manually converting them to primitive types.  

## Creating Plots

To take advantage of all these options, we had to sacrifice some of the simplicity of the older interface. To create a plot, you have follow a few steps.

- Create a layout (sometimes optional, but generally useful)
- Create one or more "traces", that represent the data to plot
- Put them together in a filter
- Show the filter

Here's a very simple example, where we create a ScatterPlot from two numeric columns (colX and colY). A default layout is used because we didn't supply one.

```java
ScatterTrace trace = ScatterTrace.builder(colX, colY).build();
Plot.show(new Figure(trace));
```

## Working in the UI

We've included a method for rendering plots from within an IDE, without serving from a web page. To do this, we write the output file to disk, and display it in the default browser on the desktop. 

## Rendering in a Web page

If you prefer to embed the plots in HTML pages, and serve them from a server like Jetty, use them in a Java Web framework like Spark, you can.  Call the asJavascript() method on your figures. This returns the JavaScript text. Send it where you will. 

## Some details

### Layouts

A layout controls the appearance of a plot. A simple default layout is supplied if you don't provide one. To create one, call Layout.builder(), and then supply the builder with the data you want. 

#### Basics

The most basic layout features are setting the plot title and the height and width. 

```java
Layout layout = Layout.builder()
		.title("My Title")
		.height(500)
		.width(650)
		.build();
```

Some other simple options include setting colors and margins.

```java
Layout layout = Layout.builder()
		.paperBgColor("red")
		.plotBgColor("blue")
		.build();
```

These attributes change the color of the plot and the surrounding area (paper). Colors can be specified by name or as hex strings.

#### Fonts

To change to font for the title, specify a titleFont. You create one with a builder

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

The same approach is used to set fonts where text is displayed, such as a legend, an axis title, or for hover text. 

#### Axes

Custom axes are extremely useful, and the options available are many. 

```java
Axis xAxis = Axis.builder()
		.title("date")
		.build();
```



Besides customizing the axes themselves, the Axis class is used to manage the display of a grid on the plot, and any "spikes" you want to use. Spikes are the lines drawn from a point to an axis when you hover over the point. 

##### Grids

A pale grid is displayed by default. You can turn off it for a given axis by calling showGrid(false) on the Axis builder. Here's an example of a custom grid format:

```java
Axis xAxis = Axis.builder()
		.gridColor("grey")
		.gridWidth(1)
		.build();
```

##### Spikes

```java
Spikes spikes = Spikes.builder()
		.dash("solid")
		.color("yellow")
		.build();
// put the spikes in the xAxis so we get vertical spikes
Axis xAxis = Axis.builder()
    	.spikes(spikes)
    	.build();
// put the axis in the builder
Layout layout = Layout.builder()
    	.xAxis()
    	.hoverMode(HoverMode.CLOSEST)
    	.build();
```

Note that Spikes only work when the *HoverMode* on layout is set to CLOSEST. That's what we do above.