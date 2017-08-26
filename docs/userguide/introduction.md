# Introduction

The best way to describe Tablesaw is as "__A platform for data science in Java__." This needs some elaboration. 

When we say "_data science_", we mean the array of disciplines, processes, and techniques that are used to discover, 
extract, and apply knowledge from data sets. That includes data wrangling, statistical analysis, machine learning, and
visualization. 

We say "_platform_" because we don't offer a single tool like a dataframe, or a library for machine-learning or 
visualization. Instead we integrate a collection of tools, built around a single datatype: a __Table__. 
Integration is key. What you produce in one step can be used in the next with as little work as possible.

If there is one overriding goal for the project, it is extreme usability. We focus on the task of the 
data scientist/programmer and attempt to provide a frictionless environment for solving data science problems in Java.

## Usability and Performance
One pitfall that has caught other technologies is performance. Poor performance destroys usability in two ways: 
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
If you are familiar with other libraries, you may think of Tablesaw as a dataframe, with the added capabilities 
that you can build statisical models and visualizations directly from the data in the dataframe. 

### What's a dataframe?
A dataframe is traditionally an in-memory, tabular data structure in which each column in the table consists of 
a single type of data, while rows can contain a variety of types. Dataframes facilitate the transformation of their 
data, generally providing several kinds of transformation opeations:

* Importing and exporting data
* Adding and removing columns
* Sorting rows
* Filtering rows
* Creating new columns from the data in existing ones
* Summarizing columns or tables
* Combining tables
* etc.

### What else is there?
Beyond the usual dataframe functionality, Tablesaw also contains a compressed, columnar storage format we call "Saw" files. 
Saw files are much smaller than CSV or other text files, so they consume less disk space and are easier and faster 
to move across a network. They can also be loaded into a dataframe orders of magnitude faster than CSV imports. 

The other major components are interfaces that allow users to create visualizations and machine learning models 
directly from the dataframes. 

### Looking ahead
In the rest of this User Guide we discuss each class of dataframe operation, as well as the visualization and machine learning 
capabilities we offer. But first, here is a sample to wet your appetite. The code below loads a CSV file and creates a  
machine learning model called a RandomForest. And it does it in two lines of code. 

        Table t = Table.read().csv("../data/KNN_Example_1.csv");
        RandomForest model = RandomForest.learn(10, t.shortColumn(2), t.nCol("X"), t.nCol("Y"));

We hope you will find Tablesaw useful in your work.
