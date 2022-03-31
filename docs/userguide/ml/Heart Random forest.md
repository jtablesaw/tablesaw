# Random Forest with Smile & Tablesaw

While linear regression analysis (introduced in the <a href="https://jtablesaw.github.io/tablesaw/userguide/ml/Moneyball%20Linear%20regression">Moneyball tutorial</a>) is widely used and works well for a variety of problems, tree-based models provide excellent results and be applied to datasets with both numerical and categorical features, without making any assumption of linearity. In addition, tree based methods can be used effectively for regression and classification tasks. 
 
This tutorial is based on Chapter 8 (Tree Based Methods) of the widely used and freely available textbook <a href="https://www.statlearning.com/">An Introduction to Statistical Learning, Second Edition</a>.

### Basic Decision Trees

The basic idea of a decision tree is simple: use a defined, algorithmic approach to partition the feature space of a dataset into regions in order to make a prediction. For a regression task, this prediction is simply the mean of the response variable for all training observations in the region. **In any decision tree, every datapoint that falls within the same region receives the same prediction.**

The following figure illistrates a two-dimensional feature space that has been partitioned according to a greedy algorithm that minimizes the residual sum of squares. 

<p align="center">
<img src="https://github.com/jbsooter/tablesaw/blob/6856ae9e92648ad1dfbb62f1ff09e744eebe4154/docs/userguide/images/ml/random_forest/Decision_Tree_8.1.jpg" width="300" height = "300"><img src="https://github.com/jbsooter/tablesaw/blob/6856ae9e92648ad1dfbb62f1ff09e744eebe4154/docs/userguide/images/ml/random_forest/Decision_Tree_8.2.jpg" width="300" height = "300">
</p>

<p align="center">
<sub>Credit: An Introduction to Statistical Learning, Second Edition Figures 8.1 and 8.2</sub>
</p>

As you can see, the feature space is split into three regions, and three predictions are possible: 5.00, 6.00, and 6.74. 

While decision trees are easily interpretable, especially when looking at the visual of the tree on the left, basic decision trees like this one are generally less accurate than other regression/classification approaches. More advanced tree based methods, however, not only compete with other approaches, but often provide exceptional performance. 

### Random Forest

Random Forest is an **ensemble method** that builds on basic decision trees to form a model that is the composition of a large number of individual decision trees. In a Random Forest model, *n* decision trees are grown independently, with each tree considering only a subset of of predictors *m*, using only a subset of the training data. Becuase the majority of the predictors are not considered in each individual tree, the predicitons of each individual tree are decorrelated and therefore the average decision of all the trees in the model will have lower variance and be of greater predictive value. The greedy algorithm used to construct decision trees is heavily influenced by strong predictors, so by excluding a number of predictors when each tree in the Random Forest is grown, the model can explore the predictive value of other features that previously may have been largely ignored. 

### Implementing Random Forest for the Heart dataset using Tablesaw + Smile

The Heart dataset contains 13 qualitative and quantitative predictors for 303 patients who sought medical attention due to chest pain. The response represents a binary classification scenario as we want to predict which patients have heart disease and which do not. 



