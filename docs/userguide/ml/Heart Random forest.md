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

The Heart dataset contains 13 qualitative and quantitative predictors for 303 patients who sought medical attention due to chest pain. The response represents a binary classification scenario as we want to predict which patients have heart disease and which do not. You can download the dataset <a href="https://github.com/jbsooter/tablesaw/blob/53358464317fbd66f8a50fe240b67765593a7659/data/Heart.csv">here</a>.

As usual, you will need to add the smile, tablesaw-core, and tablesaw-jsplot dependencies. (Described in Moneyball Tutorial)

First, load and clean the data. Qualitative features must be represented as integers to build the model. 

```
Table data = Table.read().csv("Heart.csv");

//encode qualitative features as integers to prepare for Smile model. 
data.replaceColumn("AHD", data.stringColumn("AHD").asDoubleColumn().asIntColumn());
data.replaceColumn("Thal", data.stringColumn("Thal").asDoubleColumn().asIntColumn());
data.replaceColumn("ChestPain", data.stringColumn("ChestPain").asDoubleColumn());

//Remove the index column, as it is not a feature
data.removeColumns("C0");

//Remove rows with missing values
data = data.dropRowsWithMissingValues();

//print out cleaned dataset for inspection
System.out.println(data.printAll());
```

Next, segment your dataset into two distinct tables. Rows are randomly assigned tables by default using the .sampleSplit() function.

```
//Split the data 70% test, 30% train
Table[] splitData = data.sampleSplit(0.7);
Table dataTrain = splitData[0];
Table dataTest = splitData[1];
```

Now build an initial RandomForest model using the training dataset and sensible parameter values. *m*, the number of features to be considered for each tree, is set to the square root of the number of features. *n*, or ntrees, is set to 50, a reasonmable starting point that will run quickly. *d*, or maxDepth, is the maximum depth of any individual decision tree, and is set to another generally accepted sensible value, 7. maxNodes is set to 100 and represents the most number of terminal decision making nodes a tree can have. All of these values are considered **Hyperparameters** and should be refined by the user to improve the accuracy of the model. 

```
//initial model with sensible parameters
RandomForest RFModel1 = smile.classification.RandomForest.fit(
     Formula.lhs("AHD"),
     dataTrain.smile().toDataFrame(),
     50, //n
     (int) Math.sqrt((double) (dataTrain.columnCount() - 1)), //m = sqrt(p)
     SplitRule.GINI,
     7, //d 
     100, //maxNodes
     1,
     1
);
```

View the first decision tree generated by the model. 

```
System.out.println(RFModel1.trees()[0]);
```

Predict the response of the test dataset using your model and assess model accuracy. 

```
//predict the response of test dataset with RFModel1
int[] predictions = RFModel1.predict(dataTest.smile().toDataFrame());

//evaluate % classification accuracy for RFModel1
double accuracy1 = Accuracy.of(dataTest.intColumn("AHD").asIntArray(), predictions);
System.out.println(accuracy1);
```

Generate and plot feature importance. According to the Smile documentation,  the built in .importance() function calculates variable importance as the sum decrease in the Gini impurity criterion across all node splits on that variable, averaged across all trees in the Random Forest. In other words, the more that impurity declines when a node is split on a variable, the more important it is to the predictive power of a model. 

```
//measure variable importance (mean decrease Gini Index)
double[] RFModel1_Importance = RFModel1.importance();
System.out.println(Arrays.toString(RFModel1_Importance));

//plot variable importance with tablesaw
Table varImportance = Table.create("featureImportance");
List<String> featureNames = dataTrain.columnNames();
featureNames.remove(13); //remove response
varImportance.addColumns(DoubleColumn.create("featureImportance", RFModel1_Importance), StringColumn.create("Feature",  featureNames));
varImportance = varImportance.sortDescendingOn("featureImportance");
Plot.show(HorizontalBarPlot.create("Feature Importance", varImportance, "Feature", "featureImportance"));
```








