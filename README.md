Tablesaw
=======

[![Apache 2.0](https://img.shields.io/github/license/nebula-plugins/nebula-project-plugin.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Build Status](https://travis-ci.org/jtablesaw/tablesaw.svg?branch=master)](https://travis-ci.org/jtablesaw/tablesaw)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5029f48d00c24f1ea378b090210cf7da)](https://www.codacy.com/app/jtablesaw/tablesaw?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jtablesaw/tablesaw&amp;utm_campaign=Badge_Grade)



#### A note on the new version

> Version 0.20 includes a number of fundamental changes. Any existing code using older versions of Tablesaw will require modifications. We regret the inconvenience that results.
>
> The biggest change is the removal of support for several numeric column types. We now support only double precision floating point columns. We may revert those changes in the future, but for now they let us achieve greater quality and stability. 
>
> The new version also represents a significant improvement in several dimensions. The changes are summarized [here](https://jtablesaw.github.io/tablesaw/changes_in_v_0.2), but it's worth mentioning a couple: 
>
> Overall, interfaces for tables and columns are more consistent, general, powerful, and robust. Test coverage is up from 44% to nearly 70%.  Documentation is a little better, too, although it still has great room for improvement.  Overall, this is a big step forward for Tablesaw. 
>
> Thank you. 

### Overview

__Tablesaw__ is a Java dataframe similar to Pandas in Python, and the R data frame. If you work with data in Java, it may save you time and effort.

Tablesaw also supports descriptive statistics and data visualization. 

You can use tablesaw-core, which is the dataframe library itself, by adding a dependency: 

    <dependency>
        <groupId>tech.tablesaw</groupId>
        <artifactId>tablesaw-core</artifactId>
        <version>0.12</version>
    </dependency>

You can also add a dependency for `tablesaw-plot` to use the plotting capability. Experimental support for plots built on Plot.ly (which is itself built on D3) is also included, but this will be a bumpy ride for another minor release or two.

* We also recommend trying Tablesaw inside [Jupyter notebooks](http://arogozhnikov.github.io/2016/09/10/jupyter-features.html), which lets you experiment with Tablesaw in a more interactive manner. Get started by [installing BeakerX](http://beakerx.com/documentation) and trying [the sample Tablesaw notebook](https://github.com/twosigma/beakerx/blob/master/doc/groovy/Tablesaw.ipynb)

### Documentation and support:

* Please see our documentation page: https://jtablesaw.github.io/tablesaw/ 

### Tablesaw features: 

#### Data processing & transformation
* Import data from RDBMS and CSV files, local or remote (http, S3, etc.)
* Combine files
* Add and remove columns
* Sort, Group, Filter 
* Map/Reduce operations

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

Here's an example where we use [XChart](https://github.com/timmolter/XChart) to map the locations of tornadoes: 
![Alt text](https://jtablesaw.files.wordpress.com/2016/07/tornados3.png?w=809)

If you see something that can be improved, please let us know.
