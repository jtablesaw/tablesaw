Tablesaw
=======

[![Apache 2.0](https://img.shields.io/github/license/nebula-plugins/nebula-project-plugin.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Build Status](https://travis-ci.org/jtablesaw/tablesaw.svg?branch=master)](https://travis-ci.org/jtablesaw/tablesaw)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/3ebd154b5253466b932cb17dda737293)](https://www.codacy.com/gh/jtablesaw/tablesaw/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jtablesaw/tablesaw&amp;utm_campaign=Badge_Grade)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=jtablesaw_tablesaw&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=jtablesaw_tablesaw)

### Overview

__Tablesaw__ is a dataframe and visualization library that supports loading, cleaning, transforming, filtering, and summarizing data. If you work with data in Java, it may save you time and effort. Tablesaw also supports descriptive statistics and can be used to prepare data for working with machine learning libraries like Smile, Tribuo, H20.ai, DL4J.

### Tablesaw features

#### Data processing & transformation
* Import data from RDBMS, Excel, CSV, TSV, JSON, HTML, or Fixed Width text files, whether they are local or remote (http, S3, etc.)
* Export data to CSV, JSON, HTML or Fixed Width files.
* Combine tables by appending or joining
* Add and remove columns or rows
* Sort, Group, Filter, Edit, Transpose, etc.
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

External supporting projects - **outside of this organization**:
- [tablesaw-parquet](https://github.com/tlabs-data/tablesaw-parquet) - for using the [Apache Parquet](https://parquet.apache.org/) file format with Tablesaw ([report issue](https://github.com/tlabs-data/tablesaw-parquet/issues))

### Documentation and support

* Start here:  https://jtablesaw.github.io/tablesaw/gettingstarted
* Then see our documentation page: https://jtablesaw.github.io/tablesaw/ and the [Tablesaw User Guide](https://jtablesaw.github.io/tablesaw/userguide/toc).
* Ask questions, make suggestions, or tell us how you're using Tablesaw in the new GitHub [discussions forum](https://github.com/jtablesaw/tablesaw/discussions). 
* Feature requests and bug reports can be made on the [issues tab](https://github.com/jtablesaw/tablesaw/issues).

### Integrations

#### Jupyter Notebooks

* We recommend trying Tablesaw inside [Jupyter notebooks](http://arogozhnikov.github.io/2016/09/10/jupyter-features.html), which lets you experiment with Tablesaw in a more interactive manner. Get started by [installing BeakerX](http://beakerx.com/documentation) and trying [the sample Tablesaw notebook](https://github.com/twosigma/beakerx/blob/master/doc/groovy/Tablesaw.ipynb)
* A second way to use Tablesaw inside [Jupyter notebooks](http://arogozhnikov.github.io/2016/09/10/jupyter-features.html) is with [IJava](https://github.com/SpencerPark/IJava), which has built-in support for Tablesaw. Gary Sharpe has written [an excellent tutorial](https://medium.com/@gmsharpe/java-jupyter-plotly-e1bbaa7f2be8) that shows you how to use Tablesaw plots. Gary has written a number of other tutorials that feature Tablesaw:
  * [Tidy Data with Java & Jupyter](https://medium.com/@gmsharpe/tidy-data-with-java-jupyter-b1e131b37ab0)
  * [Dataframes with Tablesaw — JSON](https://medium.com/@gmsharpe/dataframes-with-tablesaw-json-46dda9c8c217?source=your_stories_page----------------------------------------)
  * [Dataframes with Tablesaw — CSV Files](https://medium.com/@gmsharpe/importing-data-with-tablesaw-part-1-csv-files-3ac6f135cf6f?source=your_stories_page----------------------------------------)
* A third approach is to use [Google Colab](https://colab.research.google.com). Again, Gary Sharpe has an excellent tutorial:[Getting Started with Dataframes using Java and Google Colab](https://medium.com/@gmsharpe/getting-started-with-tablesaw-and-google-colab-65ef0cbe280c)

#### Other integrations

* Eclipse uses may find [etablesaw](https://github.com/hallvard/etablesaw) useful. It provides Eclipse integration aimed at turning Eclipse into a data workbench.   
* You may utilize Tablesaw with many machine learning libraries. To see an example of using Tablesaw with [Smile](https://haifengl.github.io) check out [the sample Tablesaw Jupyter notebook](https://github.com/twosigma/beakerx/blob/master/doc/groovy/Tablesaw.ipynb)
* You may use [quandl4j-tablesaw](http://quandl4j.org) if you'd like to load financial and economic data from [Quandl](https://www.quandl.com) into Tablesaw. This is demonstrated in [the sample Tablesaw notebook](https://github.com/twosigma/beakerx/blob/master/doc/groovy/Tablesaw.ipynb) as well
