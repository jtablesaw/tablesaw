# Plotting 

We're retiring the existing plotting code in Tablesaw. It will be deprecated in the .20 release. For doing quick, basic, exploratory visualizations the plotting library served its purpose, but it fell far short of what the best visualization tools provide. Unfortunately, none of those tools are written in Java. 

And so, we're switching to a new framework, which provides a Java wrapper around the Plot.ly open source Javascript visualization library. The advantages are huge; these are among the most important:

- It supports a much greater range of visualization types, including time-series, geographic maps, heat maps, 2D histograms, Contour plots, 3D scatterplots, etc. 
- It provides a single, consistent API. With the current approach built on several different tools, this was not possible. 
- It provides a single, consistent, and professional appearance to the rendered plots. 
- Each chart renders with the same set of interactive tools for saving, printing, selecting points, zooming, etc. 
- The range of supported customizations is enormous, including, fonts, legends, custom axis, spikes, hover effects, and on, and on...
- And, of course, you can use the output in a Web page.

The approach we've taken is to provide a Java wrapper that makes it easier to construct plots, using builders and type safe enums where possible to minimize spelling and other issues. We've also ensured that you can render Tablesaw columns without manually converting them to primitive types.  

### Working in the UI

The one advantage of the old approach is that you could create graphs easily in your IDE. We've retained this benefit by including a method for rendering plots without a servlet engine or web server. To do this, we write an output html file to disk, and use your default browser to load it on the desktop. 

## Creating Plots

To take advantage of plot.ly's power, we had to sacrifice some of the simplicity of the older interface. Creating a plot now requires a few steps:

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

The call to Plot.show() above opens your local browser on the plot. If you prefer to serve your plots with an engine like Jetty, or in a Web framework like Spark Java, you can.  Call the *asJavascript()* method on your *figure*. This returns the JavaScript as a String. Send it where you will. 

## Some details

### Layouts

A layout controls the appearance of a figure. A simple default layout is supplied if you don't provide one. To create one, call Layout.builder(), and then supply the builder with the options you want. I could easily spend weeks documenting Layout. This should get you started. 

#### Basics

The most basic layout features are setting the figure's **title** and the **height** and **width**. 

```java
Layout layout = Layout.builder()
		.title("My Title")
		.height(500)
		.width(650)
		.build();
```

Other simple options include setting **background colors** and **margins**.

```java
Layout layout = Layout.builder()
   		.plotBgColor("blue")
		.paperBgColor("red")
		.build();
```

These attributes change the background color of the plot and the surrounding area (paper). Colors can be specified by name or as hex strings.

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

The same approach is used to set fonts where text is displayed, such as a in a legend, an axis title, for hover text, or in a tick label. 

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

## Rendering specific kinds of plots

Initially, we provide support for Scatters (including Line Plots and Bubble Plots), Boxes, Bars (horizontal and vertical), Pies, and Histograms. Partial support is available for 3D Scatters, and 2D Histograms, 

See the examples package for code creating various types of plots. Many more are possible, but have yet to be 'wrapped'.