Tablesaw
=======

[![Apache 2.0](https://img.shields.io/github/license/nebula-plugins/nebula-project-plugin.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Build Status](https://travis-ci.org/jtablesaw/tablesaw.svg?branch=master)](https://travis-ci.org/jtablesaw/tablesaw)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5029f48d00c24f1ea378b090210cf7da)](https://www.codacy.com/app/jtablesaw/tablesaw?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jtablesaw/tablesaw&amp;utm_campaign=Badge_Grade)



### Overview

__Tablesaw__ is a Java dataframe similar to Pandas in Python, and the R data frame. If you work with data in Java, it may save you time and effort.

Tablesaw also supports descriptive statistics and data visualization. It integrates well with the Java machine learning library, Smile. 

You can use tablesaw-core, which is the dataframe library itself, by adding a dependency: 

```xml
<dependency>
    <groupId>tech.tablesaw</groupId>
    <artifactId>tablesaw-core</artifactId>
    <version>0.21.0</version>
</dependency>
```

Beyond tablesaw-core:

- You can add a dependency for `tablesaw-plot` to use the plotting capability. 
- Experimental support for JavaScript plots built on Plot.ly (which is itself built on D3) is also included, but this will be a bumpy ride for another minor release or two. Add a dependency for ```tablesaw-jsplot```.
- We also recommend trying Tablesaw inside [Jupyter notebooks](http://arogozhnikov.github.io/2016/09/10/jupyter-features.html), which lets you experiment with Tablesaw in a more interactive manner. Get started by [installing BeakerX](http://beakerx.com/documentation) and trying [the sample Tablesaw notebook](https://github.com/twosigma/beakerx/blob/master/doc/groovy/Tablesaw.ipynb)

* Finally, the [jtablesaw smile project](https://github.com/jtablesaw/smile) contains integration code to make it trivial to use the excellent Java machine learning library [Smile](https://github.com/haifengl/smile) with tablesaw. 

### Documentation and support:

* Please see our documentation page: https://jtablesaw.github.io/tablesaw/ 
* Jump right in with this guide:  https://jtablesaw.github.io/tablesaw/gettingstarted 

### Tablesaw features: 

#### Data processing & transformation
* Import data from RDBMS and CSV files, local or remote (http, S3, etc.)
* Combine tables by appending or joining
* Add and remove columns or rows
* Sort, Group, Filter 
* Map/Reduce operations
* Handle missing values

#### Statistics 
* Descriptive stats: mean, min, max, median, sum, product, standard deviation, variance, percentiles, geometric mean, skewness, kurtosis, etc.

#### Visualization
* Scatter plots
* Line plots
* Vertical and Horizontal Bar charts
* Histograms 
* Box plots
* Quantile Plots
* Pareto Charts

Here's an example where we use JavaScript plotting support to map the locations of tornadoes: 

![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/tornado.scatter.png) 

If you see something that can be improved, please let us know.
