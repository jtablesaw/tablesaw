# Machine Learning

## What is ML?
Machine learning is one of a group of synonomous or near synonomous names (like, data mining, or predictive modeling)
applied to an approach to building mathematical models from data. 

There are, broadly speaking, two kinds. One kind is called "supervised" learning, and the other "unsupervised". With either we have a dataset, organized in rows and columns, where each row represents a single "case" in the domain of interest. Some people refer to this as a "tidy" dataset, and often some work is required to get your data into tidy format. Each column contains a value representing an property of the case. These properties are potential inputs into our model. It's our job to decide whether to use them, and how.

### Supervised Methods
Supervised methods typically use a "training" dataset to build the model, and a "test" dataset to evaluate it. Both contain a column that holds the known values for the property we want to predict. It would be reasonable to ask what is the value of building a model when we already have the predictions. The answer is, of course, that we want to make predictions for cases we haven't seen yet, that are not in our dataset, or for which the predicted value is simply not known. Often, these are cases that we will see in the future, like next weeks sales records.

The two most important kinds of supervised methods are Regression and Classification. We use regression when we want to predict the value of a numeric variable, like the market value of a house. We use classification when we have a set of classes, like Gender, and we want to know whether a given case represents a male or female.

### Unsupervised Methods
Unsupervised methods have no known or knowable value to predict. The applications of unsupervised methods are even more varied than supr

The main kinds handled in Tablesaw today are Clustering, Association analysis, and Dimensionality Reduction.


