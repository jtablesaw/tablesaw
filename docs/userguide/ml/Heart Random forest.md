# Random Forest with Smile & Tablesaw

While linear regression analysis (introduced in the <a href="https://jtablesaw.github.io/tablesaw/userguide/ml/Moneyball%20Linear%20regression">Moneyball tutorial</a>) is widely used and works well for a variety of problems, tree-based models provide excellent results and be applied to datasets with both numerical and categorical features, without making any assumption of linearity. In addition, tree based methods can be used effectively for regression and classification tasks. 
 
This tutorial is based on Chapter 8 (Tree Based Methods) of the widely used and freely available textbook <a href="https://www.statlearning.com/">An Introduction to Statistical Learning, Second Edition</a>.

### Basic Decision Trees

The basic idea of a decision tree is simple: use a defined, algorithmic approach to partition the feature space of a dataset into regions in order to make a prediction. For a regression task, this prediction is simply the mean of the response variable for all training observations in the region. **In any decision tree, every datapoint that falls within the same region receives the same prediction.**

The following figure illistrates a two-dimensional feature space that has been partitioned according to a greedy algorithm that minimizes the residual sum of squares. 





