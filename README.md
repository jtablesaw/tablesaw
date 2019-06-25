Tablesaw
=======

[![Apache 2.0](https://img.shields.io/github/license/nebula-plugins/nebula-project-plugin.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Build Status](https://travis-ci.org/jtablesaw/tablesaw.svg?branch=master)](https://travis-ci.org/jtablesaw/tablesaw)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5029f48d00c24f1ea378b090210cf7da)](https://www.codacy.com/app/jtablesaw/tablesaw?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jtablesaw/tablesaw&amp;utm_campaign=Badge_Grade)

### Overview

__Tablesaw__ is Java for data science. It includes a dataframe and a visualization library, as well as utilities for loading, transforming, filtering, and summarizing data. It's fast and careful with memory. If you work with data in Java, it may save you time and effort. Tablesaw also supports descriptive statistics and integrates well with the Smile machine learning library. 

### Tablesaw features

#### Data processing & transformation
* Import data from RDBMS, Excel, CSV, JSON, HTML, or Fixed Width text files, whether they are local or remote (http, S3, etc.)
* Export data to CSV, JSON, HTML or Fixed Width files. 
* Combine tables by appending or joining
* Add and remove columns or rows
* Sort, Group, Query 
* Map/Reduce operations
* Handle missing values

#### Visualization

Tablesaw supports data visualization by providing a wrapper for the Plot.ly JavaScript plotting library. Here are a few examples of the new library in action.

| ![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/box1.png) | ![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/scatter_2_Yaxes.png) | ![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/tornado.scatter.png) |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/bush_time_series2.png) | ![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/hist_overlay.png) | ![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/histogram2.png) |
| ![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/histogram2d.png) | ![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/pie.png) | ![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/wine_bubble_3d.png) |
| ![](https://jtablesaw.github.io/tablesaw/userguide/images/eda/wine_bubble_with_groups.png) | ![](https://jtablesaw.github.io/tablesaw/userguide/images/eda/robberies_area.png) | ![](https://jtablesaw.github.io/tablesaw/userguide/images/ml/regression/wins%20by%20year.png) |
| ![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/bush_heatmap1.png) | ![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/tornado_bar_groups.png) | ![Tornadoes](https://jtablesaw.github.io/tablesaw/userguide/images/eda/ohlc1.png) |

#### Statistics

* Descriptive stats: mean, min, max, median, sum, product, standard deviation, variance, percentiles, geometric mean, skewness, kurtosis, etc.

### Getting started

Add tablesaw-core to your project. You can find the version number for the latest release in the [release notes](https://github.com/jtablesaw/tablesaw/releases): 

```xml
<dependency>
    <groupId>tech.tablesaw</groupId>
    <artifactId>tablesaw-core</artifactId>
    <version>VERSION_NUMBER_GOES_HERE</version>
</dependency>
```

You may also add supporting projects:
- `tablesaw-beakerx` - for using Tablesaw inside [BeakerX](http://beakerx.com/)
- `tablesaw-excel` - for using Excel workbooks
- `tablesaw-html` - for using HTML
- `tablesaw-json` - for using JSON
- `tablesaw-jsplot` - for creating charts

### Documentation and support

* Start here:  https://jtablesaw.github.io/tablesaw/gettingstarted
* Then see our documentation page: https://jtablesaw.github.io/tablesaw/ and the [Tablesaw User Guide](https://jtablesaw.github.io/tablesaw/userguide/toc).

And *always* feel free to ask questions or make suggestions here on the [issues tab](https://github.com/jtablesaw/tablesaw/issues). 

### Integrations

* We recommend trying Tablesaw inside [Jupyter notebooks](http://arogozhnikov.github.io/2016/09/10/jupyter-features.html), which lets you experiment with Tablesaw in a more interactive manner. Get started by [installing BeakerX](http://beakerx.com/documentation) and trying [the sample Tablesaw notebook](https://github.com/twosigma/beakerx/blob/master/doc/groovy/Tablesaw.ipynb)
* You may utilize Tablesaw with many machine learning libraries. To see an example of using Tablesaw with [Smile](https://haifengl.github.io/smile/) check out [the sample Tablesaw Jupyter notebook](https://github.com/twosigma/beakerx/blob/master/doc/groovy/Tablesaw.ipynb) 
* You may use [quandl4j-tablesaw](http://quandl4j.org) if you'd like to load financial and economic data from [Quandl](https://www.quandl.com) into Tablesaw. This is demonstrated in [the sample Tablesaw notebook](https://github.com/twosigma/beakerx/blob/master/doc/groovy/Tablesaw.ipynb) as well
