Tablesaw
=======

[![Apache 2.0](https://img.shields.io/github/license/nebula-plugins/nebula-project-plugin.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Build Status](https://travis-ci.org/jtablesaw/tablesaw.svg?branch=master)](https://travis-ci.org/jtablesaw/tablesaw)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5029f48d00c24f1ea378b090210cf7da)](https://www.codacy.com/app/jtablesaw/tablesaw?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jtablesaw/tablesaw&amp;utm_campaign=Badge_Grade)



### Overview

__Tablesaw__ is a Java dataframe similar to Pandas in Python, and the R data frame. If you work with data in Java, it may save you time and effort.

Tablesaw also supports descriptive statistics and data visualization, and integrates well with the Java machine learning library, Smile. 

### Installing

You can use tablesaw-core, which is the dataframe library itself, by adding a dependency: 

```xml
<dependency>
    <groupId>tech.tablesaw</groupId>
    <artifactId>tablesaw-core</artifactId>
    <version>0.24.1</version>
</dependency>
```

- You can add a dependency for `tablesaw-jsplot` to use the new plotting library. This library is built on the Plot.ly JavaScript library (which is itself built on D3). Documentation can be found in the [Tablesaw user guide](https://jtablesaw.github.io/tablesaw/userguide/Introduction to Plotting).
- The older ```tablesaw-plot``` library is now deprecated. In the near future it will be moved from the main tablesaw repo to a separate repo.

### Documentation and support:

* Start here:  https://jtablesaw.github.io/tablesaw/gettingstarted

* Then see our documentation page: https://jtablesaw.github.io/tablesaw/ and [user-guide](https://jtablesaw.github.io/tablesaw/userguide/toc).

And *always* feel free to ask questions here on the [issues tab](https://github.com/jtablesaw/tablesaw/issues). 

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

Tablesaw has replaced it's original plotting API with a new library based on the Plot.ly JavaScript plotting library. The new library is far more powerful and flexible than the old (now deprecated version).

Here are a few examples of the new library in action.

| ![box](https://github.com/jtablesaw/tablesaw/blob/master/docs/userguide/images/eda/box1.png) | ![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/ml/regression/run%20diff%20vs%20wins.png) | ![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/tornado.scatter.png) |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/bush_time_series2.png) | ![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/fatalities_by_scale.png) | ![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/histogram2.png) |
| ![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/histogram2d.png) | ![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/pie.png) | ![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/wine_bubble_3d.png) |
| ![](https://jtablesaw.github.io/tablesaw/userguide/images/eda/wine_bubble_with_groups.png) | ![](https://jtablesaw.github.io/tablesaw/userguide/images/eda/robberies_area.png) | ![](https://jtablesaw.github.io/tablesaw/userguide/images/ml/regression/wins%20by%20year.png) |

### Integrations

- We recommend trying Tablesaw inside [Jupyter notebooks](http://arogozhnikov.github.io/2016/09/10/jupyter-features.html), which lets you experiment with Tablesaw in a more interactive manner. Get started by [installing BeakerX](http://beakerx.com/documentation) and trying [the sample Tablesaw notebook](https://github.com/twosigma/beakerx/blob/master/doc/groovy/Tablesaw.ipynb)
- You may utilize Tablesaw with many machine learning libraries. To see an example of using Tablesaw with [Smile](https://haifengl.github.io/smile/) check out [the sample Tablesaw Jupyter notebook](https://github.com/twosigma/beakerx/blob/master/doc/groovy/Tablesaw.ipynb) 
- You may use [quandl4j-tablesaw](http://quandl4j.org) if you'd like to load financial and economic data from [Quandl](https://www.quandl.com) into Tablesaw. This is demonstrated in [the sample Tablesaw notebook](https://github.com/twosigma/beakerx/blob/master/doc/groovy/Tablesaw.ipynb) as well

### Suggestions

If you see something that can be improved, please let us know.
