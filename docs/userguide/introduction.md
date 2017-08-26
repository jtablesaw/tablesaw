# Introduction

The best way to describe Tablesaw is as "__A platform for data science in Java__." This needs some elaboration. 

When we say "_data science_", we mean the array of disciplines, processes, and techniques used to discover, 
extract, and apply knowledge from data sets. That includes data wrangling, statistical analysis, machine learning, and
visualization. 

We say "_platform_" because we don't offer a single tool like a dataframe, or a library for machine-learning or 
visualization. Instead we integrate a collection of tools, built around the _Table_ datatype. 
Integration is key. What you produce in one step can be used directly in the next.

The one overriding goal for the project is extreme usability. We attempt to provide a frictionless environment for solving data science problems in Java.

## Usability and Performance
Performance is a pitfall that ensnares many data science platforms. Poor performance destroys usability in two ways: 
First, it inhibits the analyst from trying a variety of approaches. If it takes two hours to run a job, 
you're not going to approach the problem as creatively as you would if it took two minutes. 

Worse, you may reach the point where your work needs to be distributed across numerous computing devices. 
There is nothing simple about distributed computing. Leslie Lamport is a Turing Award-winning computer scientist
who dedicated his career to distributed computing. His definition:

> A distributed system is one in which the failure of a computer you didn't even know
> existed can render your own computer unusable. 

Tablesaw is designed for high-performance for the sake of usability. Your work is faster so you can try more things. 
Better still, most people will _never_ need to distribute their work, and nearly all will _rarely_ need to do so. 

## The tools in the toolchest 
If you are familiar with other libraries, you may think of Tablesaw as a dataframe, with the added ability 
to create statisical models and visualizations directly from the data in the dataframe. 

### What's a dataframe?
A dataframe is an in-memory, tabular data structure in which each column in the table consists of 
a single datatype, while rows can contain a variety of types. Dataframes facilitate the transformation of tabular 
data, generally providing several kinds of transformation operations:

* Importing and exporting data
* Adding and removing columns
* Sorting rows
* Filtering rows
* Creating new columns from existing ones
* Summarizing columns or tables
* Combining tables
* and so on.

Even if you're not doing machine learning, if you work with data in Java you may find Tablesaw very useful.

### What else is there?
Beyond the usual dataframe functionality, Tablesaw also contains a compressed, columnar storage format we call "Saw". 
Saw files are much smaller than CSV or other text files, so they consume less disk space and are easier and faster 
to move across a network. They can also be loaded into a dataframe several orders of magnitude faster than the equivalent CSV file. 

The other major components are interfaces for creating visualizations and machine learning models 
directly from the dataframes. 

### Looking ahead
In the rest of this User Guide we discuss each class of dataframe operation, as well as the visualization and machine learning 
capabilities we offer. But first, here is a sample to wet your appetite. The code below loads a CSV file and creates a  
machine learning model called a RandomForest. And it does it in two lines of code. 

        Table t = Table.read().csv("../data/KNN_Example_1.csv");
        RandomForest model = RandomForest.learn(10, t.shortColumn(2), t.nCol("X"), t.nCol("Y"));

We hope you will find Tablesaw useful in your work.
