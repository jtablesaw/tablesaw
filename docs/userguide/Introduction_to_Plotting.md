# Introduction to Plotting with Tablesaw

From the beginning, Tablesaw supported plots for quick, basic, exploratory data analysis. It served its purpose, but fell far short of what the best visualization tools provide. Unfortunately, none of those tools are written in Java. 

With version 0.2.0 we introduced a new plotting framework, which provides a Java wrapper around the [Plot.ly](https://github.com/plotly) open source JavaScript visualization library. Plot.ly is based on the extraordinary [D3 (Data-Driven Documents)](https://d3js.org/) framework, and is certainly among the best open-source visualization packages available in any language. It is so good, it has become widely used in languages other than JavaScript such as Python and R, which already had solid options for visualization. 

We've taken a similar approach to these other languages, providing a wrapper that makes it easy to construct plots in pure Java,  We've also ensured that you can render Tablesaw columns without manually converting them to primitive types.  

This is a huge step forward for Tablesaw in many ways as high quality visualization tools are essential in analytical effort. These are some of the most important benefits:

- [Plot.ly](https://github.com/plotly) supports a huge range of visualization types. **Tablesaw already supports** many kinds, including **time-series plots**, **histograms**, **2D histograms**, **box plots**, **line charts**, **scatter plots**, **3D scatterplots**, **bubble plots**, **Pareto charts**, **Pie charts**, and **Bar charts**. 
- We support numerous variations on the plots. For bar charts, for example, we support vertical and horizontal, one-variable, and grouped or stacked charts. 
- Over time, we will add support for many additional types, including geographic maps, tree-plots, network diagrams, heat maps, dedograms, distplots, 3D surfaces, 3D ribbons, wind-rose charts, tertiary plots, 2D density plots, contour plots, tree-maps, violin plots, scatterplot matrices, etc. 
- We provide a single, consistent **Java API** for constructing these plots using builders, type safe enums, and other Java features to minimize the spelling and other issues that are common in working with JavaScript-based tools.
- The plots have a single, consistent, and professional appearance. 
- Each chart is interactive. They render with a common family of interactive tools for saving, printing, panning, zooming, selecting points, etc. 
- The range of supported customizations is enormous, including, fonts, legends, custom axis, spikes, hover effects, and so on. You can almost always get the visualization you want. 
- And, of course, you can use the output in a Web page when you're ready to share your visualizations with the wider world

Please be aware that we don't support the entire plot.ly API. We do, however, support a large portion of it, and we are continuously expanding our support.

## Visualizing data while working in your IDE

The one advantage of the old approach is that you could create graphs easily in your IDE. We've retained this benefit by including a method to render plots without a servlet engine or web server. To do this, we write an output html file to disk, and use your default browser to load it on the desktop. 

## Rendering output to a Web page



Rendering output in a Java UI