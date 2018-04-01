# Introduction

Tablesaw combines tools for working with tables and columns with the ability to create statistical models and visualizations. 

### What's a dataframe?
A dataframe is an in-memory, tabular data structure in which each column holds a single datatype, 
while rows can contain a variety of types. Tablesaw provide these operations:

* Importing and exporting data from text files and databases
* Adding and removing columns
* Sorting
* Filtering
* Creating new columns by applying functions to existing ones (mapping) 
* Summarizing columns or tables (reducing)
* Combining tables
* Descriptive statistics
* Plotting data
* Creating Machine learning models directly from the dataframe. 

### Looking ahead
In the rest of this User Guide we discuss each class of dataframe operation, as well as the visualization and machine learning 
capabilities we offer. But first, here's a sample. This code loads a CSV file and creates a machine learning classifier
 called a RandomForest. And it does it in two lines of code. 

````java
Table t = Table.read().csv("../data/KNN_Example_1.csv");
RandomForest model = RandomForest.learn(10, t.nCol(2), t.nCol("X"), t.nCol("Y"));
````
