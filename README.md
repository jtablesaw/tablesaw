Tablesaw
=======

[![Apache 2.0](https://img.shields.io/github/license/nebula-plugins/nebula-project-plugin.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Build Status](https://travis-ci.org/jtablesaw/tablesaw.svg?branch=master)](https://travis-ci.org/jtablesaw/tablesaw)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5029f48d00c24f1ea378b090210cf7da)](https://www.codacy.com/app/jtablesaw/tablesaw?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jtablesaw/tablesaw&amp;utm_campaign=Badge_Grade)

### Overview

__Tablesaw__ is the shortest path to data science in Java. It includes a dataframe, an embedded column-store, and hundreds of methods to transform, summarize, or filter data similar to the Pandas dataframe and R data frame. If you work with data in Java, it will probably save you time and effort.

Tablesaw also supports descriptive statistics, data visualization, and machine learning. And it scales: You can munge a 1/2 billion rows on a laptop and over 2 billion records on a server. 

There are other, more elaborate platforms for data science in Java. They're designed to work with vast amounts of data, and  require a huge stack and a vast amount of effort.

You can include tablesaw-core, which is the dataframe library itself, with: 

    <dependency>
        <groupId>tech.tablesaw</groupId>
        <artifactId>tablesaw-core</artifactId>
        <version>0.11.6</version>
    </dependency>

You may also add dependencies for `tablesaw-plot` to use the plotting capability and `tablesaw-smile` to use the [Smile](https://github.com/haifengl/smile) machine learning integration.

### Documentation and support:

* Please see our documentation page: https://jtablesaw.github.io/tablesaw/ 
* We also recommend trying Tablesaw inside [Jupyter notebooks](http://arogozhnikov.github.io/2016/09/10/jupyter-features.html), which lets you experiment with Tablesaw in a more interactive manner. Get started by [installing BeakerX](http://beakerx.com/documentation) and trying [the sample Tablesaw notebook](https://github.com/twosigma/beakerx/blob/master/doc/groovy/Tablesaw.ipynb)

### Tablesaw features: 

#### Data processing & transformation
* Import data from RDBMS and CSV files, local or remote (http, S3, etc.)
* Combine files
* Add and remove columns
* Sort, Group, Filter 
* Map/Reduce operations
* Store tables in a fast, compressed columnar storage format

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
