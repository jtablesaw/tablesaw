# Introduction to Plotting with Tablesaw

The original plotting code in Tablesaw will soon be deprecated. For doing quick, basic, exploratory visualizations it served its purpose, but it fell far short of what the best visualization tools provide. Unfortunately, none of those tools are written in Java. 

This section describes the new framework, which provides a Java wrapper around the Plot.ly open source JavaScript visualization library. The advantages are huge; these are among the most important:

- It supports a much greater range of visualization types, including time-series, geographic maps, heat maps, 2D histograms, Contour plots, 3D scatterplots, etc. 
- It provides a single, consistent API. With the current approach built on several different tools, this was not possible. 
- It provides a single, consistent, and professional appearance to the rendered plots. Again, using multiple Java libraries made this impossible. 
- Each chart renders with the same set of interactive tools for saving, printing, selecting points, zooming, etc. 
- The range of supported customizations is enormous, including, fonts, legends, custom axis, spikes, hover effects, and on, and on...
- And, of course, you can use the output in a Web page.

The approach we've taken is to provide a Java wrapper that makes it easier to construct plots, using builders and type safe enums where possible to minimize spelling and other issues. We've also ensured that you can render Tablesaw columns without manually converting them to primitive types.  

Please be aware that we don't support the entire plot.ly API. We do, however, support a large portion of it. 

### Visualizing data while working in your IDE

The one advantage of the old approach is that you could create graphs easily in your IDE. We've retained this benefit by including a method to render plots without a servlet engine or web server. To do this, we write an output html file to disk, and use your default browser to load it on the desktop. 

## 